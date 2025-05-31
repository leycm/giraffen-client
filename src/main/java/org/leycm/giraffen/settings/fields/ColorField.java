package org.leycm.giraffen.settings.fields;

import org.jetbrains.annotations.NotNull;
import org.leycm.giraffen.settings.Field;

public class ColorField extends Field<String> {

    public ColorField(String key, String defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public String parseFromStr(@NotNull String s) {
        if (!isValidInput(s)) {
            return getDefaultValue();
        }
        String color = s.replace("\"", "").trim();
        return color.startsWith("#") ? color : "#" + color;
    }

    @Override
    public String parseToStr(@NotNull String color) {
        return "\"" + color + "\"";
    }

    @Override
    public boolean isValidInput(String s) {
        if (s == null) return false;

        String color = s.replace("\"", "").trim();
        String hex = color.startsWith("#") ? color.substring(1) : color;
        return hex.matches("[0-9A-Fa-f]{6}");
    }

    @Override
    public String[] toTabCompleter(String arg) {
        String defaultColor = getValue().startsWith("#") ? getValue() : "#" + getValue();
        String defaultTab = "\"" + defaultColor + "\"";

        if (arg == null || !arg.startsWith("\"")) {
            return new String[]{defaultTab};
        }

        if (arg.startsWith("\"#") && arg.length() < 9) {
            String current = arg.substring(2);
            if (current.matches("[0-9A-Fa-f]*")) {
                String completion = defaultColor.substring(current.length() + 1);
                return new String[]{arg + completion + "\""};
            }
        }

        return new String[]{defaultTab};
    }
}