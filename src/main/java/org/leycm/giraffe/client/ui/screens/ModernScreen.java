package org.leycm.giraffe.client.ui.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import org.leycm.giraffe.client.Client;
import org.leycm.giraffe.client.identifier.CachedIdentifier;
import org.leycm.giraffe.client.ui.ScreenHandler;
import org.leycm.giraffe.client.ui.widgets.MediaWidget;

import java.util.function.Predicate;

public abstract class ModernScreen extends Screen {
    private final CachedIdentifier backGround;

    protected ModernScreen(String id, CachedIdentifier texture) {
        super(Text.literal(""));
        ScreenHandler.register(id, this);
        this.backGround = texture;
    }

    @Override
    protected void init() {
        super.init();

        int mediaWidth = Math.min(backGround.width() / 2, width);
        int mediaHeight = Math.min(backGround.height() / 2, height);
        int mediaX = width - mediaWidth;
        int mediaY = height - mediaHeight;

        MediaWidget mediaWidget = new MediaWidget(mediaX, mediaY, mediaWidth, mediaHeight, backGround, MediaWidget.RenderMode.COVER, 0x30FFFFFF);
        addDrawable(mediaWidget);
        //hide hud

        onInit();
    }

    @Override
    public void render(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        super.applyBlur();
        context.fill(0, 0, width, height, 0xB0000000);
        super.render(context, mouseX, mouseY, delta);
        onRender();
    }

    protected abstract void onRender();
    protected abstract void onInit();

    @Override
    protected void applyBlur() {}

    @Override
    public void close() {
        super.close();
        Client.MC.options.hudHidden = false;
        Client.MC.options.getGuiScale().setValue(ScreenHandler.getOriginalGuiScale());
        Client.MC.onResolutionChanged();
    }

    public void clear() {
        children().forEach(super::remove);
    }

    public void clear(Predicate<Element> predicate) {
        children().forEach(child -> { if(predicate.test(child)) super.remove(child); });
    }

    @Override
    protected <T extends Element & Drawable & Selectable> T addDrawableChild(T child) {
        return super.addDrawableChild(child);
    }

    @Override
    protected <T extends Element & Selectable> T addSelectableChild(T child) {
        return super.addSelectableChild(child);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
