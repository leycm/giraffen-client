package org.leycm.giraffe.client.ui.widgets;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.leycm.giraffe.client.module.common.BaseModule;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModuleCategoryList extends ClickableWidget {
    private static final int CATEGORY_HEIGHT = 36;
    private static final int MODULE_HEIGHT = 32;
    private static final int TOGGLE_BUTTON_WIDTH = 40;
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
            totalHeight += CATEGORY_HEIGHT;
            if (category.expanded) {
                totalHeight += category.modules.size() * MODULE_HEIGHT;
            }
        }
    }

    @Override
    public void renderWidget(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(getX(), getY(), getX() + width, getY() + height, 0xFF000000);

        context.fill(getX(), getY(), getX() + width, getY() + 1, 0xFF333333);
        context.fill(getX(), getY() + height - 1, getX() + width, getY() + height, 0xFF333333);
        context.fill(getX(), getY(), getX() + 1, getY() + height, 0xFF333333);
        context.fill(getX() + width - 1, getY(), getX() + width, getY() + height, 0xFF333333);

        context.enableScissor(getX(), getY(), getX() + width, getY() + height);

        int currentY = getY() - scrollY + 2;

        for (CategoryEntry category : categories) {
            if (currentY > getY() - CATEGORY_HEIGHT && currentY < getY() + height) {
                renderCategoryHeader(context, category, currentY, mouseX, mouseY);
            }
            currentY += CATEGORY_HEIGHT;

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

    private void renderCategoryHeader(DrawContext context, CategoryEntry category, int y, int mouseX, int mouseY) {
        boolean hovered = mouseX >= getX() && mouseX <= getX() + width &&
                mouseY >= y && mouseY <= y + CATEGORY_HEIGHT;

        if (hovered) {
            context.fill(getX() + 1, y, getX() + width - 1, y + CATEGORY_HEIGHT, 0xFF111111);
        }

        context.fill(getX() + 8, y + CATEGORY_HEIGHT - 1, getX() + width - 8, y + CATEGORY_HEIGHT, 0xFF222222);

        String icon = category.expanded ? "−" : "+";
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

        String count = String.valueOf(category.modules.size());
        int countWidth = getTextRenderer().getWidth(count);
        context.drawTextWithShadow(
                getTextRenderer(),
                count,
                getX() + width - countWidth - 12,
                y + (CATEGORY_HEIGHT - 8) / 2,
                0xFF888888
        );
    }

    private void renderModuleEntry(DrawContext context, BaseModule module, int y, int mouseX, int mouseY) {
        boolean hovered = mouseX >= getX() + 16 && mouseX <= getX() + width - TOGGLE_BUTTON_WIDTH - 8 &&
                mouseY >= y && mouseY <= y + MODULE_HEIGHT;
        boolean toggleHovered = mouseX >= getX() + width - TOGGLE_BUTTON_WIDTH && mouseX <= getX() + width - 8 &&
                mouseY >= y + 4 && mouseY <= y + MODULE_HEIGHT - 4;

        if (hovered) {
            context.fill(getX() + 16, y, getX() + width - TOGGLE_BUTTON_WIDTH - 8, y + MODULE_HEIGHT, 0xFF0A0A0A);
        }

        context.drawTextWithShadow(
                getTextRenderer(),
                module.getDisplayName(),
                getX() + 32,
                y + (MODULE_HEIGHT - 8) / 2,
                0xFFCCCCCC
        );

        boolean enabled = module.isRunning();

        int toggleBg = enabled ? 0xFFFFFFFF : 0xFF000000;
        if (toggleHovered) {
            toggleBg = enabled ? 0xFFEEEEEE : 0xFF0A0A0A;
        }

        context.fill(getX() + width - TOGGLE_BUTTON_WIDTH, y + 6,
                getX() + width - 8, y + MODULE_HEIGHT - 6, toggleBg);

        context.fill(getX() + width - TOGGLE_BUTTON_WIDTH, y + 6,
                getX() + width - 8, y + 7, 0xFF333333);
        context.fill(getX() + width - TOGGLE_BUTTON_WIDTH, y + MODULE_HEIGHT - 7,
                getX() + width - 8, y + MODULE_HEIGHT - 6, 0xFF333333);
        context.fill(getX() + width - TOGGLE_BUTTON_WIDTH, y + 6,
                getX() + width - TOGGLE_BUTTON_WIDTH + 1, y + MODULE_HEIGHT - 6, 0xFF333333);
        context.fill(getX() + width - 9, y + 6,
                getX() + width - 8, y + MODULE_HEIGHT - 6, 0xFF333333);

        // Toggle text
        String toggleText = enabled ? "ON" : "OFF";
        int toggleTextWidth = getTextRenderer().getWidth(toggleText);
        int textColor = enabled ? 0xFF000000 : 0xFFFFFFFF;

        context.drawTextWithShadow(
                getTextRenderer(),
                toggleText,
                getX() + width - TOGGLE_BUTTON_WIDTH / 2 - toggleTextWidth / 2,
                y + (MODULE_HEIGHT - 8) / 2,
                textColor
        );
    }

    private void renderScrollbar(@NotNull DrawContext context) {
        int scrollbarX = getX() + width - 6;
        int scrollbarHeight = Math.max(20, (height * height) / totalHeight);
        int scrollbarY = getY() + (scrollY * (height - scrollbarHeight)) / (totalHeight - height);

        context.fill(scrollbarX, getY(), scrollbarX + 4, getY() + height, 0xFF111111);

        context.fill(scrollbarX + 1, scrollbarY, scrollbarX + 3, scrollbarY + scrollbarHeight, 0xFF444444);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }

        int currentY = getY() - scrollY + 2;

        for (CategoryEntry category : categories) {
            if (mouseY >= currentY && mouseY <= currentY + CATEGORY_HEIGHT) {
                onCategoryToggled.accept(category.name);
                return true;
            }
            currentY += CATEGORY_HEIGHT;

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