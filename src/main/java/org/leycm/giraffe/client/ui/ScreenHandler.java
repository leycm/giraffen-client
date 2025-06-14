package org.leycm.giraffe.client.ui;

import net.minecraft.client.gui.screen.Screen;
import org.leycm.giraffe.client.Client;
import org.leycm.giraffe.client.ui.screens.ModuleSystemScreen;
import org.leycm.giraffe.client.ui.screens.TestModernScreen;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ScreenHandler {
    private static final Map<String, Screen> screens = new HashMap<>();
    private static final Set<String> toOpen = new HashSet<>();

    private static final int TARGET_UI_SCALE = 2;
    private static int originalGuiScale = 3;

    public static void startClient() {
        new TestModernScreen();
        new ModuleSystemScreen();
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
        if (id.isEmpty() || !screens.containsKey(id) || Client.MC.currentScreen == null) {
            Screen screen = screens.get(id);
            if (originalGuiScale != TARGET_UI_SCALE) {
                Client.MC.options.getGuiScale().setValue(TARGET_UI_SCALE);
                Client.MC.onResolutionChanged();
            }
            Client.MC.setScreen(screen);
            toOpen.remove(id);
        }
    }

    public static void setOriginalGuiScale(int originalGuiScale) {ScreenHandler.originalGuiScale = originalGuiScale;}
    public static int getOriginalGuiScale() {return originalGuiScale;}
}
