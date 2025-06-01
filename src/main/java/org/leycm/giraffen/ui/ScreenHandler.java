package org.leycm.giraffen.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.leycm.giraffen.uiold.ModuleScreen;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ScreenHandler {
    private static final Map<String, Screen> screens = new HashMap<>();
    private static final Set<String> toOpen = new HashSet<>();

    public static void startClient() {
        new ModuleScreen();
    }

    public static void register(String id, Screen screen) {
        screens.put(id, screen);
    }

    public static void openUi(String id) {
        if (!screens.containsKey(id)) return;
        toOpen.add(id);
    }

    public static void run(){
        if(toOpen.isEmpty()) return;
        String id = toOpen.stream().findFirst().orElse("");
        MinecraftClient.getInstance().setScreen(screens.get(id));
        toOpen.remove(id);
    }



}
