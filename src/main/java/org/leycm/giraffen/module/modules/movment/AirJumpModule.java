package org.leycm.giraffen.module.modules.movment;

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

    public static AirJumpModule getInstance() {
        return (AirJumpModule) Modules.getModule("air-jump");
    }
}
