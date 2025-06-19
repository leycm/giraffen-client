package org.leycm.giraffe.client.identifier;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.leycm.giraffe.client.Client;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A texture caching system that manages both local and remote textures with asynchronous loading capabilities.
 * This class handles texture loading, caching, and resource management for Minecraft textures including GIF support.
 */
public class IdentifierRegistry {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);
    private static final ScheduledExecutorService ANIMATION_EXECUTOR = Executors.newScheduledThreadPool(2);
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

            if (path.toLowerCase().endsWith(".gif")) {
                return loadGifTexture(path, identifier);
            } else {
                return loadStaticTexture(path, identifier);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load texture: " + path, e);
        }
    }

    /**
     * Loads a static (non-animated) texture.
     */
    private static @NotNull CachedIdentifier loadStaticTexture(String path, Identifier identifier) throws IOException {
        NativeImage image = loadImage(path);

        int width = image.getWidth();
        int height = image.getHeight();


        TEXTURE_MANAGER.registerTexture(identifier, new NativeImageBackedTexture(image));

        CachedIdentifier cached = new CachedIdentifier(
                identifier,
                width, height,
                path,
                new NativeImage[]{image},
                -1
        );

        cache.put(path, cached);

        Client.LOGGER.info(cached.toString());
        return cached;
    }

    /**
     * Loads a GIF texture with animation support.
     */
    private static @NotNull CachedIdentifier loadGifTexture(String path, Identifier identifier) throws IOException {
        GifData gifData = loadGifFrames(path);

        TEXTURE_MANAGER.registerTexture(identifier, new NativeImageBackedTexture(gifData.frames[0]));

        CachedIdentifier cached = new CachedIdentifier(
                identifier,
                gifData.frames[0].getWidth(),
                gifData.frames[0].getHeight(),
                path,
                gifData.frames,
                gifData.frameDelay
        );

        cache.put(path, cached);

        if (gifData.frames.length > 1) {
            startGifAnimation(cached);
        }

        Client.LOGGER.info(cached.toString());
        return cached;
    }

    /**
     * Starts the animation cycle for a GIF texture.
     */
    private static void startGifAnimation(@NotNull CachedIdentifier cachedIdentifier) {
        ANIMATION_EXECUTOR.scheduleAtFixedRate(() -> {
            try {
                NativeImage[] frames = cachedIdentifier.images();
                if (frames.length <= 1) return;

                long currentTime = System.currentTimeMillis();
                int frameIndex = (int) ((currentTime / cachedIdentifier.frameDelayMs()) % frames.length);

                NativeImage originalFrame = frames[frameIndex];
                if (originalFrame == null) return;

                RenderSystem.recordRenderCall(() -> {
                    try {
                        NativeImage frameCopy = copyNativeImage(originalFrame);
                        NativeImageBackedTexture texture = new NativeImageBackedTexture(frameCopy);
                        TEXTURE_MANAGER.registerTexture(cachedIdentifier.identifier(), texture);
                    } catch (Exception e) {
                        //Client.LOGGER.error("Error updating GIF by NativeImageBackedTexture {}", e.getMessage());
                    }
                });

            } catch (Exception e) {
                Client.LOGGER.error("Error updating GIF frame: {}", e.getMessage());
            }
        }, 0, cachedIdentifier.frameDelayMs(), TimeUnit.MILLISECONDS);
    }

    /**
     * Creates a copy of a NativeImage to prevent deallocation issues.
     */
    private static @NotNull NativeImage copyNativeImage(@NotNull NativeImage original) {
        int width = original.getWidth();
        int height = original.getHeight();
        NativeImage copy = new NativeImage(width, height, false);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                copy.setColorArgb(x, y, original.getColorArgb(x, y));
            }
        }

        return copy;
    }

    /**
     * Loads all frames from a GIF file.
     */
    private static @NotNull GifData loadGifFrames(@NotNull String path) throws IOException {
        File tempFile = null;
        try {
            File file;
            if (path.startsWith("http")) {
                tempFile = File.createTempFile("gif_texture_", ".gif");
                FileUtils.copyURLToFile(URL.of(URI.create(path), null), tempFile);
                file = tempFile;
            } else if (path.charAt(1) == ':') {
                file = new File(path);
            } else {
                file = new File(MinecraftClient.getInstance().runDirectory, path);
            }

            try (ImageInputStream input = ImageIO.createImageInputStream(file)) {
                ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
                reader.setInput(input);

                Integer transparentColor = null;

                int frameCount = reader.getNumImages(true);
                NativeImage[] frames = new NativeImage[frameCount];
                int frameDelay = 100;
                int width = 0;
                int height = 0;

                if (frameCount > 0) {
                    IIOMetadata metadata = reader.getImageMetadata(0);
                    if (metadata != null) {
                        Node tree = metadata.getAsTree(metadata.getNativeMetadataFormatName());
                        width = extractDimension(tree, "ImageWidth");
                        height = extractDimension(tree, "ImageHeight");
                    }
                    if (metadata != null) {
                        Node tree = metadata.getAsTree(metadata.getNativeMetadataFormatName());
                        transparentColor = extractTransparentColor(tree);
                    }
                }

                for (int i = 0; i < frameCount; i++) {
                    BufferedImage bufferedImage = reader.read(i);
                    if (width == 0 || height == 0) {
                        width = bufferedImage.getWidth();
                        height = bufferedImage.getHeight();
                    }
                    frames[i] = convertToNativeImage(bufferedImage, width, height, transparentColor);

                    if (i == 0) {
                        try {
                            IIOMetadata metadata = reader.getImageMetadata(i);
                            String metaFormat = metadata.getNativeMetadataFormatName();
                            Node tree = metadata.getAsTree(metaFormat);
                            frameDelay = extractGifDelay(tree);
                        } catch (Exception ignored) {}
                    }
                }

                reader.dispose();
                return new GifData(frames, frameDelay, width, height);
            }
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    /**
     * Extracts frame delay from GIF metadata.
     *
     * @param tree The IIOMetadata tree node
     * @return Frame delay in milliseconds, or 100ms default if not found
     */
    private static int extractGifDelay(Node tree) {
        if (tree == null) return 100;

        for (int i = 0; i < tree.getChildNodes().getLength(); i++) {
            Node child = tree.getChildNodes().item(i);
            if ("GraphicControlExtension".equals(child.getNodeName())) {
                Node delayNode = child.getAttributes().getNamedItem("delayTime");
                try {
                    return Math.max(Integer.parseInt(delayNode.getNodeValue()) * 10, 10);
                } catch (Exception e) {
                    return 100;
                }
            }
            int delay = extractGifDelay(child);
            if (delay != 100) return delay;
        }
        return 100;
    }

    /**
     * Converts a BufferedImage to NativeImage.
     */
    private static @NotNull NativeImage convertToNativeImage(
            @NotNull BufferedImage bufferedImage,
            int targetWidth,
            int targetHeight,
            @Nullable Integer transparentColor
    ) {
        NativeImage nativeImage = new NativeImage(targetWidth, targetHeight, false);

        int srcWidth = bufferedImage.getWidth();
        int srcHeight = bufferedImage.getHeight();
        int copyWidth = Math.min(srcWidth, targetWidth);
        int copyHeight = Math.min(srcHeight, targetHeight);

        for (int x = 0; x < copyWidth; x++) {
            for (int y = 0; y < copyHeight; y++) {
                int rgb = bufferedImage.getRGB(x, y);

                if (transparentColor != null && (rgb & 0x00FFFFFF) == transparentColor) {
                    nativeImage.setColorArgb(x, y, 0x00000000); // Transparent
                } else {
                    nativeImage.setColorArgb(x, y, rgb | 0xFF000000);
                }
            }
        }

        return nativeImage;
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
        } else if (path.charAt(1) == ':'){ // TODO : Make better logic next time
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
            for(NativeImage image : texture.images()) {
                if (image != null) image.close();
            }
            cache.remove(path);
        }
    }

    /**
     * Clears all cached textures and releases their resources.
     */
    public static void clearCache() {
        cache.forEach((path, texture) -> {clearFromCache(path);});
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

        Path path = Paths.get(originalPath).toAbsolutePath();

        String filename = (path.getRoot() != null)
                ? path.subpath(0, path.getNameCount()).toString()
                : path.toString();

        return filename
                .replace("\\", "/")
                .replaceAll("[^a-zA-Z0-9._/-]", "_")
                .toLowerCase(); // mc formatting
    }

    private static int extractDimension(Node tree, String dimensionName) {
        Node dimensionNode = findNode(tree, dimensionName);
        if (dimensionNode != null) {
            try {
                return Integer.parseInt(dimensionNode.getAttributes().getNamedItem("value").getNodeValue());
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    private static @Nullable Node findNode(@NotNull Node node, String nodeName) {
        if (node.getNodeName().equals(nodeName)) {
            return node;
        }
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node found = findNode(children.item(i), nodeName);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private static @Nullable Integer extractTransparentColor(@NotNull Node tree) {
        Integer transparentIndex = null;
        NodeList children = tree.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if ("GraphicControlExtension".equals(child.getNodeName())) {
                Node transparentNode = child.getAttributes().getNamedItem("transparentColorIndex");
                if (transparentNode != null && !transparentNode.getNodeValue().isEmpty()) {
                    try {
                        transparentIndex = Integer.parseInt(transparentNode.getNodeValue());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                    break;
                }
            }
        }

        Node colorTable = null;
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if ("ImageDescriptor".equals(child.getNodeName())) {
                NodeList imageChildren = child.getChildNodes();
                for (int j = 0; j < imageChildren.getLength(); j++) {
                    if ("LocalColorTable".equals(imageChildren.item(j).getNodeName())) {
                        colorTable = imageChildren.item(j);
                        break;
                    }
                }
                break;
            }
        }

        if (colorTable == null) {
            for (int i = 0; i < children.getLength(); i++) {
                if ("GlobalColorTable".equals(children.item(i).getNodeName())) {
                    colorTable = children.item(i);
                    break;
                }
            }
        }

        if (colorTable == null) return null;
        NodeList colorEntries = colorTable.getChildNodes();
        if (transparentIndex >= 0 && transparentIndex < colorEntries.getLength()) {
            Node colorEntry = colorEntries.item(transparentIndex);
            try {
                int red = Integer.parseInt(colorEntry.getAttributes().getNamedItem("red").getNodeValue());
                int green = Integer.parseInt(colorEntry.getAttributes().getNamedItem("green").getNodeValue());
                int blue = Integer.parseInt(colorEntry.getAttributes().getNamedItem("blue").getNodeValue());
                return (red << 16) | (green << 8) | blue;
            } catch (Exception e) {
                return null;
            }
        }
        return transparentIndex;
    }

    /**
     * Helper class to store GIF frame data and timing.
     */
    private record GifData(NativeImage[] frames, int frameDelay, int width, int height) { }

    /**
     * Shutdown method to clean up executors when mod is unloaded.
     */
    public static void shutdown() {
        EXECUTOR.shutdown();
        ANIMATION_EXECUTOR.shutdown();
        clearCache();
    }
}