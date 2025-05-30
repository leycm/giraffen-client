package org.leycm.giraffen.settings.impl;

import org.leycm.giraffen.settings.Field;

import java.util.Map;

public class MultiField extends Field<String> {

    private final Map<String, String> options;

    public MultiField(String key, String defaultValue, Map<String, String> options) {
        super(key, defaultValue);
        this.options = options;
    }

    @Override
    public String parseFromStr(String s) {
        return s;
    }

    @Override
    public boolean isValidInput(String s) {
        return false;
    }

    @Override
    public String[] toTabCompleter() {
        return options.keySet().toArray(String[]::new);
    }
}
