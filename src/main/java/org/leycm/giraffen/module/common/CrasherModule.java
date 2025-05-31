package org.leycm.giraffen.module.common;

import net.minecraft.client.MinecraftClient;

public abstract class CrasherModule extends BaseModule {
    protected CrasherModule(String displayName, String category, String id) {
        super(displayName, category, id);
    }

    @Override
    public boolean enable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.world != null) onEnable();
        return running = false;
    }

    @Override public boolean disable() {return false;}
    @Override protected void onDisable() {}
    @Override public boolean isRunning() {return super.isRunning();}
}
