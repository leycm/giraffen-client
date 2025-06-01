package org.leycm.giraffen.uiold.utils;

public class Ui {

    private Ui() {

    }

    public enum Color {
        BACKGROUND_OVERLAY(0x00000000),
        PANEL_BACKGROUND(0xF0101014),
        PANEL_BORDER(0xFF1A1A1F),
        ACCENT_GRADIENT_START(0xFF6366F1),
        ACCENT_GRADIENT_END(0xFF8B5CF6),
        TEXT_PRIMARY(0xFFFFFFFF),
        TEXT_SECONDARY(0xB3FFFFFF),
        BUTTON_ENABLED(0xCC6366F1),
        BUTTON_ENABLED_HOVER(0xFF7C3AED),
        BUTTON_DISABLED(0x80333338),
        BUTTON_DISABLED_HOVER(0x99404045),
        BUTTON_BORDER_ENABLED(0xFF8B5CF6),
        BUTTON_BORDER_DISABLED(0x66666666),
        SHADOW_COLOR(0x30000000),
        BUTTON_HIGHLIGHT(0x33FFFFFF),
        INDICATOR_ENABLED(0xFF00FF88),
        INDICATOR_DISABLED(0xFF666666);

        public final int value;

        Color(int value) {
            this.value = value;
        }
    }

    public enum Layout {
        PANEL_WIDTH(296),
        PANEL_HEIGHT(240),
        BUTTON_WIDTH(90),
        BUTTON_HEIGHT(18),
        BUTTON_SPACING(6),
        MARGIN(7),
        HEADER_HEIGHT(12),
        CORNER_RADIUS(6),
        FIELDS_PER_ROW(3),
        SHADOW_OFFSET(1),
        HEADER_Y_OFFSET(3),
        MODULE_INFO_Y_OFFSET(7),
        PANEL_BORDER_WIDTH(1),
        PANEL_BORDER_INSET(1),
        BUTTON_BORDER_INSET(1),
        BUTTON_HIGHLIGHT_HEIGHT(3),
        BUTTON_SHADOW_OFFSET(2),
        INDICATOR_SIZE(6),
        INDICATOR_MARGIN(8),
        INDICATOR_Y_OFFSET(6),
        TEXT_Y_OFFSET(6);

        public final int value;

        Layout(int value) {
            this.value = value;
        }
    }

    public enum Scale {
        TEXT_SCALE(0.8f),
        GRADIENT_ALPHA_FACTOR(0.3f),
        MATRIX_SCALE_Z(1.0f),
        ALPHA_SHIFT(24),
        RED_SHIFT(16),
        GREEN_SHIFT(8),
        COLOR_MASK(0xFF);

        public final float floatValue;
        public final int intValue;

        Scale(float floatValue) {
            this.floatValue = floatValue;
            this.intValue = 0;
        }

        Scale(int intValue) {
            this.intValue = intValue;
            this.floatValue = 0f;
        }
    }



}
