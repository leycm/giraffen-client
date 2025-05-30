package org.leycm.giraffen.settings.impl;

import org.jetbrains.annotations.NotNull;
import org.leycm.giraffen.settings.Field;

public class ColorField extends Field<Integer> {

    public ColorField(String key, Integer defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public Integer parseFromStr(@NotNull String s) {
        try {
            String hex = s.startsWith("#") ? s.substring(1) : s;
            return Integer.parseInt(hex, 16);
        } catch (NumberFormatException e) {
            return getDefaultValue();
        }
    }

    @Override
    public String parseToStr(@NotNull Integer color) {
        return String.format("%06X", color);
    }

    @Override
    public boolean isValidInput(String s) {
        if (s == null) return false;

        String hex = s.startsWith("#") ? s.substring(1) : s;
        return hex.matches("[0-9A-Fa-f]{6}");
    }

    @Override
    public String[] toTabCompleter() {
        return new String[] { "#" + parseToStr(getDefaultValue())};
    }

}