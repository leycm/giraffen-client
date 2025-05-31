package org.leycm.giraffen.commands.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.leycm.giraffen.GiraffenClient;
import org.leycm.giraffen.commands.Command;

public class ShortcutCommand extends Command {
    public ShortcutCommand() {
        super("sc");
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> build(LiteralArgumentBuilder<CommandSource> builder) {
        LiteralArgumentBuilder<CommandSource> gmCommand = LiteralArgumentBuilder.literal("gm");

        gmCommand.then(((LiteralArgumentBuilder<CommandSource>) (Object) LiteralArgumentBuilder.literal("0"))
                .executes(context -> {
                    if(GiraffenClient.MC.interactionManager.getCurrentGameMode() == GameMode.SURVIVAL) {
                        GiraffenClient.MC.player.sendMessage(Text.of("You are already in survival!"), false);
                        return 1;
                    }

                    GiraffenClient.MC.player.networkHandler.sendChatCommand("gamemode survival");
                    GiraffenClient.MC.player.sendMessage(Text.of("Changed your gamemode to §aSURVIVAL§7."), false);
                    return 1;
                })
        );

        gmCommand.then(((LiteralArgumentBuilder<CommandSource>) (Object) LiteralArgumentBuilder.literal("1"))
                .executes(context -> {
                    if(GiraffenClient.MC.interactionManager.getCurrentGameMode() == GameMode.CREATIVE) {
                        GiraffenClient.MC.player.sendMessage(Text.of("You are already in creative!"), false);
                        return 1;
                    }

                    GiraffenClient.MC.player.networkHandler.sendChatCommand("gamemode creative");
                    GiraffenClient.MC.player.sendMessage(Text.of("Changed your gamemode to §aCREATIVE§7."), false);
                    return 1;
                })
        );

        gmCommand.then(((LiteralArgumentBuilder<CommandSource>) (Object) LiteralArgumentBuilder.literal("2"))
                .executes(context -> {
                    if(GiraffenClient.MC.interactionManager.getCurrentGameMode() == GameMode.ADVENTURE) {
                        GiraffenClient.MC.player.sendMessage(Text.of("You are already in adventure!"), false);
                        return 1;
                    }

                    GiraffenClient.MC.player.networkHandler.sendChatCommand("gamemode adventure");
                    GiraffenClient.MC.player.sendMessage(Text.of("Changed your gamemode to §aADVENTURE§7."), false);
                    return 1;
                })
        );

        gmCommand.then(((LiteralArgumentBuilder<CommandSource>) (Object) LiteralArgumentBuilder.literal("3"))
                .executes(context -> {
                    if(GiraffenClient.MC.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
                        GiraffenClient.MC.player.sendMessage(Text.of("You are already in spectator!"), false);
                        return 1;
                    }

                    GiraffenClient.MC.player.networkHandler.sendChatCommand("gamemode spectator");
                    GiraffenClient.MC.player.sendMessage(Text.of("Changed your gamemode to §aSPECTATOR§7."), false);
                    return 1;
                })
        );

        builder.then(gmCommand);

        return builder;
    }

    private void sendFeedback(Text message) {
        if (GiraffenClient.MC.player != null) {
            GiraffenClient.MC.player.sendMessage(message, false);
        }
    }
}