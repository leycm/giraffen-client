package org.leycm.giraffen.module.modules.movment;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.leycm.giraffen.module.Modules;
import org.leycm.giraffen.module.common.Module;

public class AirJumpModule extends Module {

    public AirJumpModule() {
        super("Air Jump", "movement", "air-jump");

    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    private void onJump() {
        if(!isRunning()) return;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        setDefaultData("", true);
    }

    public static Module getInstance() {
        return Modules.getModule("air-jump");
    }
}
