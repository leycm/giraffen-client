package org.leycm.giraffe.client.module.modules.cosmetics;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.leycm.giraffe.client.Client;
import org.leycm.giraffe.client.identifier.CachedIdentifier;
import org.leycm.giraffe.client.module.Modules;
import org.leycm.giraffe.client.module.common.BaseModule;
import org.leycm.giraffe.client.settings.Setting;
import org.leycm.giraffe.client.settings.fields.DropDownField;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class CapeLoaderModule extends BaseModule {

    private static final Map<String, String> capes = new HashMap<>();
    private static final Map<String, String> groups = new HashMap<>();
    private static final Map<String, CachedIdentifier> capeCache = new HashMap<>();

    public CapeLoaderModule() {
        super("Cape Loader", "cosmetics", "cape-loader");

        // Standard Capes hinzufügen
        capes.put("giraffe", "Giraffen Cape v1");
        capes.put("giraffe-inverted", "Giraffen Cape v2");
        groups.put("default", "Default");

        setSetting(0, Setting.of("use-cape", config)
                .field(new DropDownField("cape.in-use.type", "default", groups))
                .field(new DropDownField("cape.in-use.id", "none", capes))
        );

        register();
    }

    @Override
    protected void onEnable() {
        loadExternalCapeTextures();
    }

    @Override
    protected void onDisable() {
        clearCapeCache();
    }

    /**
     * Gibt die Identifier für das aktuell ausgewählte Cape zurück.
     *
     * @return Identifier des Capes oder null wenn keins ausgewählt
     */
    public Identifier getCape() {
        String capeId = getData("cape.in-use.id", String.class, "none");
        String capeType = getData("cape.in-use.type", String.class, "default");

        if (capeId.equalsIgnoreCase("none") || capeId.isEmpty() || capeType.isEmpty()) {
            return null;
        }

        String cacheKey = capeType + "/" + capeId;
        CachedIdentifier cachedCape = capeCache.get(cacheKey);

        if (cachedCape != null) {
            return cachedCape.identifier();
        }

        if (capeType.equalsIgnoreCase("default")) {
            String path = "cape/" + capeType + "/" + capeId + ".png";
            return Identifier.of(Client.MOD_ID, path);
        }

        return null;
    }

    /**
     * Gibt das CachedIdentifier für das aktuell ausgewählte Cape zurück.
     *
     * @return CachedIdentifier des Capes oder null wenn keins ausgewählt
     */
    public CachedIdentifier getCachedCape() {
        String capeId = getData("cape.in-use.id", String.class, "none");
        String capeType = getData("cape.in-use.type", String.class, "default");

        if (capeId.equalsIgnoreCase("none") || capeId.isEmpty() || capeType.isEmpty()) {
            return null;
        }

        String cacheKey = capeType + "/" + capeId;
        return capeCache.get(cacheKey);
    }

    /**
     * Lädt externe Cape-Texturen aus dem config-Ordner.
     */
    public static void loadExternalCapeTextures() {
        Path configPath = Paths.get("config", Client.MOD_ID, "cape");

        if (!Files.exists(configPath)) {
            try {
                Files.createDirectories(configPath);
                Client.LOGGER.info("Created cape directory: {}", configPath);
            } catch (IOException e) {
                Client.LOGGER.error("Failed to create cape directory", e);
                return;
            }
        }

        try (Stream<Path> paths = Files.walk(configPath, 1)) {
            paths.filter(Files::isDirectory)
                    .filter(p -> !p.equals(configPath))
                    .forEach(CapeLoaderModule::loadCapesFromDirectory);
        } catch (IOException e) {
            Client.LOGGER.error("Failed to scan cape directories", e);
        }
    }

    /**
     * Lädt alle Capes aus einem spezifischen Verzeichnis.
     *
     * @param subDir Das Unterverzeichnis mit den Cape-Dateien
     */
    private static void loadCapesFromDirectory(@NotNull Path subDir) {
        String type = subDir.getFileName().toString();

        try (Stream<Path> subFiles = Files.list(subDir)) {
            subFiles.filter(Files::isRegularFile)
                    .filter(p -> {
                        String name = p.toString().toLowerCase();
                        return name.endsWith(".png") || name.endsWith(".gif");
                    })
                    .forEach(capePath -> loadSingleCape(capePath, type));
        } catch (IOException e) {
            Client.LOGGER.error("Failed to load capes from directory: {}", subDir, e);
        }
    }

    /**
     * Lädt ein einzelnes Cape und fügt es zum Cache hinzu.
     *
     * @param capePath Pfad zur Cape-Datei
     * @param type Typ des Capes (Verzeichnisname)
     */
    private static void loadSingleCape(Path capePath, String type) {
        try {
            String fileName = capePath.getFileName().toString();
            String id = fileName.substring(0, fileName.lastIndexOf('.'));
            String relativePath = capePath.toString();

            CachedIdentifier cachedCape = CachedIdentifier.of(relativePath);

            String cacheKey = type + "/" + id;
            capeCache.put(cacheKey, cachedCape);

            capes.put(id, id);
            groups.put(type, type);

            String capeInfo = cachedCape.isAnimated() ?
                    " (Animated, " + cachedCape.getFrameCount() + " frames)" : " (Static)";

            Client.LOGGER.info("Loaded cape [ID={}, Type={}] from \"{}\"{}", id, type, relativePath, capeInfo);

        } catch (Exception e) {
            Client.LOGGER.error("Failed to load cape: {}", capePath, e);
        }
    }

    /**
     * Lädt ein Cape von einer URL.
     *
     * @param url Die URL der Cape-Datei
     * @param id Die ID des Capes
     * @param type Der Typ des Capes
     * @return true wenn erfolgreich geladen, false sonst
     */
    public boolean loadCapeFromUrl(String url, String id, String type) {
        try {
            CachedIdentifier cachedCape = CachedIdentifier.of(url);

            String cacheKey = type + "/" + id;
            capeCache.put(cacheKey, cachedCape);

            capes.put(id, id);
            groups.put(type, type);

            String capeInfo = cachedCape.isAnimated() ?
                    " (Animated, " + cachedCape.getFrameCount() + " frames)" : " (Static)";

            Client.LOGGER.info("Loaded cape from URL [ID=" + id + ", Type=" + type + "]" + capeInfo);
            return true;

        } catch (Exception e) {
            Client.LOGGER.error("Failed to load cape from URL: " + url, e);
            return false;
        }
    }

    /**
     * Aktualisiert ein Cape aus dem Cache.
     *
     * @param id Die Cape-ID
     * @param type Der Cape-Typ
     * @return true wenn erfolgreich aktualisiert, false sonst
     */
    public boolean refreshCape(String id, String type) {
        String cacheKey = type + "/" + id;
        CachedIdentifier cachedCape = capeCache.get(cacheKey);

        if (cachedCape != null) {
            try {
                CachedIdentifier refreshed = cachedCape.refresh();
                capeCache.put(cacheKey, refreshed);
                Client.LOGGER.info("Refreshed cape [ID=" + id + ", Type=" + type + "]");
                return true;
            } catch (Exception e) {
                Client.LOGGER.error("Failed to refresh cape: " + cacheKey, e);
                return false;
            }
        }

        return false;
    }

    /**
     * Entfernt ein Cape aus dem Cache.
     *
     * @param id Die Cape-ID
     * @param type Der Cape-Typ
     */
    public void removeCape(String id, String type) {
        String cacheKey = type + "/" + id;
        CachedIdentifier cachedCape = capeCache.get(cacheKey);

        if (cachedCape != null) {
            cachedCape.remove();
            capeCache.remove(cacheKey);
            capes.remove(id);

            Client.LOGGER.info("Removed cape [ID=" + id + ", Type=" + type + "]");
        }
    }

    /**
     * Leert den gesamten Cape-Cache.
     */
    public void clearCapeCache() {
        capeCache.values().forEach(CachedIdentifier::remove);
        capeCache.clear();
        Client.LOGGER.info("Cleared cape cache");
    }

    /**
     * Gibt alle geladenen Capes zurück.
     *
     * @return Map mit allen Cape-IDs und Namen
     */
    public static Map<String, String> getAvailableCapes() {
        return new HashMap<>(capes);
    }

    /**
     * Gibt alle verfügbaren Cape-Gruppen zurück.
     *
     * @return Map mit allen Gruppen-IDs und Namen
     */
    public static Map<String, String> getAvailableGroups() {
        return new HashMap<>(groups);
    }

    /**
     * Prüft ob ein Cape animiert ist.
     *
     * @param id Die Cape-ID
     * @param type Der Cape-Typ
     * @return true wenn das Cape animiert ist, false sonst
     */
    public boolean isCapeAnimated(String id, String type) {
        String cacheKey = type + "/" + id;
        CachedIdentifier cachedCape = capeCache.get(cacheKey);
        return cachedCape != null && cachedCape.isAnimated();
    }

    /**
     * Gibt Informationen über ein Cape zurück.
     *
     * @param id Die Cape-ID
     * @param type Der Cape-Typ
     * @return String mit Cape-Informationen oder null
     */
    public String getCapeInfo(String id, String type) {
        String cacheKey = type + "/" + id;
        CachedIdentifier cachedCape = capeCache.get(cacheKey);

        if (cachedCape != null) {
            return cachedCape.toString();
        }

        return null;
    }

    public static CapeLoaderModule getInstance() {
        return (CapeLoaderModule) Modules.getModule("cape-loader");
    }
}