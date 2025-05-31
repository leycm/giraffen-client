package org.leycm.giraffen.settings.fields;

import org.leycm.giraffen.GiraffenClient;
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
        if(!isValidInput(s)) {
            GiraffenClient.LOGGER.warn("Invalid input, using default: " + getDefaultValue());
            return getDefaultValue();
        }

        return s == null || s.trim().isEmpty() ? getDefaultValue() : Integer.parseInt(s.trim());
    }

    @Override
    public boolean isValidInput(String s) {
        if (s == null || s.trim().isEmpty()) {return true;}

        try {
            int number = Integer.parseInt(s.trim());
            return number >= min && number <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String[] toTabCompleter(String arg) {
        if(!isValidInput(arg)) return new String[]{"<not-valid>"};
        return new String[]{"<"+min+"-"+max+">"};
    }

}
