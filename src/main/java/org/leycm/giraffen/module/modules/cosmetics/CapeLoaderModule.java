package org.leycm.giraffen.module.modules.cosmetics;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.leycm.giraffen.GiraffenClient;
import org.leycm.giraffen.module.Modules;
import org.leycm.giraffen.module.common.Module;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class CapeLoaderModule extends Module {
    public CapeLoaderModule() {
        super("Cape Loader", "cosmetics", "cape-loader");
        setDefaultSetting("cape.in-use.id", "none");
        setDefaultSetting("cape.in-use.type", "default");
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {

    }

    public static void cashExternalCapeTextures() {
        Path path = Paths.get("config", GiraffenClient.MOD_ID, "cape");

        try (Stream<Path> paths = Files.walk(path, 1)) {
            paths.filter(Files::isDirectory)
                    .filter(p -> !p.equals(path))
                    .forEach(subDir -> {
                        String type = subDir.getFileName().toString();

                        try (Stream<Path> subFiles = Files.list(subDir)) {
                            subFiles.filter(Files::isRegularFile)
                                    .filter(p -> p.toString().toLowerCase().endsWith(".png"))
                                    .forEach(capePath -> {
                                        String fileName = capePath.getFileName().toString();
                                        String id = fileName.substring(0, fileName.length() - 4);

                                        String identifier = "cape/dynamic/" + id + ".png";
                                        loadExternalCapeTexture(Identifier.of(GiraffenClient.MOD_ID, identifier), capePath.toFile());
                                        GiraffenClient.LOGGER.info("Load cape [ID=" + id + ", Typ=" + type + "] from \"config/giraffen-client/cape/" + type + "/" + fileName + "\"");
                                    });
                        } catch (IOException ignored) {}
                    });
        } catch (IOException ignored) {}

        File file = path.toFile();
    }

    private static void loadExternalCapeTexture(Identifier identifier, File file) {
        try {
            BufferedImage image = ImageIO.read(file);

            NativeImage nativeImage = new NativeImage(image.getWidth(), image.getHeight(), true);
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int rgb = image.getRGB(x, y);
                    nativeImage.setColorArgb(x, y, rgb);
                }
            }
            MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, new NativeImageBackedTexture(nativeImage));
        } catch (IOException e) {
            GiraffenClient.LOGGER.error("Failed to load cape texture from file: " + file.getPath(), e);
        }
    }

    public static Module getInstance() {
        return Modules.getModule("cape-loader");
    }
}