package org.leycm.giraffe.client.settings.fields;

import org.jetbrains.annotations.NotNull;
import org.leycm.giraffe.client.settings.Field;

public class StringField extends Field<String> {

    private final int minLength;
    private final int maxLength;
    private final String regex;
    private String tabCompleter;

    public StringField(String key, String defaultValue, String tabCompleter) {
        super(key, defaultValue);
        this.tabCompleter = tabCompleter;
        this.minLength = 0;
        this.maxLength = 500;
        this.regex = null;
    }

    public StringField(String key, String defaultValue, int minLength, int maxLength, String regex) {
        super(key, defaultValue);
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.regex = regex;
    }

    @Override
    public String parseFromStr(String s) {
        if (!isValidInput(s)) return getDefaultValue();
        return s;
    }

    @Override
    public String parseToStr(@NotNull String value) {
        return value;
    }

    @Override
    public boolean isValidInput(String s) {
        if (s == null) return false;
        int len = s.length();
        if (len < minLength || len > maxLength) return false;
        return regex == null || s.matches(regex);
    }

    @Override
    public String whyIsInvalid(String s) {
        if (s == null) {
            return "Input cannot be null";
        }

        int len = s.length();
        if (len < minLength) {
            return String.format(
                    "Input is too short (length: %d, minimum required: %d)",
                    len, minLength
            );
        }

        if (len > maxLength) {
            return String.format(
                    "Input is too long (length: %d, maximum allowed: %d)",
                    len, maxLength
            );
        }

        if (regex != null && !s.matches(regex)) {
            return String.format(
                    "Input does not match the required pattern: %s",
                    regex
            );
        }

        return "Input is valid";
    }

    @Override
    public String[] toTabCompleter(String arg) {
        if(tabCompleter != null) return new String[] {tabCompleter};
        String regexPart = (regex != null) ? ", pattern=" + regex : "";
        return new String[] {
                "<string length " + minLength + "-" + maxLength + regexPart + ">"
        };
    }
}
