package org.leycm.giraffen.module.modules.utils;

import org.leycm.giraffen.module.Modules;
import org.leycm.giraffen.module.common.Module;

public class FullbrightModule extends Module {

    public FullbrightModule() {
        super("Fullbright", "utils", "fullbright");
        setDefaultSetting("remove.darkness.factor", true);
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

    public static Module getInstance() {
        return Modules.getModule("fullbright");
    }

}
