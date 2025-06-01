package org.leycm.giraffen.module.common;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.network.ClientCommandSource;
import org.jetbrains.annotations.NotNull;
import org.leycm.giraffen.Client;
import org.leycm.giraffen.command.CommandRegistration;
import org.leycm.giraffen.module.Modules;
import org.leycm.giraffen.settings.Setting;
import org.leycm.giraffen.utlis.ChatUtil;
import org.leycm.storage.StorageBase;
import org.leycm.storage.impl.JavaStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.leycm.giraffen.command.Command.argument;
import static org.leycm.giraffen.command.Command.literal;


public abstract class BaseModule {
    private final String displayName;
    private final String id;
    protected final StorageBase config;

    private final List<Setting> settings = new ArrayList<>();

    protected boolean running;

    public BaseModule(String displayName, String category, String id) {
        Modules.instances.put(id, this);

        this.displayName = displayName;
        this.id = id;
        this.config = JavaStorage.of("modules/" + category + "/" + id, StorageBase.Type.JSON, JavaStorage.class);
        register();
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
    public String getDisplayName() {return displayName;}

    public boolean isRunning() {return running;}
    public void saveSettings() {config.save();}
    public void reloadSettings() {config.reload();}

    protected void setSetting(int index, Setting setting) {settings.add(index, setting);}
    protected Setting getSetting(int index) {return settings.get(index);}
    protected List<Setting> getSettings() {return settings;}

    public void register() {
        CommandRegistration.register(literal(id)
                .then(literal("settings")
                        .then(argument("setting", StringArgumentType.word())
                                .suggests(SETTING_SUGGESTIONS)
                                .executes(this::showSettingInfo)
                                .then(buildDynamicArguments())
                        )
                )
        );
    }

    private ArgumentBuilder<ClientCommandSource, ?> buildDynamicArguments() {
        int maxFields = settings.stream().mapToInt(Setting::size).max().orElse(1);
        return buildArgumentForField(0, maxFields);
    }

    private ArgumentBuilder<ClientCommandSource, ?> buildArgumentForField(int fieldIndex, int maxFields) {
        ArgumentBuilder<ClientCommandSource, ?> currentArg =
                argument("field" + fieldIndex, StringArgumentType.string()) // TODO :
                        .suggests((ctx, builder) -> createFieldSuggestions(ctx, builder, fieldIndex))
                        .executes(ctx -> executeCommand(ctx, fieldIndex));

        if (fieldIndex < maxFields - 1) {
            currentArg = currentArg.then(buildArgumentForField(fieldIndex + 1, maxFields));
        }

        return currentArg;
    }

    private CompletableFuture<Suggestions> createFieldSuggestions(
            CommandContext<ClientCommandSource> ctx,
            SuggestionsBuilder builder,
            int fieldIndex) {

        try {
            String settingId = StringArgumentType.getString(ctx, "setting");
            Setting setting = findSettingById(settingId);

            if (setting != null && fieldIndex < setting.size()) {
                String userInput = builder.getInput().substring(builder.getStart());

                String[] suggestions = setting.toTabCompleter(fieldIndex, userInput);
                if (suggestions != null) {
                    for (String suggestion : suggestions) {
                        builder.suggest(suggestion);
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return builder.buildFuture();
    }

    private int executeCommand(CommandContext<ClientCommandSource> ctx, int currentFieldIndex) {
        try {
            String settingId = StringArgumentType.getString(ctx, "setting");
            Setting setting = findSettingById(settingId);

            if (setting == null) {
                ChatUtil.sendMessage("Setting \"" + settingId + "\" nicht gefunden!", ChatUtil.Type.ERROR);
                return 0;
            }

            List<String> arguments = new ArrayList<>();
            for (int i = 0; i <= currentFieldIndex; i++) {
                try {
                    String arg = StringArgumentType.getString(ctx, "field" + i);

                    if (i < setting.size()) {
                        if (!setting.isValidInput(i, arg)) {
                            ChatUtil.sendMessage(
                                    "Ungültiger Wert \"" + arg + "\" für Feld " + i + ". " +
                                            "Erlaubte Werte: " + String.join(", ", setting.toTabCompleter(i, arg)),
                                    ChatUtil.Type.ERROR
                            );
                            return 0;
                        }
                        arguments.add(arg);
                    }
                } catch (IllegalArgumentException e) {
                    break;
                }
            }

            Client.LOGGER.info("Setting size: " + setting.size() + ", Arguments collected: " + arguments.size() + ", Current field index: " + currentFieldIndex);

            if (arguments.size() == setting.size()) {
                for (int i = 0; i < arguments.size(); i++) {
                    setting.assign(i, arguments.get(i));
                }

                ChatUtil.sendMessage(
                        "Setting \"" + settingId + "\" erfolgreich aktualisiert!",
                        ChatUtil.Type.SUCCESS
                );

                return Command.SINGLE_SUCCESS;
            } else if (arguments.size() < setting.size()) {
                int nextFieldIndex = arguments.size();
                if (nextFieldIndex < setting.size()) {
                    String[] nextOptions = setting.toTabCompleter(nextFieldIndex, "");
                    String optionsText = nextOptions != null ? String.join(", ", nextOptions) : "keine Optionen verfügbar";

                    ChatUtil.sendMessage(
                            "Nächstes Argument für Feld " + nextFieldIndex + " benötigt. " +
                                    "Verfügbare Optionen: " + optionsText,
                            ChatUtil.Type.INFO
                    );
                } else {
                    ChatUtil.sendMessage(
                            "Nicht genug Argumente! Benötigt: " + setting.size() + ", erhalten: " + arguments.size(),
                            ChatUtil.Type.ERROR
                    );
                }

                return Command.SINGLE_SUCCESS;
            } else {
                ChatUtil.sendMessage(
                        "Zu viele Argumente! Setting \"" + settingId + "\" benötigt nur " + setting.size() + " Argumente.",
                        ChatUtil.Type.ERROR
                );
                return 0;
            }

        } catch (Exception e) {
            ChatUtil.sendMessage("Fehler beim Ausführen des Commands: " + e.getMessage(), ChatUtil.Type.ERROR);
            Client.LOGGER.error("Command execution error", e);
            return 0;
        }
    }

    private int showSettingInfo(CommandContext<ClientCommandSource> ctx) {
        String settingId = StringArgumentType.getString(ctx, "setting");
        Setting setting = findSettingById(settingId);

        if (setting != null) {
            StringBuilder info = new StringBuilder("Setting \"" + settingId + "\" (benötigt " + setting.size() + " Argumente):\n");
            for (int i = 0; i < setting.size(); i++) {
                String[] options = setting.toTabCompleter(i, "");
                String optionsText = options != null ? String.join(", ", options) : "keine Optionen";
                info.append("  Feld ").append(i).append(": ").append(optionsText).append("\n");
            }
            ChatUtil.sendMessage(info.toString(), ChatUtil.Type.INFO);
        } else {
            ChatUtil.sendMessage("Setting \"" + settingId + "\" nicht gefunden!", ChatUtil.Type.ERROR);
        }

        return Command.SINGLE_SUCCESS;
    }

    private Setting findSettingById(String id) {
        return settings.stream()
                .filter(setting -> setting.getId().equals(id) && setting.isAccessible())
                .findFirst()
                .orElse(null);
    }

    private final SuggestionProvider<ClientCommandSource> SETTING_SUGGESTIONS =
            (ctx, builder) -> {
                String arg = builder.getRemaining().toLowerCase();

                for (Setting setting : settings) {
                    if (setting.isAccessible() && setting.getId().startsWith(arg)) builder.suggest(setting.getId());
                }

                return builder.buildFuture();
            };
}