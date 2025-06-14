package org.leycm.giraffe.client.ui.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.NotNull;
import org.leycm.giraffe.client.Client;
import org.leycm.giraffe.client.identifier.CachedIdentifier;
import org.leycm.giraffe.client.identifier.IdentifierRegistry;
import org.leycm.giraffe.client.ui.ScreenHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class ModernScreen extends Screen {
    private static final CachedIdentifier IMAGE = IdentifierRegistry.loadTexture("C:\\Users\\Admin\\Pictures\\Screenshots\\Screenshot 2025-06-02 182334.png");

    Set<Element> children = new HashSet<>();

    protected ModernScreen(String id, String title) {
        super(Text.literal(title));
        ScreenHandler.register(id, this);
    }

    @Override
    protected void init() {
        super.init();
        onInit();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderCustomBackground(context);

        onRender();

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderCustomBackground(@NotNull DrawContext context) {
        context.fill(0, 0, this.width, this.height, 0x60000000);

        float aspectRatio = (float) IMAGE.width() / IMAGE.height();

        int newHeight = (int)(this.width * aspectRatio);

        int yPos = this.height - newHeight;

        Function<Identifier, RenderLayer> renderLayers = id -> RenderLayer.getGui();

        context.drawTexture(
                renderLayers,
                IMAGE.identifier(),
                0, yPos,
                0, 0,
                this.width, newHeight,
                IMAGE.width(), IMAGE.height(),
                0x80FFFFFF
        );

        context.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);
    }

    protected abstract void onRender();
    protected abstract void onInit();

    @Override
    protected void applyBlur() {}

    @Override
    public void close() {
        super.close();
        Client.MC.options.getGuiScale().setValue(ScreenHandler.getOriginalGuiScale());
        Client.MC.onResolutionChanged();
    }

    public void clear() {
        children.forEach(super::remove);
    }

    public void clear(Predicate<Element> predicate) {
        children.forEach(child -> { if(predicate.test(child)) super.remove(child); });
    }

    @Override
    protected <T extends Element & Drawable & Selectable> T addDrawableChild(T child) {
        children.add(child);
        return super.addDrawableChild(child);
    }

    @Override
    protected <T extends Element & Selectable> T addSelectableChild(T child) {
        children.add(child);
        return super.addSelectableChild(child);
    }
}
