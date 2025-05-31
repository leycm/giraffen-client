package org.leycm.giraffen.settings.fields;

import org.jetbrains.annotations.NotNull;
import org.leycm.giraffen.settings.Field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class StringListField extends Field<List<String>> {

    private final List<String> tabOptions;
    private final int maxListSize;

    public StringListField(String key, List<String> defaultValue) {
        this(key, defaultValue, -1, Collections.emptyList());
    }

    public StringListField(String key, List<String> defaultValue, int maxListSize) {
        this(key, defaultValue, maxListSize, Collections.emptyList());
    }

    public StringListField(String key, List<String> defaultValue, Collection<String> tabOptions) {
        this(key, defaultValue, -1, tabOptions);
    }

    public StringListField(String key, List<String> defaultValue, int maxListSize, Collection<String> tabOptions) {
        super(key, new ArrayList<>(defaultValue));
        this.maxListSize = maxListSize;
        this.tabOptions = List.copyOf(tabOptions);
    }

    @Override
    public List<String> parseFromStr(String s) {
        if (s == null || s.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(s.split(","));
    }

    @Override
    public String parseToStr(@NotNull List<String> value) {
        return String.join(",", value);
    }

    @Override
    public boolean isValidInput(String s) {
        if (s == null) return false;

        if (maxListSize > 0) {
            int currentSize = getValue().size();
            int newItems = s.split(",").length;
            return currentSize + newItems <= maxListSize;
        }
        return true;
    }

    @Override
    public String[] toTabCompleter(String arg) {
        List<String> completions = new ArrayList<>();
        if ("clear".startsWith(arg)) completions.add("clear");

        getValue().forEach(i -> {
            String option = "-" + i;
            if (option.startsWith(arg)) completions.add(option);
        });

        tabOptions.forEach(option -> {
            if (!getValue().contains(option) && option.startsWith(arg)) completions.add(option);
        });

        return completions.toArray(new String[0]);
    }

    public void add(String value) {
        if (value != null && !value.trim().isEmpty()) {
            getValue().add(value.trim());
        }
    }

    public void remove(String value) {
        if (value != null) {
            getValue().remove(value.trim());
        }
    }

    public void clear() {
        getValue().clear();
    }

    @Override
    public void assignStr(String value) {
        if ("clear".equalsIgnoreCase(value)) {
            clear();
        } else if (value != null) {
            String[] parts = value.split(",");
            for (String part : parts) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) {
                    if (trimmed.startsWith("-")) {
                        remove(trimmed.substring(1));
                    } else {
                        add(trimmed);
                    }
                }
            }
        }
    }

}