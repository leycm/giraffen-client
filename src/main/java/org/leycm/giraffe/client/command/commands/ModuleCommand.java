package org.leycm.giraffe.client.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.text.Text;
import org.leycm.giraffe.client.command.CommandRegistration;
import org.leycm.giraffe.client.module.Modules;
import org.leycm.giraffe.client.module.common.BaseModule;

import static org.leycm.giraffe.client.command.GiraffenCommand.argument;
import static org.leycm.giraffe.client.command.GiraffenCommand.literal;

public class ModuleCommand {

    public static void register() {
            CommandRegistration.register(literal("module")
                    .then(argument("module", StringArgumentType.word())
                            .suggests(MODULE_SUGGESTIONS)
                            .then(literal("toggle").executes(ctx -> withModule(ctx, BaseModule::toggle)))
                            .then(literal("save").executes(ctx -> withModule(ctx, BaseModule::saveSettings)))
                            .then(literal("reload").executes(ctx -> withModule(ctx, BaseModule::reloadSettings)))
                    )
            );
    }


    private static int withModule(CommandContext<ClientCommandSource> ctx, java.util.function.Consumer<BaseModule> action) {
        String moduleName = StringArgumentType.getString(ctx, "module");
        BaseModule module = Modules.getModule(moduleName);
        if (module == null) {
            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().player.sendMessage(Text.literal("Unknown module: " + moduleName), false);
            return 0;
        }
        action.accept(module);
        return 1;
    }

    private static final SuggestionProvider<ClientCommandSource> MODULE_SUGGESTIONS = (ctx, builder) -> {
        for (String id : Modules.getModuleIds()) {
            builder.suggest(id);
        }
        return builder.buildFuture();
    };
}
