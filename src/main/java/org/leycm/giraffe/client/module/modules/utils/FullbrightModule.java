package org.leycm.giraffe.client.module.modules.utils;

import org.leycm.giraffe.client.module.Modules;
import org.leycm.giraffe.client.module.common.BaseModule;
import org.leycm.giraffe.client.settings.Setting;
import org.leycm.giraffe.client.settings.fields.BooleanField;

public class FullbrightModule extends BaseModule {

    public FullbrightModule() {
        super("Fullbright", "utils", "fullbright");
        setDefaultData("remove.darkness.factor", true);

        setSetting(0, Setting.of("remove-darkness", config)
                .field(new BooleanField("remove.darkness.factor", true))
                .prefix("Remove Warden Darkness")
        );

        register();
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
