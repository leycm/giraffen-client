package org.leycm.giraffe.client.identifier;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a cached texture with its metadata and resources.
 *
 * @param identifier The Minecraft identifier registered with the texture manager
 * @param width The width of the texture in pixels
 * @param height The height of the texture in pixels
 * @param sourcePath The original path (local file or URL) used to load this texture
 * @param image The NativeImage containing the pixel data
 */
public record CachedIdentifier(Identifier identifier, int width, int height, String sourcePath, NativeImage image) {

    @Contract("_ -> new")
    public static @NotNull CachedIdentifier of(String path){
        return IdentifierRegistry.loadTexture(path);
    }

    /**
     * Determines if this texture was loaded from a local file.
     *
     * @return true if the source is a local file path, false if it's a web URL
     */
    public boolean isLocal() {
        return !sourcePath.startsWith("http");
    }

    /**
     * Reloads this texture from its original source.
     *
     * @return The refreshed texture
     */
    @Contract(" -> new")
    public @NotNull CachedIdentifier refresh() {
        return IdentifierRegistry.refreshTexture(sourcePath);
    }

    /**
     * Removes this texture from the cache and releases its resources.
     */
    public void remove() {
        IdentifierRegistry.clearFromCache(sourcePath);
    }
}