package org.leycm.giraffen.commands.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.leycm.giraffen.commands.Command;
import org.leycm.giraffen.module.Modules;
import org.leycm.giraffen.module.common.BaseModule;
import org.leycm.giraffen.ui.ScreenHandler;

public class ClickGuiCommand extends Command {

    public ClickGuiCommand() {
        super("clickgui");
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(ctx -> {
                    ScreenHandler.openUi("clickgui");
                    return 1;
                }).then(RequiredArgumentBuilder.<CommandSource, String>argument("module", StringArgumentType.word())
                        .suggests(MODULE_SUGGESTIONS)
                        .then(LiteralArgumentBuilder.<CommandSource>literal("toggle").executes(ctx -> withModule(ctx, BaseModule::toggle)))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("save").executes(ctx -> withModule(ctx, BaseModule::saveSettings)))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("reload").executes(ctx -> withModule(ctx, BaseModule::reloadSettings)))
                );
        return builder;
    }

    private int withModule(CommandContext<CommandSource> ctx, java.util.function.Consumer<BaseModule> action) {
        String moduleName = StringArgumentType.getString(ctx, "module");
        BaseModule module = Modules.getModule(moduleName);

        if (module == null) {
            MinecraftClient.getInstance().player.sendMessage(Text.literal("Unknown module: " + moduleName), false);
            return 0;
        }

        action.accept(module);
        return 1;
    }

    private static final SuggestionProvider<CommandSource> MODULE_SUGGESTIONS = (ctx, builder) -> {
        for (String id : Modules.getModuleIds()) {
            builder.suggest(id);
        }
        return builder.buildFuture();
    };
}