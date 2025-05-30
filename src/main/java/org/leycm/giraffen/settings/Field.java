package org.leycm.giraffen.settings;

import org.jetbrains.annotations.NotNull;
import org.leycm.storage.StorageBase;

public abstract class Field<T> {

    private T value;

    public void assign(T value) {
        this.value = value;
    }

    public abstract String[] toTabCompleter();

    public void load(@NotNull StorageBase storage) {
        storage.get("", value.getClass());
    }

    public void set(@NotNull StorageBase storage) {
        storage.set("", value);
    }

}
