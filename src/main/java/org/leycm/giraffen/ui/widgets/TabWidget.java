package org.leycm.giraffen.ui.widgets;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.leycm.giraffen.Client;
import org.leycm.giraffen.ui.UiRenderCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TabWidget implements Drawable, Element {
    private int x, y, width, height;
    private final Text title;
    private final boolean collapsible, closeable;
    private final boolean scrollable, moveable;
    private final int gridColumns;
    private final UiRenderCallback<String> footerText;
    private final Consumer<TabWidget> onUpdate;

    private boolean collapsed = false;
    private boolean visible = true;
    private boolean isDragging = false;
    private double dragStartX = 0;
    private double dragStartY = 0;
    private int scrollOffset = 0;
    private int contentHeight = 0;

    private final List<GridElement> gridElements = new ArrayList<>();
    private static final List<TabWidget> allTabWidgets = new ArrayList<>();

    private static final List<TabWidget> dependentTabs = new ArrayList<>();

    private final int headerHeight = 20;
    private final int footerHeight = 16;

    private static final int SNAP_THRESHOLD = 4;
    private static final int GRID_SIZE = 1;
    private static final int HORIZONTAL_SNAP_GAP = 5;
    private static final int VERTICAL_BOTTOM_GAP = 5;

    private static final int BACKGROUND_COLOR = 0x90000000;
    private static final int HEADER_COLOR = 0xA0111111;
    private static final int FOOTER_COLOR = 0xA0111111;
    private static final int BORDER_COLOR = 0xFF333333;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int BUTTON_COLOR = 0xFF444444;
    private static final int BUTTON_HOVER_COLOR = 0xFF555555;
    private static final int CLOSE_BUTTON_COLOR = 0xFF664444;
    private static final int CLOSE_BUTTON_HOVER_COLOR = 0xFFAA4444;
    private static final int SNAP_INDICATOR_COLOR = 0x0F00FF00;

    public TabWidget(int x, int y,
                     int width, int height,
                     Text title,
                     boolean collapsible, boolean closeable,
                     boolean scrollable, boolean moveable,
                     int gridColumns,
                     UiRenderCallback<String> footerText,
                     Consumer<TabWidget> onUpdate) {
        this.x = x; this.y = y;
        this.width = width; this.height = height;
        this.title = title;

        this.collapsible = collapsible;
        this.closeable = closeable;
        this.moveable = moveable;
        this.scrollable = scrollable;

        this.gridColumns = gridColumns;
        this.footerText = footerText;
        this.onUpdate = onUpdate;

        allTabWidgets.add(this);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!visible) return;

        int actualHeight = collapsed ? headerHeight : height;

        context.fill(x, y, x + width, y + actualHeight, BACKGROUND_COLOR);

        drawBorder(context, actualHeight);

        if (isDragging) {
            drawSnapIndicators(context);
        }

        renderHeader(context, mouseX, mouseY);

        if (!collapsed) {
            renderContent(context, mouseX, mouseY);
            renderFooter(context);
        }
    }

    private void drawSnapIndicators(DrawContext context) {
        SnapResult snapResult = calculateSnapPosition(x, y);

        if (snapResult.snapX != x) {
            context.fill(snapResult.snapX, 0, snapResult.snapX + 1, context.getScaledWindowHeight(), SNAP_INDICATOR_COLOR);
        }

        if (snapResult.snapY != y) {
            context.fill(0, snapResult.snapY, context.getScaledWindowWidth(), snapResult.snapY + 1, SNAP_INDICATOR_COLOR);
        }
    }

    private void drawBorder(@NotNull DrawContext context, int actualHeight) {
        context.fill(x, y, x + width, y + 1, BORDER_COLOR);
        context.fill(x, y + actualHeight - 1, x + width, y + actualHeight, BORDER_COLOR);
        context.fill(x, y, x + 1, y + actualHeight, BORDER_COLOR);
        context.fill(x + width - 1, y, x + width, y + actualHeight, BORDER_COLOR);
    }

    private void renderHeader(@NotNull DrawContext context, int mouseX, int mouseY) {
        context.fill(x, y, x + width, y + headerHeight, HEADER_COLOR);

        int buttonSize = 16;
        int buttonY = y + 2;
        int currentButtonOffset = 0;

        if (closeable) {
            int closeButtonX = x + width - buttonSize - 2 - currentButtonOffset;
            boolean closeHovered = isMouseOverButton(mouseX, mouseY, closeButtonX, buttonY, buttonSize);

            context.fill(closeButtonX, buttonY, closeButtonX + buttonSize, buttonY + buttonSize,
                    closeHovered ? CLOSE_BUTTON_HOVER_COLOR : CLOSE_BUTTON_COLOR);

            context.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                    "×", closeButtonX + 5, buttonY + 4, TEXT_COLOR, false);

            currentButtonOffset += buttonSize + 2;
        }

        if (collapsible) {
            int collapseButtonX = x + width - buttonSize - 2 - currentButtonOffset;
            boolean collapseHovered = isMouseOverButton(mouseX, mouseY, collapseButtonX, buttonY, buttonSize);

            context.fill(collapseButtonX, buttonY, collapseButtonX + buttonSize, buttonY + buttonSize,
                    collapseHovered ? BUTTON_HOVER_COLOR : BUTTON_COLOR);

            String symbol = collapsed ? "+" : "−";
            context.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                    symbol, collapseButtonX + 5, buttonY + 4, TEXT_COLOR, false);
        }

        context.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                title, x + 6, y + 6, TEXT_COLOR, false);

        context.fill(x, y + headerHeight, x + width, y + headerHeight + 1, BORDER_COLOR);
    }

    private void renderContent(@NotNull DrawContext context, int mouseX, int mouseY) {
        int contentY = y + headerHeight + 1;
        int contentAreaHeight = height - headerHeight - footerHeight - 2;

        context.fill(x, contentY, x + width, contentY + contentAreaHeight, BACKGROUND_COLOR);

        if (scrollable) {
            context.enableScissor(x + 1, contentY, x + width - 1, contentY + contentAreaHeight);
        }

        renderGrid(context, contentY);

        if (scrollable) {
            context.disableScissor();
        }
    }

    private void renderGrid(DrawContext context, int startY) {
        if (gridElements.isEmpty()) return;

        int gridStartX = x + 4;
        int gridStartY = startY + 4 - scrollOffset;
        int cellWidth = (width - 8) / gridColumns;
        int cellHeight = 20;

        int row = 0;
        int col = 0;

        for (GridElement element : gridElements) {
            int cellX = gridStartX + col * cellWidth;
            int cellY = gridStartY + row * cellHeight;

            int contentAreaHeight = height - headerHeight - footerHeight - 2;
            if (cellY + cellHeight >= startY && cellY < startY + contentAreaHeight) {
                context.fill(cellX, cellY, cellX + cellWidth - 2, cellY + cellHeight - 2, 0x40FFFFFF);

                context.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                        element.getText(), cellX + 2, cellY + 6, TEXT_COLOR, false);
            }

            col++;
            if (col >= gridColumns) {
                col = 0;
                row++;
            }
        }

        contentHeight = (row + (col > 0 ? 1 : 0)) * cellHeight;
    }

    private void renderFooter(@NotNull DrawContext context) {
        int footerY = y + height - footerHeight;

        context.fill(x, footerY, x + width, y + height, FOOTER_COLOR);

        context.fill(x, footerY, x + width, footerY + 1, BORDER_COLOR);

        if (footerText != null) {
            context.drawText(Client.MC.textRenderer, footerText.render(this), x + 4, footerY + 4, TEXT_COLOR, false);
        }
    }

    private boolean isMouseOverButton(double mouseX, double mouseY, int buttonX, int buttonY, int buttonSize) {
        return mouseX >= buttonX && mouseX < buttonX + buttonSize &&
                mouseY >= buttonY && mouseY < buttonY + buttonSize;
    }

    private boolean isMouseOverHeader(double mouseX, double mouseY) {
        if (!visible) return false;
        return mouseX >= x && mouseX < x + width &&
                mouseY >= y && mouseY < y + headerHeight;
    }

    private boolean isMouseOverButtons(double mouseX, double mouseY) {
        int buttonSize = 16;
        int buttonY = y + 2;

        int totalButtonsWidth = 0;
        if (closeable) totalButtonsWidth += buttonSize + 2;
        if (collapsible) totalButtonsWidth += buttonSize + 2;

        if (totalButtonsWidth == 0) return false;

        int buttonsStartX = x + width - totalButtonsWidth;

        return mouseX >= buttonsStartX && mouseX < x + width &&
                mouseY >= buttonY && mouseY < buttonY + buttonSize;
    }

    @Contract("_, _ -> new")
    private @NotNull SnapResult calculateSnapPosition(int newX, int newY) {
        int snapX;
        int snapY;

        snapX = Math.round((float) newX / GRID_SIZE) * GRID_SIZE;
        snapY = Math.round((float) newY / GRID_SIZE) * GRID_SIZE;

        for (TabWidget other : allTabWidgets) {
            if (other == this || !other.visible) continue;

            int otherActualHeight = other.collapsed ? other.headerHeight : other.height;

            if (Math.abs(newX - (other.x + other.width + HORIZONTAL_SNAP_GAP)) < SNAP_THRESHOLD) {
                snapX = other.x + other.width + HORIZONTAL_SNAP_GAP;
            } else if (Math.abs(newX + width + HORIZONTAL_SNAP_GAP - other.x) < SNAP_THRESHOLD) {
                snapX = other.x - width - HORIZONTAL_SNAP_GAP;
            } else if (Math.abs(newX - other.x) < SNAP_THRESHOLD) {
                snapX = other.x;
            } else if (Math.abs(newX + width - other.x) < SNAP_THRESHOLD) {
                snapX = other.x - width;
            } else if (Math.abs(newX - (other.x + other.width)) < SNAP_THRESHOLD) {
                snapX = other.x + other.width;
            } else if (Math.abs(newX + width - (other.x + other.width)) < SNAP_THRESHOLD) {
                snapX = other.x + other.width - width;
            }

            if (Math.abs(newY - other.y) < SNAP_THRESHOLD) {
                snapY = other.y;
            } else if (Math.abs(newY + height - other.y) < SNAP_THRESHOLD) {
                snapY = other.y - height;
            } else if (Math.abs(newY - (other.y + otherActualHeight + VERTICAL_BOTTOM_GAP)) < SNAP_THRESHOLD) {
                snapY = other.y + otherActualHeight + VERTICAL_BOTTOM_GAP;
            } else if (Math.abs(newY + height - (other.y + otherActualHeight)) < SNAP_THRESHOLD) {
                snapY = other.y + otherActualHeight - height;
            }

        }

        return new SnapResult(snapX, snapY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || button != 0) return false;

        if (isMouseOverHeader(mouseX, mouseY)) {
            int buttonSize = 16;
            int buttonY = y + 2;
            int currentButtonOffset = 0;

            if (closeable) {
                int closeButtonX = x + width - buttonSize - 2 - currentButtonOffset;
                if (isMouseOverButton(mouseX, mouseY, closeButtonX, buttonY, buttonSize)) {
                    visible = false;
                    allTabWidgets.remove(this);
                    if (onUpdate != null) onUpdate.accept(this);
                    return true;
                }
                currentButtonOffset += buttonSize + 2;
            }

            if (collapsible) {
                int collapseButtonX = x + width - buttonSize - 2 - currentButtonOffset;
                if (isMouseOverButton(mouseX, mouseY, collapseButtonX, buttonY, buttonSize)) {
                    toggleCollapsed();
                    return true;
                }
            }

            if (moveable && !isMouseOverButtons(mouseX, mouseY)) {
                isDragging = true;
                dragStartX = mouseX - x;
                dragStartY = mouseY - y;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!visible || button != 0 || !isDragging || !moveable) return false;

        int newX = (int)(mouseX - dragStartX);
        int newY = (int)(mouseY - dragStartY);

        // Apply snapping
        SnapResult snapResult = calculateSnapPosition(newX, newY);
        x = snapResult.snapX;
        y = snapResult.snapY;

        if (onUpdate != null) onUpdate.accept(this);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && isDragging) {
            isDragging = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!visible || collapsed || !scrollable) return false;

        int contentY = y + headerHeight + 1;
        int contentAreaHeight = height - headerHeight - footerHeight - 2;

        if (mouseX >= x && mouseX < x + width &&
                mouseY >= contentY && mouseY < contentY + contentAreaHeight) {

            int maxScroll = Math.max(0, contentHeight - contentAreaHeight);
            scrollOffset = MathHelper.clamp(scrollOffset - (int)(verticalAmount * 10), 0, maxScroll);
            return true;
        }

        return false;
    }

    public void move(int x, int y) {
        Client.LOGGER.info("x=" + x + "  y=" + y);
        Client.LOGGER.info("altX= " + getX() + "  altY=" + getY());
        Client.LOGGER.info("newX=" + (getX() + x) + "  newY=" + (getY() + y));
        setPosition(getX() + x, getY() + y);
    }

    public void addDependentTab(TabWidget widget) {
        dependentTabs.add(widget);
    }

    public void removeDependentTab(TabWidget widget) {
        dependentTabs.remove(widget);
    }

    public List<TabWidget> getDependentTab() {
        return dependentTabs;
    }

    public void addGridElement(String text) {
        gridElements.add(new GridElement(text));
    }

    public void removeGridElement(int index) {
        if (index >= 0 && index < gridElements.size()) {
            gridElements.remove(index);
        }
    }

    public void clearGrid() {gridElements.clear();}

    public boolean isCollapsed() {return collapsed;}

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
        dependentTabs.forEach(tab -> tab.move(0, (this.height - this.headerHeight) * (collapsed ? -1 : 1)));

        if (onUpdate != null) onUpdate.accept(this);
    }

    public void toggleCollapsed() {setCollapsed(!collapsed);}

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (onUpdate != null) onUpdate.accept(this);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static void clearAllWidgets() {
        allTabWidgets.clear();
    }

    // Getters for position and dimensions
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    // Getters for the enhanced features
    public boolean isCloseable() { return closeable; }
    public boolean isMoveable() { return moveable; }
    public boolean isCollapsible() { return collapsible; }
    public boolean isScrollable() { return scrollable; }

    @Override
    public void setFocused(boolean focused) {}

    @Override
    public boolean isFocused() {
        return false;
    }

    // Helper class for snap calculations
    private static class SnapResult {
        final int snapX;
        final int snapY;

        SnapResult(int snapX, int snapY) {
            this.snapX = snapX;
            this.snapY = snapY;
        }
    }

    private static class GridElement {
        private final String text;

        public GridElement(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}