package org.leycm.giraffe.client.ui.widgets;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.leycm.giraffe.client.module.common.BaseModule;
import org.leycm.giraffe.client.settings.Setting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModuleEditPanel extends ClickableWidget {
    private static final int HEADER_HEIGHT = 50;
    private static final int BUTTON_HEIGHT = 25;
    private static final int SETTING_HEIGHT = 40;
    private static final int SETTING_TAB_HEIGHT = 36;
    private static final int SCROLL_SPEED = 15;
    private static final int PADDING = 16;
    private static final int BUTTON_SIZE = 24;

    private BaseModule selectedModule;
    private final Consumer<ModuleEditPanel> onPanelClosed;
    private final List<ModuleSettingTab> settingTabs = new ArrayList<>();

    private int scrollY = 0;
    private int contentHeight = 0;
    private boolean minimized = false;

    public ModuleEditPanel(int x, int y, int width, int height, Consumer<ModuleEditPanel> onPanelClosed) {
        super(x, y, width, height, Text.literal("Module Editor"));
        this.onPanelClosed = onPanelClosed;
    }

    public void setSelectedModule(BaseModule module) {
        this.selectedModule = module;
        this.minimized = false;
        this.scrollY = 0;
        updateSettingTabs();
        calculateContentHeight();
    }

    public BaseModule getSelectedModule() {
        return selectedModule;
    }

    public void setPosition(int x, int y) {
        this.setX(x);
        this.setY(y);
    }

    private void updateSettingTabs() {
        settingTabs.clear();
        if (selectedModule != null) {
            for (Setting setting : selectedModule.getSettings()) {
                String groupName = setting.getGroup() != null ?
                        setting.getGroup().displayname() : "General";

                ModuleSettingTab tab = settingTabs.stream()
                        .filter(t -> t.name.equals(groupName))
                        .findFirst()
                        .orElse(null);

                if (tab == null) {
                    tab = new ModuleSettingTab(groupName);
                    settingTabs.add(tab);
                }

                tab.settings.add(setting);
            }
        }
    }

    private void calculateContentHeight() {
        contentHeight = 0;
        if (selectedModule != null && !minimized) {
            contentHeight += PADDING;

            for (ModuleSettingTab tab : settingTabs) {
                contentHeight += SETTING_TAB_HEIGHT;
                if (tab.expanded) {
                    contentHeight += tab.settings.size() * SETTING_HEIGHT + PADDING / 2;
                }
            }
            contentHeight += PADDING;
        }
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (selectedModule == null) {
            return;
        }

        int renderHeight = minimized ? HEADER_HEIGHT : height;

        drawCleanBackground(context, renderHeight);
        renderHeader(context, mouseX, mouseY);

        if (!minimized) {
            context.enableScissor(getX(), getY() + HEADER_HEIGHT, getX() + width, getY() + height);
            renderContent(context, mouseX, mouseY);
            context.disableScissor();

            if (contentHeight > height - HEADER_HEIGHT) {
                renderScrollbar(context);
            }
        }
    }

    private void drawCleanBackground(@NotNull DrawContext context, int renderHeight) {
        context.fill(getX(), getY(), getX() + width, getY() + renderHeight, 0x90000000);

        context.fill(getX(), getY(), getX() + width, getY() + 1, 0x90333333);
        context.fill(getX(), getY() + renderHeight - 1, getX() + width, getY() + renderHeight, 0x90333333);
        context.fill(getX(), getY(), getX() + 1, getY() + renderHeight, 0x90333333);
        context.fill(getX() + width - 1, getY(), getX() + width, getY() + renderHeight, 0x90333333);
    }

    private void renderHeader(@NotNull DrawContext context, int mouseX, int mouseY) {
        context.fill(getX() + 1, getY() + HEADER_HEIGHT - 1, getX() + width - 1, getY() + HEADER_HEIGHT, 0x90222222);

        context.drawTextWithShadow(
                getTextRenderer(),
                selectedModule.getDisplayName(),
                getX() + PADDING,
                getY() + 12,
                0x90FFFFFF
        );

        boolean isEnabled = selectedModule.isRunning();
        String statusText = isEnabled ? "ENABLED" : "DISABLED";
        int statusColor = isEnabled ? 0x90FFFFFF : 0x90666666;
        context.drawTextWithShadow(
                getTextRenderer(),
                statusText,
                getX() + PADDING,
                getY() + 30,
                statusColor
        );

        renderHeaderButtons(context, mouseX, mouseY);
    }

    private void renderHeaderButtons(@NotNull DrawContext context, int mouseX, int mouseY) {
        int buttonY = getY() + 12;
        int buttonX = getX() + width - PADDING - BUTTON_SIZE;

        boolean closeHovered = mouseX >= buttonX && mouseX <= buttonX + BUTTON_SIZE &&
                mouseY >= buttonY && mouseY <= buttonY + BUTTON_SIZE;

        if (closeHovered) {
            context.fill(buttonX, buttonY, buttonX + BUTTON_SIZE, buttonY + BUTTON_SIZE, 0x901A1A1A);
        }

        context.fill(buttonX, buttonY, buttonX + BUTTON_SIZE, buttonY + 1, 0x90333333);
        context.fill(buttonX, buttonY + BUTTON_SIZE - 1, buttonX + BUTTON_SIZE, buttonY + BUTTON_SIZE, 0x90333333);
        context.fill(buttonX, buttonY, buttonX + 1, buttonY + BUTTON_SIZE, 0x90333333);
        context.fill(buttonX + BUTTON_SIZE - 1, buttonY, buttonX + BUTTON_SIZE, buttonY + BUTTON_SIZE, 0x90333333);

        context.drawCenteredTextWithShadow(
                getTextRenderer(),
                "×",
                buttonX + BUTTON_SIZE / 2,
                buttonY + (BUTTON_SIZE - 8) / 2,
                0x90FFFFFF
        );

        buttonX -= BUTTON_SIZE + 4;
        boolean minimizeHovered = mouseX >= buttonX && mouseX <= buttonX + BUTTON_SIZE &&
                mouseY >= buttonY && mouseY <= buttonY + BUTTON_SIZE;

        if (minimizeHovered) {
            context.fill(buttonX, buttonY, buttonX + BUTTON_SIZE, buttonY + BUTTON_SIZE, 0x901A1A1A);
        }

        context.fill(buttonX, buttonY, buttonX + BUTTON_SIZE, buttonY + 1, 0x90333333);
        context.fill(buttonX, buttonY + BUTTON_SIZE - 1, buttonX + BUTTON_SIZE, buttonY + BUTTON_SIZE, 0x90333333);
        context.fill(buttonX, buttonY, buttonX + 1, buttonY + BUTTON_SIZE, 0x90333333);
        context.fill(buttonX + BUTTON_SIZE - 1, buttonY, buttonX + BUTTON_SIZE, buttonY + BUTTON_SIZE, 0x90333333);

        context.drawCenteredTextWithShadow(
                getTextRenderer(),
                minimized ? "□" : "−",
                buttonX + BUTTON_SIZE / 2,
                buttonY + (BUTTON_SIZE - 8) / 2,
                0x90FFFFFF
        );
    }

    private void renderContent(DrawContext context, int mouseX, int mouseY) {
        if (settingTabs.isEmpty()) {
            renderNoSettings(context);
            return;
        }

        int currentY = getY() + HEADER_HEIGHT + PADDING - scrollY;

        for (ModuleSettingTab tab : settingTabs) {
            if (currentY > getY() + HEADER_HEIGHT - SETTING_TAB_HEIGHT && currentY < getY() + height) {
                renderSettingTabHeader(context, tab, currentY, mouseX, mouseY);
            }
            currentY += SETTING_TAB_HEIGHT;

            if (tab.expanded) {
                for (Setting setting : tab.settings) {
                    if (currentY > getY() + HEADER_HEIGHT - SETTING_HEIGHT && currentY < getY() + height) {
                        renderSetting(context, setting, currentY, mouseX, mouseY);
                    }
                    currentY += SETTING_HEIGHT;
                }
                currentY += PADDING / 2;
            }
        }
    }

    private void renderNoSettings(@NotNull DrawContext context) {
        String noSettingsText = "No settings available";
        context.drawCenteredTextWithShadow(
                getTextRenderer(),
                noSettingsText,
                getX() + width / 2,
                getY() + HEADER_HEIGHT + 30,
                0x90666666
        );
    }

    private void renderSettingTabHeader(DrawContext context, ModuleSettingTab tab, int y, int mouseX, int mouseY) {
        boolean hovered = mouseX >= getX() + PADDING && mouseX <= getX() + width - PADDING &&
                mouseY >= y && mouseY <= y + SETTING_TAB_HEIGHT;

        if (hovered) {
            context.fill(getX() + PADDING, y, getX() + width - PADDING, y + SETTING_TAB_HEIGHT, 0x90111111);
        }

        context.fill(getX() + PADDING * 2, y + SETTING_TAB_HEIGHT - 1,
                getX() + width - PADDING * 2, y + SETTING_TAB_HEIGHT, 0x90222222);

        String icon = tab.expanded ? "−" : "+";
        context.drawTextWithShadow(
                getTextRenderer(),
                icon,
                getX() + PADDING + 8,
                y + (SETTING_TAB_HEIGHT - 8) / 2,
                0x90FFFFFF
        );

        context.drawTextWithShadow(
                getTextRenderer(),
                tab.name,
                getX() + PADDING + 28,
                y + (SETTING_TAB_HEIGHT - 8) / 2,
                0x90FFFFFF
        );
    }

    private void renderSetting(DrawContext context, Setting setting, int y, int mouseX, int mouseY) {
        boolean hovered = mouseX >= getX() + PADDING * 2 && mouseX <= getX() + width - PADDING &&
                mouseY >= y && mouseY <= y + SETTING_HEIGHT;

        if (hovered) {
            context.fill(getX() + PADDING * 2, y, getX() + width - PADDING, y + SETTING_HEIGHT, 0x900A0A0A);
        }

        context.drawTextWithShadow(
                getTextRenderer(),
                setting.getPrefix(),
                getX() + PADDING * 2 + 12,
                y + (SETTING_HEIGHT - 8) / 2,
                0x90CCCCCC
        );
    }

    private void renderScrollbar(@NotNull DrawContext context) {
        int scrollbarX = getX() + width - 6;
        int scrollbarHeight = Math.max(15, ((height - HEADER_HEIGHT) * (height - HEADER_HEIGHT)) / contentHeight);
        int scrollbarY = getY() + HEADER_HEIGHT + (scrollY * (height - HEADER_HEIGHT - scrollbarHeight)) / (contentHeight - (height - HEADER_HEIGHT));

        context.fill(scrollbarX, getY() + HEADER_HEIGHT, scrollbarX + 4, getY() + height, 0x90111111);

        context.fill(scrollbarX + 1, scrollbarY, scrollbarX + 3, scrollbarY + scrollbarHeight, 0x90444444);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY) || selectedModule == null) {
            return false;
        }

        if (mouseY >= getY() && mouseY <= getY() + HEADER_HEIGHT) {
            return handleHeaderClick(mouseX, mouseY);
        }

        if (!minimized) {
            return handleContentClick(mouseX, mouseY);
        }

        return false;
    }

    private boolean handleHeaderClick(double mouseX, double mouseY) {
        int buttonY = getY() + 12;
        int buttonX = getX() + width - PADDING - BUTTON_SIZE;

        if (mouseX >= buttonX && mouseX <= buttonX + BUTTON_SIZE &&
                mouseY >= buttonY && mouseY <= buttonY + BUTTON_SIZE) {
            onPanelClosed.accept(this);
            return true;
        }

        buttonX -= BUTTON_SIZE + 4;
        if (mouseX >= buttonX && mouseX <= buttonX + BUTTON_SIZE &&
                mouseY >= buttonY && mouseY <= buttonY + BUTTON_SIZE) {
            minimized = !minimized;
            calculateContentHeight();
            return true;
        }

        return false;
    }

    private boolean handleContentClick(double mouseX, double mouseY) {
        int currentY = getY() + HEADER_HEIGHT + PADDING - scrollY;

        for (ModuleSettingTab tab : settingTabs) {
            if (mouseY >= currentY && mouseY <= currentY + SETTING_TAB_HEIGHT) {
                tab.expanded = !tab.expanded;
                calculateContentHeight();
                return true;
            }
            currentY += SETTING_TAB_HEIGHT;

            if (tab.expanded) {
                currentY += tab.settings.size() * SETTING_HEIGHT + PADDING / 2;
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!isMouseOver(mouseX, mouseY) || minimized || contentHeight <= height - HEADER_HEIGHT) {
            return false;
        }

        scrollY = Math.max(0, Math.min(contentHeight - (height - HEADER_HEIGHT),
                scrollY - (int)(verticalAmount * SCROLL_SPEED)));
        return true;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        if (selectedModule != null) {
            builder.put(net.minecraft.client.gui.screen.narration.NarrationPart.TITLE,
                    "Module Editor: " + selectedModule.getDisplayName());
        }
    }

    private net.minecraft.client.font.TextRenderer getTextRenderer() {
        return net.minecraft.client.MinecraftClient.getInstance().textRenderer;
    }

    private static class ModuleSettingTab {
        final String name;
        final List<Setting> settings = new ArrayList<>();
        boolean expanded = true;

        ModuleSettingTab(String name) {
            this.name = name;
        }
    }
}