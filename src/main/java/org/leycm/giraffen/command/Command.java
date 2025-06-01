package org.leycm.giraffen.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.client.network.ClientCommandSource;

public record Command(
        String name,
        String desc,
        LiteralArgumentBuilder<ClientCommandSource> command
) {
    public static LiteralArgumentBuilder<ClientCommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public static <T> RequiredArgumentBuilder<ClientCommandSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }
}
