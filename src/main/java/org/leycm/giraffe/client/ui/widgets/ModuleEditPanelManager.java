package org.leycm.giraffe.client.ui.widgets;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.leycm.giraffe.client.module.common.BaseModule;

import java.util.ArrayList;
import java.util.List;

public class ModuleEditPanelManager extends ClickableWidget {
    private static final int COLUMNS = 3;
    private static final int PANEL_SPACING = 7;
    private static final int PANEL_HEIGHT = 300;

    private static final List<ModuleEditPanel> editPanels = new ArrayList<>();
    private int panelWidth;
    private int scrollOffset = 0;

    public ModuleEditPanelManager(int x, int y, int width, int height) {
        super(x, y, width, height, Text.literal("Module Panel Manager"));
        calculatePanelDimensions();
    }

    private void calculatePanelDimensions() {
        int availableWidth = width - (COLUMNS - 1) * PANEL_SPACING;
        this.panelWidth = 170;
    }

    public void addOrSelectModule(BaseModule module) {
        for (ModuleEditPanel panel : editPanels) {
            if (panel.getSelectedModule() == module) return;
        }

        ModuleEditPanel available = findAvailablePanel();
        if (available != null) {
            available.setSelectedModule(module);
        } else {
            ModuleEditPanel panel = new ModuleEditPanel(0, 0, panelWidth, PANEL_HEIGHT, this::onPanelClosed);
            panel.setSelectedModule(module);
            editPanels.add(panel);
        }

        reorganizePanels();
    }

    private @Nullable ModuleEditPanel findAvailablePanel() {
        for (ModuleEditPanel panel : editPanels) {
            if (panel.getSelectedModule() == null) return panel;
        }
        return null;
    }

    private void onPanelClosed(@NotNull ModuleEditPanel panel) {
        panel.setSelectedModule(null);
        reorganizePanels();
    }

    private void reorganizePanels() {
        List<ModuleEditPanel> active = new ArrayList<>();
        for (ModuleEditPanel p : editPanels) {
            if (p.getSelectedModule() != null) active.add(p);
        }

        for (int i = 0; i < active.size(); i++) {
            int col = i % COLUMNS;
            int row = i / COLUMNS;

            int px = getX() + col * (panelWidth + PANEL_SPACING);
            int py = getY() + row * (PANEL_HEIGHT + PANEL_SPACING) - scrollOffset;

            active.get(i).setPosition(px, py);
        }

        editPanels.clear();
        editPanels.addAll(active);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (editPanels.isEmpty()) {
            renderEmptyState(context);
        } else {
            for (ModuleEditPanel panel : editPanels) {
                panel.renderWidget(context, mouseX, mouseY, delta);
            }
        }
    }

    private void renderEmptyState(@NotNull DrawContext context) {
        String emptyText = "Select modules from the sidebar to edit them here";
        int textWidth = getTextRenderer().getWidth(emptyText);

        context.drawCenteredTextWithShadow(
                getTextRenderer(), "⚙",
                getX() + width / 2, getY() + height / 2 - 20, 0x90555555
        );

        context.drawCenteredTextWithShadow(
                getTextRenderer(), emptyText,
                getX() + width / 2, getY() + height / 2 + 5, 0x90888888
        );
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (ModuleEditPanel panel : editPanels) {
            if (panel.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
                return true;
            }
        }

        int scrollAmount = (int) (verticalAmount * 20);
        scrollOffset = Math.max(0, scrollOffset - scrollAmount);
        reorganizePanels();
        return true;
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (ModuleEditPanel panel : editPanels) {
            if (panel.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void appendClickableNarrations(@NotNull NarrationMessageBuilder builder) {
        builder.put(net.minecraft.client.gui.screen.narration.NarrationPart.TITLE, "Module Edit Panel Manager");
    }

    private net.minecraft.client.font.TextRenderer getTextRenderer() {
        return net.minecraft.client.MinecraftClient.getInstance().textRenderer;
    }
}
