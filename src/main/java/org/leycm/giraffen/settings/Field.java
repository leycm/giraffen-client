package org.leycm.giraffen.settings;

import org.jetbrains.annotations.NotNull;
import org.leycm.storage.StorageBase;

public abstract class Field<T> {
    private T value;
    private final T defaultValue;
    private final String key;

    public Field(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public void assign(T value) {this.value = value;}
    public void assignStr(String value) {this.value = parseFromStr(value);}
    public T getDefaultValue(){return defaultValue;}
    public T getValue() {return value;}
    public String getKey() {return key;}

    public void load(@NotNull StorageBase storage) {
        storage.get(key, value.getClass());
    }

    public void set(@NotNull StorageBase storage) {
        storage.set(key, value);
    }

    public String parseToStr(@NotNull T value) {return value.toString();}

    public abstract T parseFromStr(String s);
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public abstract boolean isValidInput(String s);
    public abstract String[] toTabCompleter();

}
