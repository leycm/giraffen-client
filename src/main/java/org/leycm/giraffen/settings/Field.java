package org.leycm.giraffen.settings;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.leycm.storage.StorageBase;

public abstract class Field<T> {
    private T value;
    private final T defaultValue;
    private final String key;

    public Field(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public void assign(T value) {this.value = value;}
    public void assignStr(String value) {this.value = parseFromStr(value);}
    public T getDefaultValue(){return defaultValue;}
    public T getValue() {return value;}
    public String getKey() {return key;}

    public void load(@NotNull StorageBase storage) {
        //noinspection unchecked
        T storageValue = (T) storage.get(key, value.getClass());
        value = storageValue != null ? storageValue : defaultValue;
    }

    public void set(@NotNull StorageBase storage) {
        T value = this.value == null ?  defaultValue : this.value;
        storage.set(key, value);
    }
    public String parseToStr() {return parseToStr(value);}
    public String parseToStr(@NotNull T value) {return value.toString();}

    public abstract T parseFromStr(String s);
    public abstract boolean isValidInput(String s);
    public abstract String whyIsInvalid(String s);
    public abstract String[] toTabCompleter(String s);

    public ArgumentType<String> toArgumentType() {
        return reader -> {
            String input = reader.readUnquotedString();
            if (!isValidInput(input)) {
                throw new SimpleCommandExceptionType(Text.literal(whyIsInvalid(input))).create();
            }
            return input;
        };
    }

}
