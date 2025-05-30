package org.leycm.giraffen.settings.impl;

import org.jetbrains.annotations.NotNull;
import org.leycm.giraffen.settings.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class StringField extends Field<String> {

    private final List<String> tabCompletionOptions;
    private final int maxLength;

    public StringField(String key, String defaultValue) {
        this(key, defaultValue, -1, Collections.emptyList());
    }

    public StringField(String key, String defaultValue, int maxLength) {
        this(key, defaultValue, maxLength, Collections.emptyList());
    }

    public StringField(String key, String defaultValue, Collection<String> tabCompletionOptions) {
        this(key, defaultValue, -1, tabCompletionOptions);
    }

    public StringField(String key, String defaultValue, int maxLength, Collection<String> tabCompletionOptions) {
        super(key, defaultValue);
        this.maxLength = maxLength;
        this.tabCompletionOptions = List.copyOf(tabCompletionOptions);
    }

    @Override
    public String parseFromStr(String s) {
        return s;
    }

    @Override
    public String parseToStr(@NotNull String value) {
        return value;
    }

    @Override
    public boolean isValidInput(String s) {
        if (s == null) return false;
        return maxLength <= 0 || s.length() <= maxLength;
    }

    @Override
    public String[] toTabCompleter() {
        return tabCompletionOptions.toArray(new String[0]);
    }
}