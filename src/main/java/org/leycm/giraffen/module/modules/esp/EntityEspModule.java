package org.leycm.giraffen.module.modules.esp;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.WaterAnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.leycm.giraffen.module.Modules;
import org.leycm.giraffen.module.common.Module;

public class EntityEspModule extends Module {

    public EntityEspModule() {
        super("Entity Esp", "esp", "entity-esp");

        setDefaultSetting("esp.type", 0);

        // Farb-Einstellungen
        setDefaultSetting("esp.groups.player.color", 0xFF0000);
        setDefaultSetting("esp.groups.water.color", 0xFFFFFF);
        setDefaultSetting("esp.groups.monster.color", 0xFFFFFF);
        setDefaultSetting("esp.groups.passive.color", 0xFFFFFF);
        setDefaultSetting("esp.groups.animal.color", 0xFFFFFF);
        setDefaultSetting("esp.groups.default.color", 0xFFFFFF);

        // Sichtbarkeits-Einstellungen
        setDefaultSetting("esp.groups.player.show", true);
        setDefaultSetting("esp.groups.monster.show", true);
        setDefaultSetting("esp.groups.water.show", false);
        setDefaultSetting("esp.groups.passive.show", false);
        setDefaultSetting("esp.groups.animal.show", false);
        setDefaultSetting("esp.groups.default.show", false);
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    public boolean shouldGlow(Entity entity) {
        if (getSetting("esp.type", Integer.class, 0) != 0 || !isRunning()) return false;

        return switch (entity) {
            case PlayerEntity playerEntity -> getSetting("esp.groups.player.show", Boolean.class, true);
            case Monster monster -> getSetting("esp.groups.monster.show", Boolean.class, true);
            case WaterAnimalEntity waterAnimalEntity -> getSetting("esp.groups.water.show", Boolean.class, false);
            case PassiveEntity passiveEntity -> getSetting("esp.groups.passive.show", Boolean.class, false);
            case null, default -> getSetting("esp.groups.default.show", Boolean.class, false);
        };
    }

    public int getColor(Entity entity) {
        if (getSetting("esp.type", Integer.class, 0) != 0 || !isRunning()) return 0xFFFFFF;

        return switch (entity) {
            case PlayerEntity playerEntity -> getSetting("esp.groups.player.color", Integer.class, 0xFF0000);
            case Monster monster -> getSetting("esp.groups.monster.color", Integer.class, 0xFFFFFF);
            case WaterAnimalEntity waterAnimalEntity -> getSetting("esp.groups.water.color", Integer.class, 0xFFFFFF);
            case PassiveEntity passiveEntity -> getSetting("esp.groups.passive.color", Integer.class, 0xFFFFFF);
            case null, default -> getSetting("esp.groups.default.color", Integer.class, 0xFFFFFF);
        };
    }

    public static EntityEspModule getInstance() {
        return (EntityEspModule) Modules.getModule("entity-esp");
    }

}
