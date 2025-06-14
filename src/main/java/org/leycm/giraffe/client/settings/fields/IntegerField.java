package org.leycm.giraffe.client.settings.fields;

import org.leycm.giraffe.client.Client;
import org.leycm.giraffe.client.settings.Field;

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
            Client.LOGGER.warn("Invalid input, using default: " + getDefaultValue());
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
    public String whyIsInvalid(String s) {
        if (s == null || s.trim().isEmpty()) {
            return "Input is valid (empty or null is allowed)";
        }

        try {
            int number = Integer.parseInt(s.trim());
            if (number < min) {
                return String.format(
                        "Number %d is too small (minimum allowed: %d)",
                        number, min
                );
            }
            if (number > max) {
                return String.format(
                        "Number %d is too large (maximum allowed: %d)",
                        number, max
                );
            }
        } catch (NumberFormatException e) {
            return String.format(
                    "'%s' is not a valid integer number",
                    s.trim()
            );
        }

        return "Input is valid";
    }

    @Override
    public String[] toTabCompleter(String arg) {
        return new String[]{"<"+min+"-"+max+">"};
    }

}
