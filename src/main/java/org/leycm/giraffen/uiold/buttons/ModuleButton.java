package org.leycm.giraffen.uiold.buttons;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;

import static org.leycm.giraffen.uiold.utils.Ui.*;

public class ModuleButton extends ButtonWidget {
    private final BooleanSupplier isEnabledSupplier;
    private final TextRenderer textRenderer;
    private final float fontSize;

    public ModuleButton(int x, int y,
                        int width, int height,
                        float fontSize,
                        String message,
                        BooleanSupplier isEnabledSupplier,
                        PressAction onPress,
                        TextRenderer textRenderer
    ) {
        super(x, y, width, height, Text.literal(message), onPress, DEFAULT_NARRATION_SUPPLIER);
        this.isEnabledSupplier = isEnabledSupplier;
        this.textRenderer = textRenderer;
        this.fontSize = fontSize;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        boolean isEnabled = isEnabledSupplier.getAsBoolean();
        boolean isHovered = this.isHovered();

        int backgroundColor = isEnabled
                ? (isHovered ? Color.BUTTON_ENABLED_HOVER.value : Color.BUTTON_ENABLED.value)
                : (isHovered ? Color.BUTTON_DISABLED_HOVER.value : Color.BUTTON_DISABLED.value);

        int borderColor = isEnabled
                ? Color.BUTTON_BORDER_ENABLED.value
                : Color.BUTTON_BORDER_DISABLED.value;

        if (isHovered) {
            context.fill(
                    this.getX() + Layout.BUTTON_SHADOW_OFFSET.value,
                    this.getY() + Layout.BUTTON_SHADOW_OFFSET.value,
                    this.getX() + this.width + Layout.BUTTON_SHADOW_OFFSET.value,
                    this.getY() + this.height + Layout.BUTTON_SHADOW_OFFSET.value,
                    Color.SHADOW_COLOR.value
            );
        }

        context.fill(this.getX(), this.getY(),
                this.getX() + this.width, this.getY() + this.height,
                backgroundColor);

        context.drawBorder(this.getX(), this.getY(), this.width, this.height, borderColor);

        if (isEnabled) {
            context.fill(this.getX() + Layout.BUTTON_BORDER_INSET.value, this.getY() + Layout.BUTTON_BORDER_INSET.value,
                    this.getX() + this.width - Layout.BUTTON_BORDER_INSET.value, this.getY() + Layout.BUTTON_HIGHLIGHT_HEIGHT.value,
                    Color.BUTTON_HIGHLIGHT.value);
        }

        int indicatorX = this.getX() + this.width - Layout.INDICATOR_SIZE.value - Layout.INDICATOR_MARGIN.value;
        int indicatorY = this.getY() + Layout.INDICATOR_Y_OFFSET.value;
        int indicatorColor = isEnabled ? Color.INDICATOR_ENABLED.value : Color.INDICATOR_DISABLED.value;

        context.fill(indicatorX, indicatorY,
                indicatorX + Layout.INDICATOR_SIZE.value, indicatorY + Layout.INDICATOR_SIZE.value,
                indicatorColor);

        context.getMatrices().push();
        context.getMatrices().scale(fontSize, fontSize, Scale.MATRIX_SCALE_Z.floatValue);

        int textX = (int) ((this.getX() + (float) this.width / 2) / fontSize);
        int textY = (int) ((this.getY() + (float) (this.height - Layout.TEXT_Y_OFFSET.value) / 2) / fontSize);

        context.drawCenteredTextWithShadow(
                textRenderer,
                this.getMessage(),
                textX,
                textY,
                isEnabled ? Color.TEXT_PRIMARY.value : Color.TEXT_SECONDARY.value
        );

        context.getMatrices().pop();
    }
}
