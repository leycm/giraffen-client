package org.leycm.giraffe.client.module.modules.utils;

import org.leycm.giraffe.client.module.Modules;
import org.leycm.giraffe.client.module.common.BaseModule;


public class SkinChangerModule extends BaseModule {

    public SkinChangerModule() {
        super("Skin Changer", "utils", "skin-changer");

        register();
    }


    public static SkinChangerModule getInstance() {
        return (SkinChangerModule) Modules.getModule("skin-changer");
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

}