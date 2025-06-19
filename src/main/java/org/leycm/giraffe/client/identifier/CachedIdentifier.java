package org.leycm.giraffe.client.identifier;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Represents a cached texture with its metadata and resources.
 * Supports both static images and animated GIFs.
 *
 * @param identifier The Minecraft identifier registered with the texture manager
 * @param width The width of the texture in pixels
 * @param height The height of the texture in pixels
 * @param sourcePath The original path (local file or URL) used to load this texture
 * @param images The NativeImage array containing the pixel data for static images or GIF frames
 * @param frameDelayMs The delay between frames in milliseconds (-1 for static images)
 */
public record CachedIdentifier(
        Identifier identifier,
        int width,
        int height,
        String sourcePath,
        NativeImage[] images,
        int frameDelayMs
) {

    @Contract("_ -> new")
    public static @NotNull CachedIdentifier of(String path) {
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
     * Determines if this texture is animated (GIF with multiple frames).
     *
     * @return true if this is an animated texture, false for static images
     */
    public boolean isAnimated() {
        return frameDelayMs > 0 && images.length > 1;
    }

    /**
     * Determines if this texture is a static image.
     *
     * @return true if this is a static image, false for animated textures
     */
    public boolean isStatic() {
        return frameDelayMs == -1 || images.length == 1;
    }

    /**
     * Gets the number of frames in this texture.
     *
     * @return 1 for static images, or the actual frame count for GIFs
     */
    public int getFrameCount() {
        return images.length;
    }

    /**
     * Gets a specific frame from the texture.
     *
     * @param frameIndex The index of the frame to retrieve (0-based)
     * @return The NativeImage for the specified frame
     * @throws IndexOutOfBoundsException if frameIndex is invalid
     */
    public NativeImage getFrame(int frameIndex) {
        if (frameIndex < 0 || frameIndex >= images.length) {
            throw new IndexOutOfBoundsException("Frame index " + frameIndex + " out of bounds for " + images.length + " frames");
        }
        return images[frameIndex];
    }

    /**
     * Gets the current frame based on the current time and frame delay.
     * For static images, always returns frame 0.
     *
     * @return The current frame index
     */
    public int getCurrentFrameIndex() {
        if (isStatic()) {
            return 0;
        }
        long currentTime = System.currentTimeMillis();
        return (int) ((currentTime / frameDelayMs) % images.length);
    }

    /**
     * Gets the current frame based on the current time and frame delay.
     * For static images, always returns the single frame.
     *
     * @return The current NativeImage frame
     */
    public NativeImage getCurrentFrame() {
        return getFrame(getCurrentFrameIndex());
    }

    /**
     * Gets the total duration of the animation in milliseconds.
     * For static images, returns 0.
     *
     * @return The total animation duration
     */
    public long getTotalAnimationDuration() {
        if (isStatic()) {
            return 0;
        }
        return (long) frameDelayMs * images.length;
    }

    /**
     * Reloads this texture from its original source.
     *
     * @return The refreshed texture
     */
    @Contract(" -> new")
    public @NotNull CachedIdentifier refresh() throws IOException {
        return IdentifierRegistry.refreshTexture(sourcePath);
    }

    /**
     * Removes this texture from the cache and releases its resources.
     */
    public void remove() {
        IdentifierRegistry.clearFromCache(sourcePath);
    }

    /**
     * Returns a string representation of this cached identifier.
     *
     * @return A descriptive string
     */
    @Override
    public @NotNull String toString() {
        String type = isAnimated() ? "Animated" : "Static";
        String frames = isAnimated() ? " (" + images.length + " frames, " + frameDelayMs + "ms delay)" : "";
        return "CachedIdentifier{" +
                "type=" + type +
                ", size=" + width + "x" + height +
                ", source=\"" + sourcePath + "\"" +
                ", identifier=\"" + identifier.getPath() + "\"" +
                frames +
                '}';
    }

}