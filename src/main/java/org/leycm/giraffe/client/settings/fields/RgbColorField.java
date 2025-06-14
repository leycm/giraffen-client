package org.leycm.giraffe.client.settings.fields;

import org.jetbrains.annotations.NotNull;
import org.leycm.giraffe.client.Client;
import org.leycm.giraffe.client.settings.Field;

public class RgbColorField extends Field<String> {

    public RgbColorField(String key, String defaultValue) {
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

        Client.LOGGER.warn("isValidInput(\"" + s + "\")");

        String hex = s.trim();

        return hex.matches("[0-9A-Fa-f]{6}");
    }

    @Override
    public String whyIsInvalid(String s) {
        if (s == null) return "Input cannot be null";

        Client.LOGGER.warn("whyIsInvalid(\"" + s + "\")");

        String hex = s.trim();

        if (hex.isEmpty()) {
            return "Empty color value provided";
        }

        if (hex.startsWith("#")) {
            return "Please enter color without # prefix (just 6 hex digits)";
        }

        if (hex.contains("\"")) {
            return "Please enter color without quotation marks";
        }

        if (!hex.matches("[0-9A-Fa-f]+")) {
            return "Invalid characters (only 0-9, A-F, a-f allowed)";
        }

        if (hex.length() != 6) {
            return String.format(
                    "Invalid length: %d digits (needs exactly 6)%s",
                    hex.length(),
                    hex.length() == 3 ? " - did you mean " +
                            hex.charAt(0) + hex.charAt(0) +
                            hex.charAt(1) + hex.charAt(1) +
                            hex.charAt(2) + hex.charAt(2) + "?" : ""
            );
        }

        return "Input is valid";
    }

    @Override
    public String[] toTabCompleter(String arg) {
        String baseValue = getValue().startsWith("#")
                ? getValue().substring(1)
                : getValue();

        if (arg == null || arg.startsWith("#")) {
            return new String[]{baseValue};
        }

        if (arg.length() < 7) {
            if (arg.matches("[0-9A-Fa-f]*")) {
                String completion = baseValue.substring(arg.length());
                return new String[]{arg + completion};
            }
        }

        return new String[]{baseValue};
    }

}