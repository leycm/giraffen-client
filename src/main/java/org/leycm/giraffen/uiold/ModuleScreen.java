package org.leycm.giraffen.uiold;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.leycm.giraffen.module.Modules;
import org.leycm.giraffen.module.common.BaseModule;
import org.leycm.giraffen.uiold.buttons.ModuleButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.leycm.giraffen.uiold.utils.Ui.*;

@Environment(EnvType.CLIENT)
public class ModuleScreen extends Screen {

    private final List<BaseModule> modules;
    private List<BaseModule> filteredModules;
    private List<String> groups;
    private Set<ModuleButton> buttons;
    private String selectedGroup = "🔍"; // Search tab as default
    private int panelX, panelY;
    private int scrollOffset = 0;
    private int maxScrollOffset = 0;
    private int contentHeight = 0;
    private int visibleHeight = 0;
    private TextFieldWidget searchField;
    private List<ModuleButton> groupTabs;

    public ModuleScreen() {
        super(Text.literal("Module Manager"));
        this.modules = Modules.getModules().values().stream().toList();
        this.filteredModules = new ArrayList<>(modules);
        this.groups = new ArrayList<>();
        this.groupTabs = new ArrayList<>();
        this.buttons = new HashSet<>();

        // Collect all unique groups
        this.groups.add("🔍"); // Search tab first
        this.groups.addAll(
                modules.stream()
                        .map(BaseModule::getCategory)
                        .distinct()
                        .sorted()
                        .toList()
        );

        //ScreenHandler.register("module-menu-screens", this);
    }

    @Override
    protected void init() {
        super.init();

        this.panelX = (this.width - Layout.PANEL_WIDTH.value) / 2;
        this.panelY = (this.height - Layout.PANEL_HEIGHT.value) / 2;

        createGroupTabs();
        createSearchField();
        updateFilteredModules();
        calculateContentDimensions();
        createModuleButtons();
    }

    private void createGroupTabs() {
        this.groupTabs.clear();
        int tabSize = 20;
        int tabSpacing = 5;
        int startY = panelY + Layout.MARGIN.value;

        for (int i = 0; i < groups.size(); i++) {
            String group = groups.get(i);
            int tabX = panelX - tabSize - 10;
            int tabY = startY + i * (tabSize + tabSpacing);

            String displayText = group.equals("🔍") ? "🔍" : group.substring(0, 1).toUpperCase();

            ModuleButton tabButton = new ModuleButton(
                    tabX, tabY,
                    tabSize, tabSize,
                    1.5f,
                    displayText,
                    () -> group.equals(selectedGroup),
                    (button) -> selectGroup(group),
                    textRenderer
            );

            this.groupTabs.add(tabButton);
            this.addDrawableChild(tabButton);
        }
    }

    private void createSearchField() {
        if (selectedGroup.equals("🔍")) {
            int fieldWidth = Layout.PANEL_WIDTH.value - Layout.MARGIN.value * 2;
            int fieldHeight = 20;
            int fieldX = panelX + Layout.MARGIN.value;
            int fieldY = panelY + Layout.HEADER_HEIGHT.value + Layout.HEADER_Y_OFFSET.value + 5;

            this.searchField = new TextFieldWidget(
                    this.textRenderer,
                    fieldX, fieldY, fieldWidth, fieldHeight,
                    Text.literal("Search modules...")
            );

            this.searchField.setPlaceholder(Text.literal("Search modules..."));
            this.searchField.setChangedListener(this::onSearchChanged);
            this.addDrawableChild(this.searchField);
        } else {
            this.searchField = null;
        }
    }

    private void selectGroup(@NotNull String group) {
        if (!group.equals(selectedGroup)) {
            selectedGroup = group;
            scrollOffset = 0;

            // Remove old search field if it exists
            if (searchField != null) {
                this.remove(searchField);
                searchField = null;
            }

            // Create new search field if needed
            createSearchField();

            updateFilteredModules();
            calculateContentDimensions();
            createModuleButtons();
        }
    }

    private void onSearchChanged(String searchText) {
        updateFilteredModules();
        calculateContentDimensions();
        createModuleButtons();
        scrollOffset = 0;
    }

    private void updateFilteredModules() {
        if (selectedGroup.equals("🔍")) {
            // Search mode
            String searchText = searchField != null ? searchField.getText().toLowerCase() : "";
            if (searchText.isEmpty()) {
                filteredModules = new ArrayList<>(modules);
            } else {
                filteredModules = modules.stream()
                        .filter(module -> module.getDisplayName().toLowerCase().contains(searchText) ||
                                module.getCategory().toLowerCase().contains(searchText))
                        .collect(Collectors.toList());
            }
        } else {
            // Group mode
            filteredModules = modules.stream()
                    .filter(module -> module.getCategory().equals(selectedGroup))
                    .collect(Collectors.toList());
        }
    }

    private void calculateContentDimensions() {
        int buttonsPerRow = Layout.FIELDS_PER_ROW.value;
        int totalRows = (int) Math.ceil((double) filteredModules.size() / buttonsPerRow);

        this.contentHeight = totalRows * (Layout.BUTTON_HEIGHT.value + Layout.BUTTON_SPACING.value) - Layout.BUTTON_SPACING.value;

        int searchFieldHeight = selectedGroup.equals("🔍") ? 30 : 0; // Extra space for search field
        this.visibleHeight = Layout.PANEL_HEIGHT.value - Layout.HEADER_HEIGHT.value - Layout.HEADER_Y_OFFSET.value - Layout.MARGIN.value * 2 - Layout.MODULE_INFO_Y_OFFSET.value - searchFieldHeight;

        this.maxScrollOffset = Math.max(0, contentHeight - visibleHeight);
        this.scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset));
    }

    private void createModuleButtons() {
        this.children().removeIf(widget -> widget instanceof ModuleButton);
        this.buttons.forEach(this::remove);

        int startX = panelX + Layout.MARGIN.value;
        int searchFieldOffset = selectedGroup.equals("🔍") ? 30 : 0;
        int startY = panelY + Layout.HEADER_HEIGHT.value + Layout.HEADER_Y_OFFSET.value + searchFieldOffset - scrollOffset;
        int currentX = startX;
        int currentY = startY;
        int buttonsInRow = 0;

        for (BaseModule module : filteredModules) {
            int clipStartY = panelY + Layout.HEADER_HEIGHT.value + Layout.HEADER_Y_OFFSET.value + searchFieldOffset;
            int clipEndY = panelY + Layout.PANEL_HEIGHT.value - Layout.MARGIN.value - Layout.MODULE_INFO_Y_OFFSET.value;

            if (currentY + Layout.BUTTON_HEIGHT.value >= clipStartY &&
                    currentY <= clipEndY) {

                ModuleButton button = new ModuleButton(
                        currentX, currentY,
                        Layout.BUTTON_WIDTH.value, Layout.BUTTON_HEIGHT.value,
                        Scale.TEXT_SCALE.floatValue,
                        module.getDisplayName(),
                        module::isRunning,
                        btn -> module.toggle(),
                        textRenderer
                );

                this.buttons.add(button);
                this.addDrawableChild(button);
            }

            buttonsInRow++;
            if (buttonsInRow >= Layout.FIELDS_PER_ROW.value) {
                currentX = startX;
                currentY += Layout.BUTTON_HEIGHT.value + Layout.BUTTON_SPACING.value;
                buttonsInRow = 0;
            } else {
                currentX += Layout.BUTTON_WIDTH.value + Layout.BUTTON_SPACING.value;
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseX >= panelX && mouseX <= panelX + Layout.PANEL_WIDTH.value &&
                mouseY >= panelY && mouseY <= panelY + Layout.PANEL_HEIGHT.value) {

            int scrollSpeed = 20;
            int newScrollOffset = scrollOffset - (int) (verticalAmount * scrollSpeed);

            newScrollOffset = Math.max(0, Math.min(newScrollOffset, maxScrollOffset));

            if (newScrollOffset != scrollOffset) {
                scrollOffset = newScrollOffset;
                createModuleButtons();
                return true;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void render(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, Color.BACKGROUND_OVERLAY.value);

        drawShadow(context);

        context.fill(
                panelX, panelY,
                panelX + Layout.PANEL_WIDTH.value, panelY + Layout.PANEL_HEIGHT.value,
                Color.PANEL_BACKGROUND.value
        );

        context.drawBorder(
                panelX, panelY,
                Layout.PANEL_WIDTH.value, Layout.PANEL_HEIGHT.value,
                Color.PANEL_BORDER.value
        );

        drawHeaderGradient(context);

        // Draw group tabs
        for (ModuleButton tab : groupTabs) {
            tab.render(context, mouseX, mouseY, delta);
        }

        int searchFieldOffset = selectedGroup.equals("🔍") ? 30 : 0;
        int clipStartY = panelY + Layout.HEADER_HEIGHT.value + Layout.HEADER_Y_OFFSET.value + searchFieldOffset;
        int clipEndY = panelY + Layout.PANEL_HEIGHT.value - Layout.MARGIN.value - Layout.MODULE_INFO_Y_OFFSET.value;

        context.enableScissor(
                panelX + Layout.MARGIN.value,
                clipStartY,
                panelX + Layout.PANEL_WIDTH.value - Layout.MARGIN.value,
                clipEndY
        );

        super.render(context, mouseX, mouseY, delta);

        context.disableScissor();

        String moduleInfo = filteredModules.size() + " modules" +
                (selectedGroup.equals("🔍") ? " found" : " in " + selectedGroup);
        context.drawTextWithShadow(
                this.textRenderer,
                Text.literal(moduleInfo),
                panelX + Layout.MARGIN.value,
                panelY + Layout.PANEL_HEIGHT.value - Layout.MARGIN.value - Layout.MODULE_INFO_Y_OFFSET.value,
                Color.TEXT_SECONDARY.value
        );

        if (maxScrollOffset > 0) {
            drawScrollIndicator(context);
        }
    }

    private void drawScrollIndicator(@NotNull DrawContext context) {
        if (maxScrollOffset <= 0) return;

        int indicatorX = panelX + Layout.PANEL_WIDTH.value - Layout.MARGIN.value - 2;
        int searchFieldOffset = selectedGroup.equals("🔍") ? 30 : 0;
        int indicatorStartY = panelY + Layout.HEADER_HEIGHT.value + Layout.HEADER_Y_OFFSET.value + searchFieldOffset;
        int indicatorHeight = visibleHeight;

        context.fill(
                indicatorX, indicatorStartY,
                indicatorX + 2, indicatorStartY + indicatorHeight,
                0x20FFFFFF
        );

        float scrollProgress = (float) scrollOffset / maxScrollOffset;
        int thumbHeight = Math.max(10, (int) ((float) indicatorHeight * visibleHeight / contentHeight));
        int thumbY = indicatorStartY + (int) (scrollProgress * (indicatorHeight - thumbHeight));

        context.fill(
                indicatorX, thumbY,
                indicatorX + 2, thumbY + thumbHeight,
                0x80FFFFFF
        );
    }

    private void drawShadow(@NotNull DrawContext context) {
        context.fill(
                panelX + Layout.SHADOW_OFFSET.value, panelY + Layout.SHADOW_OFFSET.value,
                panelX + Layout.PANEL_WIDTH.value + Layout.SHADOW_OFFSET.value,
                panelY + Layout.PANEL_HEIGHT.value + Layout.SHADOW_OFFSET.value,
                Color.SHADOW_COLOR.value
        );
    }

    private void drawHeaderGradient(@NotNull DrawContext context) {
        int inset = Layout.PANEL_BORDER_INSET.value;
        int headerHeight = Layout.HEADER_HEIGHT.value;

        for (int i = 0; i < headerHeight; i++) {
            float progress = (float) i / headerHeight;
            int gradientColor = interpolateColor(
                    Color.ACCENT_GRADIENT_START.value,
                    Color.ACCENT_GRADIENT_END.value,
                    progress
            );
            context.fill(
                    panelX + inset,
                    panelY + inset + i,
                    panelX + Layout.PANEL_WIDTH.value - inset,
                    panelY + inset + i + 1,
                    gradientColor
            );
        }
    }

    private int interpolateColor(int color1, int color2, float factor) {
        int a1 = (color1 >> Scale.ALPHA_SHIFT.intValue) & Scale.COLOR_MASK.intValue;
        int r1 = (color1 >> Scale.RED_SHIFT.intValue) & Scale.COLOR_MASK.intValue;
        int g1 = (color1 >> Scale.GREEN_SHIFT.intValue) & Scale.COLOR_MASK.intValue;
        int b1 = color1 & Scale.COLOR_MASK.intValue;

        int a2 = (color2 >> Scale.ALPHA_SHIFT.intValue) & Scale.COLOR_MASK.intValue;
        int r2 = (color2 >> Scale.RED_SHIFT.intValue) & Scale.COLOR_MASK.intValue;
        int g2 = (color2 >> Scale.GREEN_SHIFT.intValue) & Scale.COLOR_MASK.intValue;
        int b2 = color2 & Scale.COLOR_MASK.intValue;

        int a = (int) (a1 + factor * (a2 - a1));
        int r = (int) (r1 + factor * (r2 - r1));
        int g = (int) (g1 + factor * (g2 - g1));
        int b = (int) (b1 + factor * (b2 - b1));

        return (a << Scale.ALPHA_SHIFT.intValue) |
                (r << Scale.RED_SHIFT.intValue) |
                (g << Scale.GREEN_SHIFT.intValue) |
                b;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void applyBlur() {}

    // Inner class for group tab buttons
    private static class GroupTabButton extends net.minecraft.client.gui.widget.ButtonWidget {
        private final String group;
        private final String displayText;
        private final java.util.function.BooleanSupplier isSelected;
        private final net.minecraft.client.font.TextRenderer textRenderer;

        public GroupTabButton(int x, int y, int width, int height, String displayText, String group,
                              java.util.function.BooleanSupplier isSelected, Runnable onPress,
                              net.minecraft.client.font.TextRenderer textRenderer) {
            super(x, y, width, height, Text.literal(displayText), button -> onPress.run(), DEFAULT_NARRATION_SUPPLIER);
            this.group = group;
            this.displayText = displayText;
            this.isSelected = isSelected;
            this.textRenderer = textRenderer;
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            boolean selected = isSelected.getAsBoolean();
            boolean hovered = this.isHovered();

            int backgroundColor = selected ? 0xFF4A90E2 : (hovered ? 0xFF3A3A3A : 0xFF2A2A2A);
            int borderColor = selected ? 0xFF5BA0F2 : 0xFF444444;

            context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), backgroundColor);
            context.drawBorder(getX(), getY(), getWidth(), getHeight(), borderColor);

            int textColor = selected ? 0xFFFFFFFF : 0xFFCCCCCC;
            int textX = getX() + (getWidth() - textRenderer.getWidth(displayText)) / 2;
            int textY = getY() + (getHeight() - textRenderer.fontHeight) / 2;

            context.drawText(textRenderer, displayText, textX, textY, textColor, false);
        }
    }
}