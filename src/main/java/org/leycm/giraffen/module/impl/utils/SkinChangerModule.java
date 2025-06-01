package org.leycm.giraffen.module.impl.utils;

import org.leycm.giraffen.module.Modules;
import org.leycm.giraffen.module.modules.BaseModule;


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