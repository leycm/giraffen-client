package org.leycm.giraffen.module.modules.esp;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.WaterAnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.leycm.giraffen.module.Modules;
import org.leycm.giraffen.module.common.Module;
import org.leycm.giraffen.settings.Setting;
import org.leycm.giraffen.settings.fields.BooleanField;
import org.leycm.giraffen.settings.fields.ColorField;
import org.leycm.giraffen.settings.fields.DropDownField;

import java.util.Map;

public class EntityEspModule extends Module {

    public EntityEspModule() {
        super("Entity Esp", "esp", "entity-esp");

        setSetting(0, Setting.of("esp-type", config)
                .field(new DropDownField("esp.type", "glowing", Map.of("glowing", "Vanilla Glowing")))
        );

        setSetting(1, Setting.of("display-player", config)
                .field(new BooleanField("esp.groups.player.show", true))
                .field(new ColorField("esp.groups.player.color", "#FF0000"))
                .prefix("Player")
        );

        setSetting(2, Setting.of("display-water", config)
                .field(new BooleanField("esp.groups.water.show", false))
                .field(new ColorField("esp.groups.water.color", "#FFFFFF"))
                .prefix("Water")
        );

        setSetting(3, Setting.of("display-monster", config)
                .field(new BooleanField("esp.groups.monster.show", true))
                .field(new ColorField("esp.groups.monster.color", "#FFFFFF"))
                .prefix("Monster")
        );

        setSetting(4, Setting.of("display-passive", config)
                .field(new BooleanField("esp.groups.passive.show", false))
                .field(new ColorField("esp.groups.passive.color", "#FFFFFF"))
                .prefix("Passive")
        );

        setSetting(5, Setting.of("display-animal", config)
                .field(new BooleanField("esp.groups.animal.show", false))
                .field(new ColorField("esp.groups.animal.color", "#FFFFFF"))
                .prefix("Animal")
        );

        setSetting(6, Setting.of("display-default", config)
                .field(new BooleanField("esp.groups.default.show", false))
                .field(new ColorField("esp.groups.default.color", "#FFFFFF"))
                .prefix("Default")
        );

    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    public boolean shouldGlow(Entity entity) {
        if (!getData("esp.type", String.class, "glowing").equals("glowing") || !isRunning()) return false;

        return switch (entity) {
            case PlayerEntity playerEntity -> getData("esp.groups.player.show", Boolean.class, true);
            case Monster monster -> getData("esp.groups.monster.show", Boolean.class, true);
            case WaterAnimalEntity waterAnimalEntity -> getData("esp.groups.water.show", Boolean.class, false);
            case PassiveEntity passiveEntity -> getData("esp.groups.passive.show", Boolean.class, false);
            case null, default -> getData("esp.groups.default.show", Boolean.class, false);
        };
    }

    public int getColor(Entity entity) {
        if (!getData("esp.type", String.class, "glowing").equals("glowing") || !isRunning())
            return 0xFFFFFF;

        String colorHex = switch (entity) {
            case PlayerEntity playerEntity -> getData("esp.groups.player.color", String.class, "#FF0000");
            case Monster monster -> getData("esp.groups.monster.color", String.class, "#FFFFFF");
            case WaterAnimalEntity waterAnimalEntity -> getData("esp.groups.water.color", String.class, "#FFFFFF");
            case PassiveEntity passiveEntity -> getData("esp.groups.passive.color", String.class, "#FFFFFF");
            case null, default -> getData("esp.groups.default.color", String.class, "#FFFFFF");
        };

        try {
            return (int) Long.parseLong(colorHex.replace("#", ""), 16);
        } catch (NumberFormatException e) {
            return 0xFFFFFF;
        }
    }

    public static EntityEspModule getInstance() {
        return (EntityEspModule) Modules.getModule("entity-esp");
    }

}
