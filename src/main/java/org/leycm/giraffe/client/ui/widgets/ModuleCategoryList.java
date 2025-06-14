package org.leycm.giraffe.client.ui.widgets;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.leycm.giraffe.client.module.common.BaseModule;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModuleCategoryList extends ClickableWidget {
    private static final int CATEGORY_HEIGHT = 32;
    private static final int MODULE_HEIGHT = 28;
    private static final int TOGGLE_BUTTON_WIDTH = 50;
    private static final int SCROLL_SPEED = 12;

    private final List<CategoryEntry> categories = new ArrayList<>();
    private final Consumer<BaseModule> onModuleSelected;
    private final Consumer<BaseModule> onModuleToggled;
    private final Consumer<String> onCategoryToggled;

    private int scrollY = 0;
    private int totalHeight = 0;

    public ModuleCategoryList(int x, int y, int width, int height,
                              Consumer<BaseModule> onModuleSelected,
                              Consumer<BaseModule> onModuleToggled,
                              Consumer<String> onCategoryToggled) {
        super(x, y, width, height, Text.literal("Categories"));
        this.onModuleSelected = onModuleSelected;
        this.onModuleToggled = onModuleToggled;
        this.onCategoryToggled = onCategoryToggled;
    }

    public void addCategory(String categoryName, List<BaseModule> modules, boolean expanded) {
        categories.add(new CategoryEntry(categoryName, modules, expanded));
        updateTotalHeight();
    }

    public void clear() {
        categories.clear();
        scrollY = 0;
        totalHeight = 0;
    }

    public void refresh() {
    }

    private void updateTotalHeight() {
        totalHeight = 0;
        for (CategoryEntry category : categories) {
            totalHeight += CATEGORY_HEIGHT + 2;
            if (category.expanded) {
                totalHeight += category.modules.size() * MODULE_HEIGHT;
            }
        }
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        drawGlassBackground(context);

        context.enableScissor(getX(), getY(), getX() + width, getY() + height);

        int currentY = getY() - scrollY + 4;

        for (CategoryEntry category : categories) {
            if (currentY > getY() - CATEGORY_HEIGHT && currentY < getY() + height) {
                renderCategoryHeader(context, category, currentY, mouseX, mouseY);
            }
            currentY += CATEGORY_HEIGHT + 2;

            if (category.expanded) {
                for (BaseModule module : category.modules) {
                    if (currentY > getY() - MODULE_HEIGHT && currentY < getY() + height) {
                        renderModuleEntry(context, module, currentY, mouseX, mouseY);
                    }
                    currentY += MODULE_HEIGHT;
                }
            }
        }

        context.disableScissor();

        if (totalHeight > height) {
            renderScrollbar(context);
        }
    }

    private void drawGlassBackground(DrawContext context) {
        context.fill(getX(), getY(), getX() + width, getY() + height, 0x35000000);

        context.fill(getX(), getY(), getX() + width, getY() + 1, 0x40FFFFFF);
        context.fill(getX(), getY() + height - 1, getX() + width, getY() + height, 0x20000000);
        context.fill(getX(), getY(), getX() + 1, getY() + height, 0x30FFFFFF);
        context.fill(getX() + width - 1, getY(), getX() + width, getY() + height, 0x15000000);
    }

    private void renderCategoryHeader(DrawContext context, CategoryEntry category, int y, int mouseX, int mouseY) {
        boolean hovered = mouseX >= getX() && mouseX <= getX() + width &&
                mouseY >= y && mouseY <= y + CATEGORY_HEIGHT;

        int bgColor = hovered ? 0x50FFFFFF : 0x25FFFFFF;
        context.fill(getX() + 4, y, getX() + width - 4, y + CATEGORY_HEIGHT, bgColor);

        if (hovered) {
            context.fill(getX() + 4, y, getX() + width - 4, y + 1, 0x60FFFFFF);
            context.fill(getX() + 4, y + CATEGORY_HEIGHT - 1, getX() + width - 4, y + CATEGORY_HEIGHT, 0x30000000);
        }

        String icon = category.expanded ? "▼" : "▶";
        context.drawTextWithShadow(
                getTextRenderer(),
                icon,
                getX() + 12,
                y + (CATEGORY_HEIGHT - 8) / 2,
                0xFFFFFFFF
        );

        context.drawTextWithShadow(
                getTextRenderer(),
                category.name,
                getX() + 32,
                y + (CATEGORY_HEIGHT - 8) / 2,
                0xFFFFFFFF
        );

        String count = "(" + category.modules.size() + ")";
        int countWidth = getTextRenderer().getWidth(count);
        context.drawTextWithShadow(
                getTextRenderer(),
                count,
                getX() + width - countWidth - 12,
                y + (CATEGORY_HEIGHT - 8) / 2,
                0xBBFFFFFF
        );
    }

    private void renderModuleEntry(DrawContext context, BaseModule module, int y, int mouseX, int mouseY) {
        boolean hovered = mouseX >= getX() + 16 && mouseX <= getX() + width - TOGGLE_BUTTON_WIDTH - 4 &&
                mouseY >= y && mouseY <= y + MODULE_HEIGHT;
        boolean toggleHovered = mouseX >= getX() + width - TOGGLE_BUTTON_WIDTH && mouseX <= getX() + width - 4 &&
                mouseY >= y && mouseY <= y + MODULE_HEIGHT;

        int bgColor = hovered ? 0x40FFFFFF : 0x00000000;
        context.fill(getX() + 16, y, getX() + width - TOGGLE_BUTTON_WIDTH - 4, y + MODULE_HEIGHT, bgColor);

        context.drawTextWithShadow(
                getTextRenderer(),
                module.getDisplayName(),
                getX() + 28,
                y + (MODULE_HEIGHT - 8) / 2,
                0xEEFFFFFF
        );

        boolean enabled = module.isRunning();
        int toggleBgColor;
        int toggleBorderColor;

        if (enabled) {
            toggleBgColor = toggleHovered ? 0x80007BFF : 0x60007BFF;
            toggleBorderColor = 0x90007BFF;
        } else {
            toggleBgColor = toggleHovered ? 0x50FFFFFF : 0x30FFFFFF;
            toggleBorderColor = 0x40FFFFFF;
        }

        context.fill(getX() + width - TOGGLE_BUTTON_WIDTH, y + 4, getX() + width - 4, y + MODULE_HEIGHT - 4, toggleBgColor);

        context.fill(getX() + width - TOGGLE_BUTTON_WIDTH, y + 4, getX() + width - 4, y + 5, toggleBorderColor);
        context.fill(getX() + width - TOGGLE_BUTTON_WIDTH, y + MODULE_HEIGHT - 5, getX() + width - 4, y + MODULE_HEIGHT - 4, 0x40000000);
        context.fill(getX() + width - TOGGLE_BUTTON_WIDTH, y + 4, getX() + width - TOGGLE_BUTTON_WIDTH + 1, y + MODULE_HEIGHT - 4, toggleBorderColor);
        context.fill(getX() + width - 5, y + 4, getX() + width - 4, y + MODULE_HEIGHT - 4, 0x30000000);

        String toggleText = enabled ? "ON" : "OFF";
        int toggleTextWidth = getTextRenderer().getWidth(toggleText);
        context.drawTextWithShadow(
                getTextRenderer(),
                toggleText,
                getX() + width - TOGGLE_BUTTON_WIDTH / 2 - toggleTextWidth / 2,
                y + (MODULE_HEIGHT - 8) / 2,
                0xFFFFFFFF
        );
    }

    private void renderScrollbar(DrawContext context) {
        int scrollbarX = getX() + width - 8;
        int scrollbarHeight = Math.max(20, (height * height) / totalHeight);
        int scrollbarY = getY() + (scrollY * (height - scrollbarHeight)) / (totalHeight - height);

        context.fill(scrollbarX, getY(), scrollbarX + 8, getY() + height, 0x20FFFFFF);
        context.fill(scrollbarX + 2, scrollbarY, scrollbarX + 6, scrollbarY + scrollbarHeight, 0x80FFFFFF);

        context.fill(scrollbarX + 2, scrollbarY, scrollbarX + 6, scrollbarY + 1, 0xAAFFFFFF);
        context.fill(scrollbarX + 2, scrollbarY + scrollbarHeight - 1, scrollbarX + 6, scrollbarY + scrollbarHeight, 0x40000000);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }

        int currentY = getY() - scrollY + 4;

        for (CategoryEntry category : categories) {
            if (mouseY >= currentY && mouseY <= currentY + CATEGORY_HEIGHT) {
                onCategoryToggled.accept(category.name);
                return true;
            }
            currentY += CATEGORY_HEIGHT + 2;

            if (category.expanded) {
                for (BaseModule module : category.modules) {
                    if (mouseY >= currentY && mouseY <= currentY + MODULE_HEIGHT) {
                        if (mouseX >= getX() + width - TOGGLE_BUTTON_WIDTH) {
                            onModuleToggled.accept(module);
                        } else {
                            onModuleSelected.accept(module);
                        }
                        return true;
                    }
                    currentY += MODULE_HEIGHT;
                }
            }
        }

        return false;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!isMouseOver(mouseX, mouseY) || totalHeight <= height) {
            return false;
        }

        scrollY = Math.max(0, Math.min(totalHeight - height,
                scrollY - (int)(verticalAmount * SCROLL_SPEED)));
        return true;
    }

    private net.minecraft.client.font.TextRenderer getTextRenderer() {
        return net.minecraft.client.MinecraftClient.getInstance().textRenderer;
    }

    private static class CategoryEntry {
        final String name;
        final List<BaseModule> modules;
        boolean expanded;

        CategoryEntry(String name, List<BaseModule> modules, boolean expanded) {
            this.name = name;
            this.modules = new ArrayList<>(modules);
            this.expanded = expanded;
        }
    }
}