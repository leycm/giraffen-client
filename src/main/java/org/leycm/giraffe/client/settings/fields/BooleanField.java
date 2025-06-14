package org.leycm.giraffe.client.settings.fields;

import org.jetbrains.annotations.NotNull;
import org.leycm.giraffe.client.settings.Field;

import java.util.List;
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
    public String whyIsInvalid(String s) {
        if (s == null) {
            return "Input cannot be null";
        }

        String sc = s.trim().toLowerCase();

        if (sc.isEmpty()) {
            return "Empty input provided";
        }

        if (!TRUE_VALUES.contains(sc) && !FALSE_VALUES.contains(sc)) {
            return String.format(
                    "'%s' is not a valid boolean value. Accepted values are: %s (true) or %s (false)",
                    s,
                    String.join(", ", TRUE_VALUES),
                    String.join(", ", FALSE_VALUES)
            );
        }

        return "Input is valid";
    }

    @Override
    public String[] toTabCompleter(@NotNull String arg) {
        if (arg.isEmpty()) return new String[]{"true", "false"};

        String lowerArg = arg.toLowerCase();

        List<String> trueMatches = TRUE_VALUES.stream()
                .filter(v -> v.startsWith(lowerArg))
                .sorted()
                .toList();

        List<String> falseMatches = FALSE_VALUES.stream()
                .filter(v -> v.startsWith(lowerArg))
                .sorted()
                .toList();

        if (!trueMatches.isEmpty()) return trueMatches.toArray(new String[0]);

        if (!falseMatches.isEmpty()) return falseMatches.toArray(new String[0]);

        return new String[]{"true", "false"};
    }

}
