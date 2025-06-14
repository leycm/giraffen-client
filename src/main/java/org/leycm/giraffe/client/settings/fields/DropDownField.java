package org.leycm.giraffe.client.settings.fields;

import org.leycm.giraffe.client.settings.Field;

import java.util.Map;

public class DropDownField extends Field<String> {

    private final Map<String, String> options;

    public DropDownField(String key, String defaultValue, Map<String, String> options) {
        super(key, defaultValue);
        this.options = options;
    }

    @Override
    public String parseFromStr(String s) {
        if (isValidInput(s)) {
            return s;
        }
        return getDefaultValue();
    }

    @Override
    public boolean isValidInput(String s) {
        return s != null && options.containsKey(s);
    }

    @Override
    public String whyIsInvalid(String s) {
        if (s == null) return "Input cannot be null";
        if (!options.containsKey(s)) {
            return String.format(
                    "'%s' is not a valid option. Available options are: %s",
                    s,
                    String.join(", ", options.keySet())
            );
        }

        return "Input is valid";
    }

    @Override
    public String[] toTabCompleter(String arg) {
        return options.keySet().stream()
                .filter(key -> key.startsWith(arg))
                .toArray(String[]::new);
    }

}
