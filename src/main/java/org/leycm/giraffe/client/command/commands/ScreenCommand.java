package org.leycm.giraffe.client.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.client.network.ClientCommandSource;
import org.leycm.giraffe.client.command.CommandRegistration;
import org.leycm.giraffe.client.ui.ScreenHandler;

import static org.leycm.giraffe.client.command.GiraffenCommand.argument;
import static org.leycm.giraffe.client.command.GiraffenCommand.literal;

public class ScreenCommand {
    public static void register() {
        CommandRegistration.register(
                literal("screen")
                        .then(argument("screen-id", StringArgumentType.word())
                                .suggests(SCREEN_SUGGESTIONS)
                                .executes(ctx -> {
                                    String screenId = StringArgumentType.getString(ctx, "screen-id");
                                    ScreenHandler.openUi(screenId);
                                    return 0;
                                })
                        )
                        .executes(ctx -> {
                            ScreenHandler.openUi("test-screen");
                            return 0;
                        })
        );
    }

    private static final SuggestionProvider<ClientCommandSource> SCREEN_SUGGESTIONS = (ctx, builder) -> {
        String input = builder.getRemaining().toLowerCase();
        for (String id : ScreenHandler.getScreenIds()) {
            if (id.toLowerCase().startsWith(input)) {
                builder.suggest(id);
            }
        }
        return builder.buildFuture();
    };

}
