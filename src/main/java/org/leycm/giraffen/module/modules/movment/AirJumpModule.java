package org.leycm.giraffen.module.modules.movment;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.leycm.giraffen.module.Modules;
import org.leycm.giraffen.module.common.BaseModule;

public class AirJumpModule extends BaseModule {

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

    public static BaseModule getInstance() {
        return Modules.getModule("air-jump");
    }
}
