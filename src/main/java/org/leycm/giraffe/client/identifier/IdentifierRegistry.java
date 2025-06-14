package org.leycm.giraffe.client.identifier;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.leycm.giraffe.client.Client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A texture caching system that manages both local and remote textures with asynchronous loading capabilities.
 * This class handles texture loading, caching, and resource management for Minecraft textures.
 */
public class IdentifierRegistry {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);
    private static final TextureManager TEXTURE_MANAGER = MinecraftClient.getInstance().getTextureManager();
    private static final Map<String, CachedIdentifier> cache = new HashMap<>();

    /**
     * Loads a texture from the specified path or URL.
     *
     * @param path The file path (relative to Minecraft run directory) or web URL of the texture
     * @return The loaded CachedTexture
     * @throws RuntimeException if the texture fails to load
     */
    @Contract("_ -> new")
    public static @NotNull CachedIdentifier loadTexture(String path) {
        try {
            if (cache.containsKey(path)) {
                return cache.get(path);
            }

            String namespace = path.startsWith("http") ? "remote" : "local";
            String texturePath = generateTexturePath(path);

            Identifier identifier = Identifier.of(Client.MOD_ID + "-cache", namespace + "/" + texturePath);

            NativeImage image = loadImage(path);
            int width = image.getWidth();
            int height = image.getHeight();

            TEXTURE_MANAGER.registerTexture(identifier, new NativeImageBackedTexture(image));

            CachedIdentifier cached = new CachedIdentifier(identifier, width, height, path, image);
            cache.put(path, cached);
            return cached;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load texture: " + path, e);
        }
    }

    /**
     * Loads a NativeImage from either a local file or web URL.
     *
     * @param path The file path or URL to load the image from
     * @return The loaded NativeImage
     * @throws IOException if the image cannot be read
     */
    private static @NotNull NativeImage loadImage(@NotNull String path) throws IOException {
        if (path.startsWith("http")) {
            File tempFile = File.createTempFile("web_texture_", ".png");
            FileUtils.copyURLToFile(URL.of(URI.create(path), null), tempFile);
            return NativeImage.read(Files.newInputStream(tempFile.toPath()));
        } else if (path.charAt(1) == ':'){ // TODO : Make better logik next time
            Path resolvedPath = Paths.get(path);
            try (InputStream is = Files.newInputStream(resolvedPath)) {
                return NativeImage.read(is);
            }

        } else {
            Path resolvedPath = Paths.get(MinecraftClient.getInstance().runDirectory.getPath(), path);
            try (InputStream is = Files.newInputStream(resolvedPath)) {
                return NativeImage.read(is);
            }
        }
    }

    /**
     * Reloads a texture from its original source.
     *
     * @param path The original path/URL of the texture to refresh
     * @return The refreshed texture
     */
    @Contract("_ -> new")
    public static @NotNull CachedIdentifier refreshTexture(String path) {
        clearFromCache(path);
        return loadTexture(path);
    }

    /**
     * Refreshes all currently cached textures.
     */
    public static void refreshAll() {
        var paths = cache.keySet().toArray(new String[0]);
        clearCache();
        for (String path : paths) {
            loadTexture(path);
        }
    }

    /**
     * Retrieves a cached texture by its original path/URL.
     *
     * @param path The original path/URL used to load the texture
     * @return The cached texture, or null if no texture exists for this path
     */
    public static CachedIdentifier getCachedTexture(String path) {
        return cache.get(path);
    }

    /**
     * Removes a specific texture from the cache and releases its resources.
     *
     * @param path The original path/URL of the texture to remove
     */
    public static void clearFromCache(String path) {
        CachedIdentifier texture = cache.get(path);
        if (texture != null) {
            TEXTURE_MANAGER.destroyTexture(texture.identifier());
            texture.image().close();
            cache.remove(path);
        }
    }

    /**
     * Clears all cached textures and releases their resources.
     */
    public static void clearCache() {
        cache.forEach((path, texture) -> {
            TEXTURE_MANAGER.destroyTexture(texture.identifier());
            texture.image().close();
        });
        cache.clear();
    }

    /**
     * Generates a valid texture path from the original source path/URL.
     * For web URLs, creates a consistent hash-based identifier.
     * For local paths, sanitizes the input to create a valid resource path.
     *
     * @param originalPath The original path or URL
     * @return A sanitized path suitable for use as a texture identifier
     */
    private static @NotNull String generateTexturePath(@NotNull String originalPath) {
        if (originalPath.startsWith("http")) {
            return "web_" + Math.abs(originalPath.hashCode());
        }

        String filename = Paths.get(originalPath).getFileName().toString();
        return filename
                .replaceAll("[^a-zA-Z0-9._-]", "_")
                .toLowerCase(); // Minecraft identifiers should be lowercase
    }
}