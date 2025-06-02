package org.leycm.giraffen.ui;

import net.minecraft.client.gui.Element;

@FunctionalInterface
public interface UiRenderCallback<T> {
    T render(Element element);
}
