package org.leycm.giraffen.ui.widgets;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.EmptyWidget;

import java.util.function.IntUnaryOperator;

public class ContainerWidget extends EmptyWidget {
    private Anchor anchor;
    private final Screen screen;

    public ContainerWidget(int x, int y,
                           int width, int height,
                           Screen screen, Anchor anchor) {
        super(x, y, width, height);
        this.screen = screen;
        this.anchor = anchor;
    }

    public void setAnchor(Anchor anchor) {
        this.anchor = anchor;
    }

    public Anchor getAnchor(){
        return getAnchor(true);
    }

    public Anchor getAnchor(boolean calculateAuto){
        if (!calculateAuto) return anchor;
        return anchor != Anchor.AUTO ? anchor : Anchor.calculateAuto(super.getX(), super.getY(), screen.width, screen.height);
    }

    @Override
    public int getX() {
        return getAnchor(true).getXAnchorFromSize(screen.width) + super.getX();
    }

    @Override
    public int getY() {
        return getAnchor(true).getYAnchorFromSize(screen.height) + super.getY();
    }

    public int getXformAnchor() {
        return super.getX();
    }

    public int getYformAnchor() {
        return super.getY();
    }

    public enum Anchor {
        LEFT_TOP((x) -> 0, (y) -> 0),
        LEFT_CENTER((x) -> 0, (y) -> y / 2),
        LEFT_BOTTOM((x) -> 0, (y) -> y),

        CENTER_TOP((x) -> x / 2, (y) -> 0),
        CENTER_CENTER((x) -> x / 2, (y) -> y / 2),
        CENTER_BOTTOM((x) -> x / 2, (y) -> y),

        RIGHT_TOP((x) -> x, (y) -> 0),
        RIGHT_CENTER((x) -> x, (y) -> y / 2),
        RIGHT_BOTTOM((x) -> x, (y) -> y),

        AUTO();

        private final IntUnaryOperator xOperator;
        private final IntUnaryOperator yOperator;

        public int getXAnchorFromSize(int x) {
            return xOperator.applyAsInt(x);
        }

        public int getYAnchorFromSize(int y) {
            return yOperator.applyAsInt(y);
        }

        public static Anchor calculateAuto(int x, int y, int screenSizeX, int screenSizeY) {
            Anchor horizontal;
            if (x < screenSizeX / 3) {
                horizontal = Anchor.LEFT_TOP;
            } else if (x < 2 * screenSizeX / 3) {
                horizontal = Anchor.CENTER_TOP;
            } else {
                horizontal = Anchor.RIGHT_TOP;
            }

            Anchor vertical;
            if (y < screenSizeY / 3) {
                vertical = Anchor.LEFT_TOP;
            } else if (y < 2 * screenSizeY / 3) {
                vertical = Anchor.LEFT_CENTER;
            } else {
                vertical = Anchor.LEFT_BOTTOM;
            }

            if (horizontal == Anchor.LEFT_TOP) {
                if (vertical == Anchor.LEFT_TOP) return Anchor.LEFT_TOP;
                if (vertical == Anchor.LEFT_CENTER) return Anchor.LEFT_CENTER;
                return Anchor.LEFT_BOTTOM;
            } else if (horizontal == Anchor.CENTER_TOP) {
                if (vertical == Anchor.LEFT_TOP) return Anchor.CENTER_TOP;
                if (vertical == Anchor.LEFT_CENTER) return Anchor.CENTER_CENTER;
                return Anchor.CENTER_BOTTOM;
            } else {
                if (vertical == Anchor.LEFT_TOP) return Anchor.RIGHT_TOP;
                if (vertical == Anchor.LEFT_CENTER) return Anchor.RIGHT_CENTER;
                return Anchor.RIGHT_BOTTOM;
            }
        }

        Anchor(IntUnaryOperator x, IntUnaryOperator y) {
            this.xOperator = x;
            this.yOperator = y;
        }

        Anchor() {
            this.xOperator = (x) -> x;
            this.yOperator = (y) -> y;
        }
    }
}