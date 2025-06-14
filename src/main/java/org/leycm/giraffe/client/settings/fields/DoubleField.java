package org.leycm.giraffe.client.settings.fields;

import org.jetbrains.annotations.NotNull;
import org.leycm.giraffe.client.settings.Field;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DoubleField extends Field<Double> {

    private final double min;
    private final double max;
    private final int decimalPlaces;
    private final DecimalFormat decimalFormat;

    public DoubleField(String key, Double defaultValue, double min, double max, int decimalPlaces) {
        super(key, defaultValue);
        this.min = min;
        this.max = max;
        this.decimalPlaces = decimalPlaces;

        this.decimalFormat = new DecimalFormat("0." + "0".repeat(decimalPlaces),
                new DecimalFormatSymbols(Locale.US));
    }

    @Override
    public Double parseFromStr(String s) {
        if (!isValidInput(s)) return getDefaultValue();

        double value = Double.parseDouble(s);
        double factor = Math.pow(10, decimalPlaces);
        return Math.round(value * factor) / factor;
    }

    @Override
    public String parseToStr(@NotNull Double value) {
        return decimalFormat.format(value);
    }

    @Override
    public boolean isValidInput(String s) {
        try {
            double number = Double.parseDouble(s);

            String[] parts = s.split("\\.");
            if (parts.length == 2 && parts[1].length() > decimalPlaces) {
                return false;
            }

            return number >= min && number <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String whyIsInvalid(String s) {
        try {
            double number = Double.parseDouble(s);

            String[] parts = s.split("\\.");
            if (parts.length == 2 && parts[1].length() > decimalPlaces) {
                return String.format(
                        "Number has too many decimal places (%d found, maximum allowed: %d)",
                        parts[1].length(), decimalPlaces
                );
            }

            if (number < min) {
                return String.format(
                        "Number %f is below minimum allowed value (%f)",
                        number, min
                );
            }
            if (number > max) {
                return String.format(
                        "Number %f is above maximum allowed value (%f)",
                        number, max
                );
            }

        } catch (NumberFormatException e) {
            return String.format(
                    "'%s' is not a valid number",
                    s
            );
        }

        return "Input is valid";
    }

    @Override
    public String[] toTabCompleter(String arg) {
        return new String[]{"<" + min + "-" + max + " (" + decimalPlaces + " decimal places)>"};
    }
}