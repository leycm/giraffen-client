package org.leycm.giraffen.settings.impl;

import org.leycm.giraffen.settings.Field;

public class IntegerField extends Field<Integer> {

    private final int min;
    private final int max;

    public IntegerField(String key, Integer defaultValue, int min, int max) {
        super(key, defaultValue);
        this.min = min;
        this.max = max;
    }

    @Override
    public Integer parseFromStr(String s) {
        if(!isValidInput(s)) return getDefaultValue();
        return Integer.parseInt(s);
    }

    @Override
    public boolean isValidInput(String s) {
        try {
            int number = Integer.parseInt(s);
            return number >= min && number <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String[] toTabCompleter() {
        return new String[]{"<"+min+"-"+max+">"};
    }

}
