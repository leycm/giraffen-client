package org.leycm.giraffen.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;

public abstract class Command {
    private final String name;

    public Command(String name) {
        this.name = name;
    }

    public abstract LiteralArgumentBuilder<CommandSource> build(LiteralArgumentBuilder<CommandSource> builder);

    public String getName() {
        return this.name;
    }
}
