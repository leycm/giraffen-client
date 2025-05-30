package org.leycm.giraffen.module.common;

import org.jetbrains.annotations.NotNull;
import org.leycm.giraffen.module.Modules;
import org.leycm.storage.StorageBase;
import org.leycm.storage.impl.JavaStorage;

public abstract class Module {
    private final String displayName;
    private final String id;
    private final StorageBase config;

    protected boolean running;

    public Module(String displayName, String category, String id) {
        Modules.instances.put(id, this);

        this.displayName = displayName;
        this.id = id;
        this.config = JavaStorage.of("modules/" + category + "/" + id, StorageBase.Type.JSON, JavaStorage.class);
    }


    public boolean enable() {
        if(!running) running = true;
        Modules.onModuleEnabled(id);
        onEnable();
        return true;
    }

    public boolean disable() {
        if(running) running = false;
        Modules.onModuleDisabled(id);
        onDisable();
        return false;
    }

    public void register() {

    }

    public void toggle() {running = !running ? enable() : disable();}

    protected abstract void onEnable();
    protected abstract void onDisable();

    public void setSetting(String key, Object value) {config.set(key, value);}
    public void setDefaultSetting(String key, @NotNull Object value) {if (config.get(key, value.getClass()) == null) config.set(key, value);}
    public <T> T getSetting(String key, Class<T> valueClass, T value) {return config.get(key, valueClass, value);}
    public <T> T getSetting(String key, Class<T> valueClass) {return config.get(key, valueClass);}

    public String getId() {return id;}
    public String getDisplayName() {return displayName;}

    public boolean isRunning() {return running;}
    public void saveSettings() {config.save();}
    public void reloadSettings() {config.reload();}
}
