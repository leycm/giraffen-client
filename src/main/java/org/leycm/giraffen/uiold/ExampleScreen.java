package org.leycm.giraffen.uiold;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.leycm.giraffen.ui.ScreenHandler;
import org.leycm.giraffen.ui.widgets.TabWidget;

import java.util.ArrayList;
import java.util.List;

public class ExampleScreen extends Screen {
    private final List<TabWidget> widgets = new ArrayList<>();

    public ExampleScreen() {
        super(Text.literal("ModernWidget Demo"));

        ScreenHandler.register("module-menu-screens", this);
        TabWidget inventoryWidget = new TabWidget(
                50, 50,
                200, 150,
                Text.literal("Inventory Manager"),
                true,
                true,
                true,
                true,
                4,
                (element) -> "Items: 32/64",
                this::onWidgetUpdate
        );

        for (int i = 0; i < 16; i++) {
            inventoryWidget.addGridElement("Item " + (i + 1));
        }

        widgets.add(inventoryWidget);

        TabWidget chatWidget = new TabWidget(
                300, 80,
                180, 120,
                Text.literal("Chat Messages"),
                true,
                true,
                true,
                true,
                1,
                (element) -> "Online: 12",
                this::onWidgetUpdate
        );

        for (int i = 0; i < 20; i++) {
            chatWidget.addGridElement("Player" + i + ": Hello World!");
        }

        widgets.add(chatWidget);

        TabWidget settingsWidget = new TabWidget(
                100, 220,
                250, 100,
                Text.literal("Quick Settings"),
                false,
                true,
                true,
                true,
                2,
                (element) -> "Config v1.2",
                this::onWidgetUpdate
        );

        widgets.add(settingsWidget);

        TabWidget modules = new TabWidget(
                300, 80,
                180, 120,
                Text.literal("Modules"),
                true,
                false,
                true,
                false,
                1,
                (element) -> "Online: 12",
                this::onWidgetUpdate
        );

        settingsWidget.addGridElement("Sound: ON");
        settingsWidget.addGridElement("Graphics: HIGH");
        settingsWidget.addGridElement("FOV: 90°");
        settingsWidget.addGridElement("Sensitivity: 50%");

        widgets.add(modules);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Show All"), button -> {
            widgets.forEach(widget -> widget.setVisible(true));
        }).dimensions(10, height - 30, 80, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Toggle Collapse"), button -> {
            widgets.forEach(widget -> {
                if (widget.isVisible()) {
                    widget.setCollapsed(!widget.isCollapsed());
                }
            });
        }).dimensions(100, height - 30, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Reset Positions"), button -> {
            resetWidgetPositions();
        }).dimensions(210, height - 30, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Close"), button -> {
            this.close();
        }).dimensions(width - 60, height - 30, 50, 20).build());

    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 10, 0xFFFFFF);

        for (int i = widgets.size() - 1; i >= 0; i--) {
            TabWidget widget = widgets.get(i);
            if (widget.isVisible()) {
                widget.render(context, mouseX, mouseY, delta);
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (TabWidget widget : widgets) {
            if (widget.isVisible() && widget.mouseClicked(mouseX, mouseY, button)) {
                widgets.remove(widget);
                widgets.add(0, widget);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (TabWidget widget : widgets) {
            if (widget.isVisible() && widget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (TabWidget widget : widgets) {
            if (widget.isVisible() && widget.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (TabWidget widget : widgets) {
            if (widget.isVisible() && widget.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
                return true;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private void onWidgetUpdate(TabWidget widget) {}

    private void resetWidgetPositions() {
        if (widgets.size() >= 3) {
            widgets.get(0).setPosition(50, 50);
            widgets.get(1).setPosition(300, 80);
            widgets.get(2).setPosition(100, 220);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void applyBlur() {}
}