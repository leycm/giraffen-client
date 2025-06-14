package org.leycm.giraffe.client.module.modules.esp;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.WaterAnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.leycm.giraffe.client.module.Modules;
import org.leycm.giraffe.client.module.common.BaseModule;
import org.leycm.giraffe.client.settings.Group;
import org.leycm.giraffe.client.settings.Setting;
import org.leycm.giraffe.client.settings.fields.BooleanField;
import org.leycm.giraffe.client.settings.fields.RgbColorField;
import org.leycm.giraffe.client.settings.fields.DropDownField;

import java.util.Map;

public class EntityEspModule extends BaseModule {

    public EntityEspModule() {
        super("Entity Esp", "esp", "entity-esp");

        setSetting(0, Setting.of("esp-type", config)
                .field(new DropDownField("esp.type", "glowing", Map.of("glowing", "Vanilla Glowing")))
        );

        setSetting(1, Setting.of("player", config)
                .field(new BooleanField("esp.groups.player.show", true))
                .field(new RgbColorField("esp.groups.player.color", "#FF0000"))
                .group(new Group("display", "Display"))
                .prefix("Player")
        );

        setSetting(2, Setting.of("water", config)
                .field(new BooleanField("esp.groups.water.show", false))
                .field(new RgbColorField("esp.groups.water.color", "#FFFFFF"))
                .group(new Group("display", "Display"))
                .prefix("Water")
        );

        setSetting(3, Setting.of("monster", config)
                .field(new BooleanField("esp.groups.monster.show", true))
                .field(new RgbColorField("esp.groups.monster.color", "#FFFFFF"))
                .group(new Group("display", "Display"))
                .prefix("Monster")
        );

        setSetting(4, Setting.of("animal", config)
                .field(new BooleanField("esp.groups.animal.show", false))
                .field(new RgbColorField("esp.groups.animal.color", "#FFFFFF"))
                .group(new Group("display", "Display"))
                .prefix("Animal")
        );

        setSetting(5, Setting.of("passive", config)
                .field(new BooleanField("esp.groups.passive.show", false))
                .field(new RgbColorField("esp.groups.passive.color", "#FFFFFF"))
                .group(new Group("display", "Display"))
                .prefix("Passive")
        );

        setSetting(6, Setting.of("default", config)
                .field(new BooleanField("esp.groups.default.show", false))
                .field(new RgbColorField("esp.groups.default.color", "#FFFFFF"))
                .group(new Group("display", "Display"))
                .prefix("Default")
        );

        register();
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    public boolean shouldGlow(Entity entity) {

        return switch (entity) {
            case PlayerEntity player -> getData("esp.groups.player.show", Boolean.class, true);
            case Monster monster -> getData("esp.groups.monster.show", Boolean.class, true);
            case WaterAnimalEntity animal -> getData("esp.groups.water.show", Boolean.class, false);
            case AnimalEntity animal -> getData("esp.groups.animal.show", Boolean.class, false);
            case PassiveEntity passive -> getData("esp.groups.passive.show", Boolean.class, false);
            case null, default -> getData("esp.groups.default.show", Boolean.class, false);
        };

    }

    public int getColor(Entity entity) {

        String colorHex = switch (entity) {
            case PlayerEntity player -> getData("esp.groups.player.color", String.class, "#FF0000");
            case Monster monster -> getData("esp.groups.monster.color", String.class, "#FFFFFF");
            case WaterAnimalEntity animal -> getData("esp.groups.water.color", String.class, "#FFFFFF");
            case AnimalEntity animal -> getData("esp.groups.animal.color", String.class, "#FFFFFF");
            case PassiveEntity passive -> getData("esp.groups.passive.color", String.class, "#FFFFFF");
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
