package org.leycm.giraffen.ui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.leycm.giraffen.module.Modules;
import org.leycm.giraffen.module.common.BaseModule;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ModuleScreen extends Screen {

    // Color Constants
    public static final int BACKGROUND_OVERLAY = 0x00000000;
    public static final int PANEL_BACKGROUND = 0xF0101014;
    public static final int PANEL_BORDER = 0xFF1A1A1F;
    public static final int ACCENT_GRADIENT_START = 0xFF6366F1;
    public static final int ACCENT_GRADIENT_END = 0xFF8B5CF6;
    public static final int TEXT_PRIMARY = 0xFFFFFFFF;
    public static final int TEXT_SECONDARY = 0xB3FFFFFF;
    public static final int BUTTON_ENABLED = 0xCC6366F1;
    public static final int BUTTON_ENABLED_HOVER = 0xFF7C3AED;
    public static final int BUTTON_DISABLED = 0x80333338;
    public static final int BUTTON_DISABLED_HOVER = 0x99404045;
    public static final int BUTTON_BORDER_ENABLED = 0xFF8B5CF6;
    public static final int BUTTON_BORDER_DISABLED = 0x66666666;
    public static final int SHADOW_COLOR = 0x30000000;
    public static final int BUTTON_HIGHLIGHT = 0x33FFFFFF;
    public static final int INDICATOR_ENABLED = 0xFF00FF88;
    public static final int INDICATOR_DISABLED = 0xFF666666;

    // Layout Constants
    public static final int PANEL_WIDTH = 320;
    public static final int PANEL_HEIGHT = 240;
    public static final int BUTTON_WIDTH = 90;
    public static final int BUTTON_HEIGHT = 18;
    public static final int BUTTON_SPACING = 6;
    public static final int MARGIN = 15;
    public static final int HEADER_HEIGHT = 10;
    public static final int CORNER_RADIUS = 6;
    public static final int FIELDS_PER_ROW = 3;
    public static final int SHADOW_OFFSET = 4;
    public static final int HEADER_Y_OFFSET = 15;
    public static final int MODULE_INFO_Y_OFFSET = 10;
    public static final int PANEL_BORDER_WIDTH = 1;
    public static final int PANEL_BORDER_INSET = 1;
    public static final int BUTTON_BORDER_INSET = 1;
    public static final int BUTTON_HIGHLIGHT_HEIGHT = 3;
    public static final int BUTTON_SHADOW_OFFSET = 2;
    public static final int INDICATOR_SIZE = 6;
    public static final int INDICATOR_MARGIN = 8;
    public static final int INDICATOR_Y_OFFSET = 6;
    public static final int TEXT_Y_OFFSET = 4;

    // Scale Constants
    public static final float TEXT_SCALE = 0.8f;
    public static final float GRADIENT_ALPHA_FACTOR = 0.3f;
    public static final float MATRIX_SCALE_Z = 1.0f;

    // Bit Manipulation Constants
    public static final int ALPHA_SHIFT = 24;
    public static final int RED_SHIFT = 16;
    public static final int GREEN_SHIFT = 8;
    public static final int COLOR_MASK = 0xFF;

    private final List<BaseModule> modules;
    private int panelX, panelY;

    public ModuleScreen() {
        super(Text.literal("Module Manager"));
        this.modules = Modules.getModules().values().stream().toList();
        ScreenHandler.register("module-menu-screen", this);
    }

    @Override
    protected void init() {
        super.init();

        this.panelX = (this.width - PANEL_WIDTH) / 2;
        this.panelY = (this.height - PANEL_HEIGHT) / 2;

        createModuleButtons();
    }

    private void createModuleButtons() {
        int startX = panelX + MARGIN;
        int startY = panelY + HEADER_HEIGHT + HEADER_Y_OFFSET;
        int currentX = startX;
        int currentY = startY;
        int buttonsInRow = 0;

        for (BaseModule module : modules) {
            ModernModuleButton button = new ModernModuleButton(
                    currentX, currentY,
                    BUTTON_WIDTH, BUTTON_HEIGHT,
                    Text.literal(module.getDisplayName()),
                    module
            );

            this.addDrawableChild(button);

            buttonsInRow++;
            if (buttonsInRow >= FIELDS_PER_ROW) {
                currentX = startX;
                currentY += BUTTON_HEIGHT + BUTTON_SPACING;
                buttonsInRow = 0;
            } else {
                currentX += BUTTON_WIDTH + BUTTON_SPACING;
            }
        }
    }

    @Override
    public void render(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, BACKGROUND_OVERLAY);

        drawShadow(context);

        context.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, PANEL_BACKGROUND);

        context.drawBorder(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, PANEL_BORDER);

        drawHeaderGradient(context);

        String moduleInfo = modules.size() + " modules loaded";
        context.drawTextWithShadow(
                this.textRenderer,
                Text.literal(moduleInfo),
                panelX + MARGIN,
                panelY + PANEL_HEIGHT - MODULE_INFO_Y_OFFSET,
                TEXT_SECONDARY
        );

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawShadow(@NotNull DrawContext context) {
        context.fill(
                panelX + SHADOW_OFFSET, panelY + SHADOW_OFFSET,
                panelX + PANEL_WIDTH + SHADOW_OFFSET, panelY + PANEL_HEIGHT + SHADOW_OFFSET,
                SHADOW_COLOR
        );
    }

    private void drawHeaderGradient(@NotNull DrawContext context) {
        context.fill(panelX + PANEL_BORDER_INSET, panelY + PANEL_BORDER_INSET,
                panelX + PANEL_WIDTH - PANEL_BORDER_INSET, panelY + HEADER_HEIGHT, ACCENT_GRADIENT_START);

        for (int i = 0; i < HEADER_HEIGHT; i++) {
            float alpha = 1.0f - (float) i / HEADER_HEIGHT * GRADIENT_ALPHA_FACTOR;
            int gradientColor = interpolateColor(ACCENT_GRADIENT_START, ACCENT_GRADIENT_END, (float) i / HEADER_HEIGHT);
            context.fill(panelX + PANEL_BORDER_INSET, panelY + PANEL_BORDER_INSET + i,
                    panelX + PANEL_WIDTH - PANEL_BORDER_INSET, panelY + PANEL_BORDER_INSET + i + 1, gradientColor);
        }
    }

    private int interpolateColor(int color1, int color2, float factor) {
        int a1 = (color1 >> ALPHA_SHIFT) & COLOR_MASK;
        int r1 = (color1 >> RED_SHIFT) & COLOR_MASK;
        int g1 = (color1 >> GREEN_SHIFT) & COLOR_MASK;
        int b1 = color1 & COLOR_MASK;

        int a2 = (color2 >> ALPHA_SHIFT) & COLOR_MASK;
        int r2 = (color2 >> RED_SHIFT) & COLOR_MASK;
        int g2 = (color2 >> GREEN_SHIFT) & COLOR_MASK;
        int b2 = color2 & COLOR_MASK;

        int a = (int) (a1 + factor * (a2 - a1));
        int r = (int) (r1 + factor * (r2 - r1));
        int g = (int) (g1 + factor * (g2 - g1));
        int b = (int) (b1 + factor * (b2 - b1));

        return (a << ALPHA_SHIFT) | (r << RED_SHIFT) | (g << GREEN_SHIFT) | b;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private class ModernModuleButton extends ButtonWidget {
        private final BaseModule module;

        public ModernModuleButton(int x, int y, int width, int height, Text message, BaseModule module) {
            super(x, y, width, height, message, button -> module.toggle(), DEFAULT_NARRATION_SUPPLIER);
            this.module = module;
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            boolean isEnabled = module.isRunning();
            boolean isHovered = this.isHovered();

            int backgroundColor;
            int borderColor;

            if (isEnabled) {
                backgroundColor = isHovered ? BUTTON_ENABLED_HOVER : BUTTON_ENABLED;
                borderColor = BUTTON_BORDER_ENABLED;
            } else {
                backgroundColor = isHovered ? BUTTON_DISABLED_HOVER : BUTTON_DISABLED;
                borderColor = BUTTON_BORDER_DISABLED;
            }

            if (isHovered) {
                context.fill(this.getX() + BUTTON_SHADOW_OFFSET, this.getY() + BUTTON_SHADOW_OFFSET,
                        this.getX() + this.width + BUTTON_SHADOW_OFFSET, this.getY() + this.height + BUTTON_SHADOW_OFFSET,
                        SHADOW_COLOR);
            }

            context.fill(this.getX(), this.getY(),
                    this.getX() + this.width, this.getY() + this.height,
                    backgroundColor);

            context.drawBorder(this.getX(), this.getY(), this.width, this.height, borderColor);

            if (isEnabled) {
                context.fill(this.getX() + BUTTON_BORDER_INSET, this.getY() + BUTTON_BORDER_INSET,
                        this.getX() + this.width - BUTTON_BORDER_INSET, this.getY() + BUTTON_HIGHLIGHT_HEIGHT,
                        BUTTON_HIGHLIGHT);
            }

            int indicatorX = this.getX() + this.width - INDICATOR_SIZE - INDICATOR_MARGIN;
            int indicatorY = this.getY() + INDICATOR_Y_OFFSET;
            int indicatorColor = isEnabled ? INDICATOR_ENABLED : INDICATOR_DISABLED;

            context.fill(indicatorX, indicatorY,
                    indicatorX + INDICATOR_SIZE, indicatorY + INDICATOR_SIZE,
                    indicatorColor);

            context.getMatrices().push();
            context.getMatrices().scale(TEXT_SCALE, TEXT_SCALE, MATRIX_SCALE_Z);

            int textX = (int) ((this.getX() + (float) this.width / 2) / TEXT_SCALE);
            int textY = (int) ((this.getY() + (float) (this.height - TEXT_Y_OFFSET) / 2) / TEXT_SCALE);

            context.drawCenteredTextWithShadow(
                    ModuleScreen.this.textRenderer,
                    this.getMessage(),
                    textX,
                    textY,
                    isEnabled ? TEXT_PRIMARY : TEXT_SECONDARY
            );

            context.getMatrices().pop();
        }
    }

    @Override
    protected void applyBlur() {}
}