package org.leycm.giraffe.client.ui.widgets;
import com.mojang.blaze3d.systems.RenderSystem;


import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.*;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import org.leycm.giraffe.client.identifier.CachedIdentifier;

public class MediaWidget extends ClickableWidget {

    private final CachedIdentifier texture;
    private final Identifier image;

    public MediaWidget(int x, int y, int width, int height, @NotNull CachedIdentifier texture) {
        super(x, y, width, height, Text.of(""));
        this.texture = texture;
        this.image = texture.identifier();
    }

    @Override
    protected void renderWidget(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderTexture(0, image);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableBlend();

        int texW = texture.width();
        int texH = texture.height();

        int boxW = this.width;
        int boxH = this.height;

        float scaleX = (float) boxW / texW;
        float scaleY = (float) boxH / texH;

        float u = 0f;
        float v = 0f;
        int regionW = texW;
        int regionH = texH;

        if (scaleX > scaleY) {
            float renderedW = texW * scaleY;
            float crop = (renderedW - boxW) / scaleY / 2f;
            u = crop;
            regionW = (int) (texW - crop * 2);
        } else {
            float renderedH = texH * scaleX;
            float crop = (renderedH - boxH) / scaleX / 2f;
            v = crop;
            regionH = (int) (texH - crop * 2);
        }

        int x = this.getX();
        int y = this.getY();

        context.drawTexture(
                RenderLayer::getGuiTextured,
                image,
                x, y,
                u, v,
                boxW, boxH,
                regionW, regionH,
                texW, texH,
                0xFFFFFFFF
        );
    }


    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
