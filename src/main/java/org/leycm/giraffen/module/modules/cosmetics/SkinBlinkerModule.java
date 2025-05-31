package org.leycm.giraffen.module.modules.cosmetics;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerModelPart;
import org.leycm.giraffen.module.Modules;
import org.leycm.giraffen.module.common.Module;
import org.leycm.giraffen.module.common.ThreadModule;
import org.leycm.giraffen.settings.Setting;
import org.leycm.giraffen.settings.fields.BooleanField;
import org.leycm.giraffen.settings.fields.DropDownField;
import org.leycm.giraffen.settings.fields.IntegerField;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SkinBlinkerModule extends ThreadModule {

    private static Integer counter = 0;
    private static Set<PlayerModelPart> settings = new HashSet<>();

    public SkinBlinkerModule() {
        super("Skin Blinker", "cosmetics", "skin-blinker");

        setSetting(0, Setting.of("blinker-type", config)
                .field(new DropDownField("blinker.type", "blinking",
                        Map.of("blinking", "Skin Blinker",
                                "custom", "Custom",
                                "wave", "Wave"
                        )))
                .prefix("Blinker Type")
        );

        setSetting(1, Setting.of("blinker-ticks", config)
                .field(new IntegerField("blinker.ticks", 4, 1, 16))
                .prefix("Blink Speed")
                .suffix("ticks")
                .description("Controls how fast parts blink in blinking/wave modes")
                .condition(s -> {
                    String blinkerType = getData("blinker.type", String.class, "blinking");
                    return blinkerType.equalsIgnoreCase("blinking") || blinkerType.equalsIgnoreCase("wave");
                })
        );

        for (PlayerModelPart modelPart : PlayerModelPart.values()) {

            int settingId = Arrays.asList(PlayerModelPart.values()).indexOf(modelPart) + 2;
            String settingKey = modelPart.toString().toLowerCase();

            setSetting(settingId, Setting.of("display-" + settingKey, config, s -> getData("blinker.type", String.class, "blinking").equals("custom"))
                    .field(new BooleanField(
                            settingKey + ".blinking",
                            !modelPart.equals(PlayerModelPart.CAPE)
                    ))
                    .prefix(modelPart.getName())
            );

        }
    }

    @Override
    protected void onThreadCall() {
        String mode = getData("blinker.type", String.class, "blinking");
        int ticks = getData("blinker.ticks", Integer.class, 4);

        switch (mode) {
            case "blinking" -> handleBlinkingMode(ticks);
            case "custom" -> handleCustomMode(ticks);
            case "wave" -> handleWaveMode(6);
        }
    }

    private void handleBlinkingMode(int intervalTicks) {
        if (counter < intervalTicks) {
            counter++;
            return;
        }
        counter = 0;

        for (PlayerModelPart part : PlayerModelPart.values()) {
            if (part != PlayerModelPart.CAPE) {
                toggleModelPart(part);
            }
        }
        sendUpdates();
    }

    private void handleCustomMode(int intervalTicks) {
        if (counter < intervalTicks) {
            counter++;
            return;
        }
        counter = 0;

        for (PlayerModelPart part : PlayerModelPart.values()) {
            boolean shouldBlink = getData(part.toString().toLowerCase() + ".blinking", Boolean.class,
                    part != PlayerModelPart.CAPE);

            if (shouldBlink) {
                toggleModelPart(part);
            }
        }
        sendUpdates();
    }

    private void handleWaveMode(@SuppressWarnings("SameParameterValue") int waveDuration) {
        PlayerModelPart[][] partsInOrder = {
                {PlayerModelPart.HAT},
                {PlayerModelPart.JACKET, PlayerModelPart.LEFT_SLEEVE, PlayerModelPart.RIGHT_SLEEVE},
                {PlayerModelPart.LEFT_PANTS_LEG, PlayerModelPart.RIGHT_PANTS_LEG}
        };

        int currentIndex = counter % (waveDuration * partsInOrder.length);
        int activePartIndex = currentIndex / waveDuration;

        for (int i = 0; i < partsInOrder.length; i++) {
            for (PlayerModelPart part : partsInOrder[i]) {
                boolean shouldEnable = (i == activePartIndex);
                MinecraftClient.getInstance().options.setPlayerModelPart(part, shouldEnable);
            }
        }

        counter++;
        sendUpdates();
    }

    private void toggleModelPart(PlayerModelPart part) {
        MinecraftClient.getInstance().options.setPlayerModelPart(
                part,
                !MinecraftClient.getInstance().options.isPlayerModelPartEnabled(part)
        );
    }

    private void sendUpdates() {
        MinecraftClient.getInstance().options.sendClientSettings();
    }

    @Override
    protected void onEnable() {
        settings.clear();
        for (PlayerModelPart part : PlayerModelPart.values()) {
            if (MinecraftClient.getInstance().options.isPlayerModelPartEnabled(part)) {
                settings.add(part);
            }
        }

    }

    @Override
    protected void onDisable() {
        for (PlayerModelPart part : PlayerModelPart.values()) {
            MinecraftClient.getInstance().options.setPlayerModelPart(part, settings.contains(part));
        }
        settings.clear();
    }

    public static Module getInstance() {
        return Modules.getModule("skin-blinker");
    }
}
