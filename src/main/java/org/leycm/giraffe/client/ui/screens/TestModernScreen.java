package org.leycm.giraffe.client.ui.screens;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.leycm.giraffe.client.identifier.CachedIdentifier;
import org.leycm.giraffe.client.ui.widgets.MediaWidget;

public class TestModernScreen extends ModernScreen {
    private ButtonWidget testButton1;
    private ButtonWidget testButton2;
    private ButtonWidget clearButton;
    private ButtonWidget closeButton;
    private MediaWidget mediaWidget;

    public TestModernScreen() {
        super("test-screen", CachedIdentifier.of("C:\\Users\\Admin\\Pictures\\a_853765577f0168521bb2641bf8adf065.gif"));
    }

    @Override
    protected void onRender() {

    }

    @Override
    protected void onInit() {

        this.testButton1 = ButtonWidget.builder(Text.literal("Button 1"), button -> System.out.println("Button 1 clicked!"))
                .dimensions(this.width / 2 - 100, this.height / 2 - 30, 200, 20)
                .build();

        this.testButton2 = ButtonWidget.builder(Text.literal("Button 2"), button -> System.out.println("Button 2 clicked! " + width + "/" + height))
                .dimensions(this.width / 2 - 100, this.height / 2, 200, 20)
                .build();

        this.clearButton = ButtonWidget.builder(Text.literal("Clear"), button -> this.clear(child -> child.equals(closeButton)))
                .dimensions(this.width / 2 - 100, this.height / 2 + 30, 200, 20)
                .build();

        this.closeButton = ButtonWidget.builder(Text.literal("Close"), button -> this.close())
                .dimensions(this.width / 2 - 100, this.height / 2 + 60, 200, 20)
                .build();


        this.addDrawableChild(testButton1);
        this.addDrawableChild(testButton2);
        this.addDrawableChild(clearButton);
        this.addDrawableChild(closeButton);
    }
}
