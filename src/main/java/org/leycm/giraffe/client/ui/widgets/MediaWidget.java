package org.leycm.giraffe.client.ui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.render.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.leycm.giraffe.client.identifier.CachedIdentifier;

public class MediaWidget implements Drawable {

    public enum RenderMode {
        COVER,      // Skaliert so, dass das Widget komplett gefüllt wird – kann beschneiden
        FIT,        // Skaliert so, dass das ganze Bild sichtbar bleibt – kann Lücken lassen
        STRETCH,    // Skaliert das Bild ohne Rücksicht auf Seitenverhältnis
        CENTER,     // Kein Skalieren, einfach zentriert anzeigen
        TILE,       // Bild wird gekachelt
        FILL_WIDTH, // Skaliert nur auf Breite
        FILL_HEIGHT // Skaliert nur auf Höhe
    }


    private final CachedIdentifier texture;
    private final Identifier image;
    private final int filter;
    private final RenderMode renderMode;
    private final int width, height;
    private final int x, y;

    public MediaWidget(int x, int y, int width, int height, @NotNull CachedIdentifier texture) {
        this(x, y, width, height, texture, RenderMode.COVER, 0xFFFFFFFF);
    }

    public MediaWidget(int x, int y, int width, int height, @NotNull CachedIdentifier texture, RenderMode renderMode) {
        this(x, y, width, height, texture, renderMode, 0xFFFFFFFF);
    }

    public MediaWidget(int x, int y, int width, int height, @NotNull CachedIdentifier texture, RenderMode renderMode, int filter) {
        this.texture = texture;
        this.image = texture.identifier();
        this.filter = filter;
        this.renderMode = renderMode;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void render(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderTexture(0, image);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableBlend();

        int texW = texture.width();
        int texH = texture.height();

        int boxW = this.width;
        int boxH = this.height;

        float scaleX = (float) boxW / texW;
        float scaleY = (float) boxH / texH;

        float scale;
        float renderW, renderH, offsetX = 0, offsetY = 0;
        float u = 0, v = 0;
        int regionW = texW, regionH = texH;

        switch (renderMode) {
            case COVER -> {
                scale = Math.max(scaleX, scaleY);
                renderW = texW * scale;
                renderH = texH * scale;
                offsetX = (renderW - boxW) / 2f;
                offsetY = (renderH - boxH) / 2f;
                u = offsetX / renderW * texW;
                v = offsetY / renderH * texH;
                regionW = (int) (texW - 2 * u);
                regionH = (int) (texH - 2 * v);
                context.drawTexture(
                        RenderLayer::getGuiTextured,
                        image,
                        x, y,
                        u, v,
                        boxW, boxH,
                        regionW, regionH,
                        texW, texH,
                        filter
                );

            }

            case FIT -> {
                scale = Math.min(scaleX, scaleY);
                renderW = texW * scale;
                renderH = texH * scale;
                offsetX = (boxW - renderW) / 2f;
                offsetY = (boxH - renderH) / 2f;

                context.drawTexture(
                        RenderLayer::getGuiTextured,
                        image,
                        x + (int) offsetX,
                        x + (int) offsetY,
                        0f, 0f,
                        (int) renderW, (int) renderH,
                        (int) renderW, (int) renderH,
                        filter
                );
            }


            case STRETCH -> context.drawTexture(
                    RenderLayer::getGuiTextured,
                    image,
                    x, y,
                    0, 0,
                    boxW, boxH,
                    texW, texH,
                    filter
            );

            case CENTER -> {
                int drawX = x + (boxW - texW) / 2;
                int drawY = y + (boxH - texH) / 2;
                context.drawTexture(
                        RenderLayer::getGuiTextured,
                        image,
                        drawX, drawY,
                        0, 0,
                        texW, texH,
                        texW, texH,
                        filter
                );
            }

            case TILE -> {
                for (int tileX = 0; tileX < boxW; tileX += texW) {
                    for (int tileY = 0; tileY < boxH; tileY += texH) {
                        int drawW = Math.min(texW, boxW - tileX);
                        int drawH = Math.min(texH, boxH - tileY);
                        context.drawTexture(
                                RenderLayer::getGuiTextured,
                                image,
                                x + tileX, y + tileY,
                                0, 0,
                                drawW, drawH,
                                texW, texH,
                                filter
                        );
                    }
                }
            }

            case FILL_WIDTH -> {
                scale = scaleX;
                renderW = boxW;
                renderH = texH * scale;
                offsetY = (boxH - renderH) / 2f;

                System.out.println("FILL_WIDTH Debug:");
                System.out.println("  texW=" + texW + ", texH=" + texH);
                System.out.println("  boxW=" + boxW + ", boxH=" + boxH);
                System.out.println("  scaleX=" + scaleX + ", scale=" + scale);
                System.out.println("  renderW=" + renderW + ", renderH=" + renderH);
                System.out.println("  offsetY=" + offsetY);
                System.out.println("  Drawing at: x=" + x + ", y=" + (y + (int)offsetY));
                System.out.println("  Size: w=" + (int)renderW + ", h=" + (int)renderH);

                context.drawTexture(
                        RenderLayer::getGuiTextured,
                        image,
                        x,
                        y + (int) offsetY,
                        0, 0,
                        (int) renderW, (int) renderH,
                        texW, texH,
                        filter
                );
            }

            case FILL_HEIGHT -> {
                scale = scaleY;
                renderW = texW * scale;
                renderH = boxH;
                offsetX = (boxW - renderW) / 2f;

                System.out.println("FILL_HEIGHT Debug:");
                System.out.println("  texW=" + texW + ", texH=" + texH);
                System.out.println("  boxW=" + boxW + ", boxH=" + boxH);
                System.out.println("  scaleY=" + scaleY + ", scale=" + scale);
                System.out.println("  renderW=" + renderW + ", renderH=" + renderH);
                System.out.println("  offsetX=" + offsetX);
                System.out.println("  Drawing at: x=" + (x + (int)offsetX) + ", y=" + y);
                System.out.println("  Size: w=" + (int)renderW + ", h=" + (int)renderH);

                context.drawTexture(
                        RenderLayer::getGuiTextured,
                        image,
                        this.x + (int) offsetX,
                        this.y,
                        0, 0,
                        (int) renderW, (int) renderH,
                        texW, texH,
                        filter
                );
            }

            default -> context.drawTexture(
                RenderLayer::getGuiTextured,
                image,
                x, y,
                u, v,
                boxW, boxH,
                regionW, regionH,
                texW, texH,
                filter
            );

        }
    }

}
