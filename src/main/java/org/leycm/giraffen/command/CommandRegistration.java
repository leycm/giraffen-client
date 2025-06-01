package org.leycm.giraffen.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommandRegistration {

    public static final CommandDispatcher<ClientCommandSource> DISPATCHER = new CommandDispatcher<>();
    public static final List<Command> commands = new ArrayList<>();

    private static ClientCommandSource source;

    public static void register(LiteralArgumentBuilder<ClientCommandSource> command) {
        commands.add(new Command(command.getLiteral(), "No desc for the Command", command));
        DISPATCHER.register(command);
    }

    public static void init() {
        source = MinecraftClient.getInstance().getNetworkHandler().getCommandSource();
        DISPATCHER.register(
                LiteralArgumentBuilder.<ClientCommandSource>literal("help")
                        .executes(ctx -> {
                            assert MinecraftClient.getInstance().player != null;
                            MinecraftClient.getInstance().player.sendMessage(Text.literal("Hey! here is your help (°_°)"), false);
                            for (Command command : commands) {
                                MinecraftClient.getInstance().player.sendMessage(Text.literal(" §l§b> §7.§f" + command.name() + "§b - §7" + command.desc()), false);
                            }
                            return 1;
                        })
        );
    }

    public static boolean tryExecute(@NotNull String message) {
        if (!message.startsWith(".")) return false;
        String command = message.substring(1);
        try {
            DISPATCHER.execute(command, source);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public static CompletableFuture<Suggestions> suggestions(StringReader input, int cursor) {
        return DISPATCHER.getCompletionSuggestions(
                DISPATCHER.parse(input, source), cursor
        );
    }

    public static String getPrefix() {
        return "."; // TODO : Add config for command prefix
    }
}

