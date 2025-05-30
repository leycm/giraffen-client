package org.leycm.giraffen.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.leycm.giraffen.module.Modules;
import org.leycm.giraffen.module.common.Module;

public class ModuleCommand {

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("module")
                    .then(argument("module", StringArgumentType.word())
                            .suggests(MODULE_SUGGESTIONS)
                            .then(literal("toggle").executes(ctx -> withModule(ctx, Module::toggle)))
                            .then(literal("save").executes(ctx -> withModule(ctx, Module::saveSettings)))
                            .then(literal("reload").executes(ctx -> withModule(ctx, Module::reloadSettings)))
                    )
            );
        });
    }


    private static int withModule(CommandContext<FabricClientCommandSource> ctx, java.util.function.Consumer<Module> action) {
        String moduleName = StringArgumentType.getString(ctx, "module");
        Module module = Modules.getModule(moduleName);
        if (module == null) {
            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().player.sendMessage(Text.literal("Unknown module: " + moduleName), false);
            return 0;
        }
        action.accept(module);
        return 1;
    }

    private static final SuggestionProvider<FabricClientCommandSource> MODULE_SUGGESTIONS = (ctx, builder) -> {
        for (String id : Modules.getModuleIds()) {
            builder.suggest(id);
        }
        return builder.buildFuture();
    };
}
