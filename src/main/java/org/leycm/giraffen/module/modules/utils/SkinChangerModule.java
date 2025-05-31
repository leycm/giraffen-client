package org.leycm.giraffen.module.modules.utils;

import org.leycm.giraffen.module.Modules;
import org.leycm.giraffen.module.common.Module;


public class SkinChangerModule extends Module {

    public SkinChangerModule() {
        super("Skin Changer", "utils", "skin-changer");
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