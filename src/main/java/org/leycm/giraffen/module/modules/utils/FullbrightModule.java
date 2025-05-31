package org.leycm.giraffen.module.modules.utils;

import org.leycm.giraffen.module.Modules;
import org.leycm.giraffen.module.common.BaseModule;
import org.leycm.giraffen.settings.Setting;
import org.leycm.giraffen.settings.fields.BooleanField;

public class FullbrightModule extends BaseModule {

    public FullbrightModule() {
        super("Fullbright", "utils", "fullbright");
        setDefaultData("remove.darkness.factor", true);

        setSetting(0, Setting.of("remove-darkness", config)
                .field(new BooleanField("remove.darkness.factor", true))
                .prefix("Remove Warden Darkness")
        );
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

    public static FullbrightModule getInstance() {
        return (FullbrightModule) Modules.getModule("fullbright");
    }

}
