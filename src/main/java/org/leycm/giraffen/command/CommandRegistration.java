package org.leycm.giraffen.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.leycm.giraffen.Client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommandRegistration {

    public static final CommandDispatcher<ClientCommandSource> DISPATCHER = new CommandDispatcher<>();
    private static final List<Command> commands = new ArrayList<>();
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
                            Client.MC.player.sendMessage(Text.literal("Hey! here is your help (°_°)"), false);
                            commands.stream()
                                    .sorted(Comparator.comparing(Command::name))
                                    .forEach(cmd -> {
                                        Client.MC.player.sendMessage(Text.literal(" §l§b> §7." + cmd.name() + "§b - §7" + cmd.desc()), false);
                                    });
                            return 1;
                        })
        );
    }

    public static boolean execute(@NotNull String message) {
        if (!message.startsWith(getPrefix())) return false;

        StringReader command = prepareReader(message);

        try {
            int result = DISPATCHER.execute(command, source);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
    public static CompletableFuture<Suggestions> suggestions(StringReader reader, int cursor) {
        return DISPATCHER.getCompletionSuggestions(parse(reader), cursor);
    }

    public static CompletableFuture<Suggestions> suggestions(ParseResults<ClientCommandSource> parse, int cursor) {
        return DISPATCHER.getCompletionSuggestions(parse, cursor);
    }

    public static ParseResults<ClientCommandSource> parse(StringReader message) {
        StringReader command = prepareReader(message);
        return DISPATCHER.parse(command, source);
    }

    @Contract(pure = true)
    public static @NotNull String getPrefix() {
        return "."; // TODO : Configurability
    }

    public static @NotNull StringReader prepareReader(@NotNull String input) {
        StringReader reader = new StringReader(input);
        return prepareReader(reader);
    }

    public static @NotNull StringReader prepareReader(@NotNull StringReader reader) {
        String prefix = getPrefix();
        int cursor = reader.getCursor();
        if (reader.canRead(prefix.length()) && reader.getString().startsWith(prefix, cursor)) {
            reader.setCursor(cursor + prefix.length());
        }
        return reader;
    }

    @Contract(pure = true)
    public static @NotNull @Unmodifiable List<Command> getCommands() {
        return List.copyOf(commands);
    }
}