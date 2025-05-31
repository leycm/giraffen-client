package org.leycm.giraffen.settings.fields;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ParagraphStringField extends StringField{

    public ParagraphStringField(String key, String defaultValue, String tabCompleter) {
        super(key, defaultValue, tabCompleter);
    }

    public ParagraphStringField(String key, String defaultValue, int minLength, int maxLength, String regex) {
        super(key, defaultValue, minLength, maxLength, regex);
    }

    @Contract(pure = true)
    @Override
    public @NotNull String parseFromStr(@NotNull String s) {
        return s.replace('&', '§');
    }

    @Contract(pure = true)
    @Override
    public @NotNull String parseToStr(@NotNull String s) {
        return s.replace('§', '&');
    }

}
