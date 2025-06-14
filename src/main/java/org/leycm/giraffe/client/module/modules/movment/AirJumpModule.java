package org.leycm.giraffe.client.module.modules.movment;

import org.leycm.giraffe.client.module.Modules;
import org.leycm.giraffe.client.module.common.BaseModule;

public class AirJumpModule extends BaseModule {

    public AirJumpModule() {
        super("Air Jump", "movement", "air-jump");
        register();
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
