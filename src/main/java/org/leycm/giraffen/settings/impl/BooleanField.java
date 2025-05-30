package org.leycm.giraffen.settings.impl;

import org.leycm.giraffen.settings.Field;

import java.util.Set;

public class BooleanField extends Field<Boolean> {
    private static final Set<String> TRUE_VALUES = Set.of("true", "yes", "1", "on");
    private static final Set<String> FALSE_VALUES = Set.of("false", "no", "0", "off");

    public BooleanField(String key, Boolean defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public Boolean parseFromStr(String s) {
        if (s == null) return null;

        String normalizedInput = s.trim().toLowerCase();

        if (TRUE_VALUES.contains(normalizedInput)) {
            return true;
        } else if (FALSE_VALUES.contains(normalizedInput)) {
            return false;
        } else {
            return getDefaultValue();
        }
    }

    @Override
    public boolean isValidInput(String s) {
        if (s == null) return false;
        String sc = s.trim().toLowerCase();
        return TRUE_VALUES.contains(sc) || FALSE_VALUES.contains(sc);
    }

    @Override
    public String[] toTabCompleter() {
        return new String[]{"true", "false"};
    }

}
