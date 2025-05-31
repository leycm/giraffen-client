package org.leycm.giraffen.module;

import org.jetbrains.annotations.NotNull;
import org.leycm.giraffen.GiraffenClient;
import org.leycm.giraffen.module.common.BaseModule;
import org.leycm.giraffen.module.modules.cosmetics.CapeLoaderModule;
import org.leycm.giraffen.module.modules.cosmetics.SkinBlinkerModule;
import org.leycm.giraffen.module.modules.crasher.BundleCrashModule;
import org.leycm.giraffen.module.modules.esp.EntityEspModule;
import org.leycm.giraffen.module.modules.movment.AirJumpModule;
import org.leycm.giraffen.module.modules.utils.SkinChangerModule;
import org.leycm.giraffen.module.modules.utils.FullbrightModule;
import org.leycm.storage.StorageBase;
import org.leycm.storage.impl.JavaStorage;

import java.util.*;

public class Modules {
    public static final Map<String, BaseModule> instances = new HashMap<>();
    private static final Set<BaseModule> running = new HashSet<>();
    private static final StorageBase config = StorageBase.of("modules/data", StorageBase.Type.JSON, JavaStorage.class);

    public static BaseModule getModule(String id) {
        return instances.get(id);
    }
    public static BaseModule addModule(BaseModule module) {return instances.put(module.getId(), module);}

    public static void startClient() {
        new EntityEspModule();
        new SkinBlinkerModule();
        new FullbrightModule();
        new CapeLoaderModule();
        new AirJumpModule();
        new BundleCrashModule();
        new SkinChangerModule();

        @SuppressWarnings("unchecked")
        List<String> lastTimeActive = (List<String>) config.get("modules.active", List.class, new ArrayList<>());
        GiraffenClient.LOGGER.info(lastTimeActive.toString());
        instances.forEach((id, module) -> {
            if(lastTimeActive.contains(id)) module.enable();
            module.saveSettings();
        });

    }

    public static void saveClient() {
        instances.forEach((moduleid, module) -> {
            module.saveSettings();
        });
        config.set("modules.active", getRunningModuleIds());
        config.save();
    }

    public static void onModuleEnabled(String id) {
        BaseModule module = instances.get(id);
        if(module != null) running.add(module);
    }

    public static void onModuleDisabled(String id) {
        BaseModule module = instances.get(id);
        if(module != null) running.remove(module);
    }

    public static void enableModule(String id) {
        BaseModule module = instances.get(id);
        if(module != null) module.enable();
    }

    public static void disableModule(String id) {
        BaseModule module = instances.get(id);
        if(module != null) module.disable();
    }

    public static void toggleModule(String id) {
        BaseModule module = instances.get(id);
        if(module != null) module.toggle();
    }

    public static Set<BaseModule> getRunningModules() {return running;}
    public static @NotNull Set<String> getRunningModuleIds() {
        Set<String> result = new HashSet<>();
        running.forEach(module -> result.add(module.getId()));
        return result;
    }

    public static Map<String, BaseModule> getModules() {return instances;}
    public static @NotNull Set<String> getModuleIds() {
        Set<String> result = new HashSet<>();
        instances.forEach((id, module) -> result.add(id));
        return result;
    }

}
