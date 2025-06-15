package org.leycm.giraffe.client.module.common;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.network.ClientCommandSource;
import org.jetbrains.annotations.NotNull;
import org.leycm.giraffe.client.command.CommandRegistration;
import org.leycm.giraffe.client.module.Modules;
import org.leycm.giraffe.client.settings.Field;
import org.leycm.giraffe.client.settings.Group;
import org.leycm.giraffe.client.settings.Setting;
import org.leycm.giraffe.client.utlis.ChatUtil;
import org.leycm.storage.StorageBase;
import org.leycm.storage.impl.JavaStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.leycm.giraffe.client.command.GiraffenCommand.literal;


public abstract class BaseModule {
    private final String displayName;
    private final String id;
    private final String category;
    protected final StorageBase config;

    private final List<Setting> settings = new ArrayList<>();

    protected boolean running;

    public BaseModule(String displayName, String category, String id) {
        Modules.instances.put(id, this);

        this.displayName = displayName;
        this.id = id;
        this.category = category;
        this.config = JavaStorage.of("modules/" + category + "/" + id, StorageBase.Type.JSON, JavaStorage.class);
    }

    public boolean enable() {
        if(!running) running = true;
        Modules.onModuleEnabled(id);
        onEnable();
        return true;
    }

    public boolean disable() {
        if(running) running = false;
        Modules.onModuleDisabled(id);
        onDisable();
        return false;
    }

    public void toggle() {running = !running ? enable() : disable();}

    protected abstract void onEnable();
    protected abstract void onDisable();

    public void setData(String key, Object value) {config.set(key, value);}
    public void setDefaultData(String key, @NotNull Object value) {if (config.get(key, value.getClass()) == null) config.set(key, value);}
    public <T> T getData(String key, Class<T> valueClass, T value) {return config.get(key, valueClass, value);}
    public <T> T getData(String key, Class<T> valueClass) {return config.get(key, valueClass);}
    public StorageBase getConfig() {return config;}

    public String getId() {return id;}
    public String getCategory() {return category;}
    public String getDisplayName() {return displayName;}

    public boolean isRunning() {return running;}
    public void saveSettings() {config.save();}
    public void reloadSettings() {config.reload();}

    protected void setSetting(int index, Setting setting) {settings.add(index, setting);}
    protected Setting getSetting(int index) {return settings.get(index);}
    public List<Setting> getSettings() {return settings;}

    public void register() {
        CommandRegistration.register(literal(id)
                .then(settingsTab())
                .then(toggleTab())
        );
    }

    // Toggle
    public LiteralArgumentBuilder<ClientCommandSource> toggleTab() {
        LiteralArgumentBuilder<ClientCommandSource> builder = LiteralArgumentBuilder.literal("toggle");
        builder.executes(ctx -> {
            toggle();
            return 1;
        });
        return builder;
    }

    // Settings
    public LiteralArgumentBuilder<ClientCommandSource> settingsTab() {
        LiteralArgumentBuilder<ClientCommandSource> builder = LiteralArgumentBuilder.literal("settings");

        Set<Group> groups = new HashSet<>();

        for(Setting setting : settings) {
            if(setting.getGroup() == null) {
                builder.then(settingTabOf(setting));
            } else if(!groups.contains(setting.getGroup())) {
                builder.then(settingGroupTabOf(setting.getGroup()));
                groups.add(setting.getGroup());
            }
        }

        return builder;
    }

    private @NotNull LiteralArgumentBuilder<ClientCommandSource> settingGroupTabOf(@NotNull Group group) {
        LiteralArgumentBuilder<ClientCommandSource> builder = LiteralArgumentBuilder.literal(group.id());

        for(Setting setting : settings) {
            if(setting.getGroup() == null) continue;
            if(!setting.getGroup().id().equals(group.id())) continue;
            builder.then(settingTabOf(setting));
            builder.executes(ctx -> {
                ChatUtil.sendMessage("Please use", ChatUtil.Type.ERROR);
                return 0;
            });
        }

        return builder;
    }

    private @NotNull LiteralArgumentBuilder<ClientCommandSource> settingTabOf(@NotNull Setting setting) {
        LiteralArgumentBuilder<ClientCommandSource> builder = LiteralArgumentBuilder.literal(setting.getId());

        List<RequiredArgumentBuilder<ClientCommandSource, ?>> fieldArguments = new ArrayList<>();

        for (Field<?> field : setting.getFields()) {
            fieldArguments.add(settingField(setting, field));
        }

        if (!fieldArguments.isEmpty()) {
            fieldArguments.getLast().executes(ctx -> {
                for (int i = 0; i < setting.getFields().size(); i++) {
                    Field<?> field = setting.getFields().get(i);
                    setting.assign(i, ctx.getArgument(field.getKey(), String.class));
                }

                ChatUtil.sendMessage(
                        "Setting §6" + setting.getPrefix() + "§7 successfully updated to §6" +
                                setting.getFields().stream()
                                        .map(Field::parseToStr)
                                        .collect(Collectors.joining("§7, §6")) + "§7!",
                        ChatUtil.Type.INFO
                );
                return 1;
            });
        }

        for (int arg = fieldArguments.size() - 1; arg == 1; arg--) {
            RequiredArgumentBuilder<ClientCommandSource, ?> child = fieldArguments.get(arg);
            RequiredArgumentBuilder<ClientCommandSource, ?> parent = fieldArguments.get(arg - 1);
            parent.then(child);
        }

        if (!fieldArguments.isEmpty()) {
            builder.then(fieldArguments.getFirst());
        }

        return builder;
    }

    private @NotNull RequiredArgumentBuilder<ClientCommandSource, ?> settingField(Setting setting, @NotNull Field<?> field) {
        RequiredArgumentBuilder<ClientCommandSource, ?> builder = RequiredArgumentBuilder.argument(field.getKey(), field.toArgumentType());
        builder.suggests((ctx, suggestBuilder) -> createFieldSuggestions(ctx, suggestBuilder, field));
        builder.executes(ctx -> {
            List<String> fieldStates = new ArrayList<>();

            for (Field<?> f : setting.getFields()) {
                boolean valid;
                try {
                    ctx.getArgument(f.getKey(), String.class);
                    valid = false;
                } catch (Exception e) {valid = true;}

                fieldStates.add(valid ? "§c!" + f.getDefaultValue() + "§7" : "§6" + f.getValue() + "§7");
            }

            ChatUtil.sendMessage(
                    "§cNot all values for setting §6" + setting.getPrefix() + "§c were specified §7[" +
                            String.join("§7, ", fieldStates) + "§7]",
                    ChatUtil.Type.ERROR
            );

            return 0;
        });

        return builder;
    }

    private CompletableFuture<Suggestions> createFieldSuggestions(
            CommandContext<ClientCommandSource> ctx,
            @NotNull SuggestionsBuilder builder,
            @NotNull Field<?> field) {

            String userInput = builder.getInput().substring(builder.getStart());

            String[] suggestions = field.toTabCompleter(userInput);
            if (suggestions != null) {
                for (String suggestion : suggestions) {
                    builder.suggest(suggestion);
                }
            }

        return builder.buildFuture();
    }

}

