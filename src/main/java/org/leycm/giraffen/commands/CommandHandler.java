package org.leycm.giraffen.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.leycm.giraffen.commands.impl.ClickGuiCommand;
import org.leycm.giraffen.commands.impl.ShortcutCommand;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CommandHandler {
    public static final CommandDispatcher<CommandSource> DISPATCHER = new CommandDispatcher<>();
    public static final Set<Command> COMMANDS = new HashSet<>();

    private String prefix;

    public CommandHandler() {
        init();
    }

    public void init() {
        register(new ShortcutCommand());
        register(new ClickGuiCommand());
    }

    private void register(Command command) {
        COMMANDS.add(command);
        DISPATCHER.register(command.build(LiteralArgumentBuilder.literal(command.getName())));
    }

    public void dispatch(String command) {
        try {
            DISPATCHER.execute(command, Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).getCommandSource());
        } catch (CommandSyntaxException e) {
            MinecraftClient.getInstance().player.sendMessage(Text.of("Invalid command. Type .help for a list of commands."), false);
        }
    }

    public String getPrefix() {
        return prefix != null ? prefix : ".";
    }
}
