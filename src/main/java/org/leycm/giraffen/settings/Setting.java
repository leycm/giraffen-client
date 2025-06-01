package org.leycm.giraffen.settings;

import org.jetbrains.annotations.NotNull;
import org.leycm.storage.StorageBase;

import java.util.ArrayList;
import java.util.List;

public class Setting {
    private final List<Field<?>> fields = new ArrayList<>();
    private final String id;
    private final StorageBase storage;
    private Requirement requirement;

    private String prefix;
    private String suffix;
    private String description;
    private Group group;

    public static @NotNull Setting of(String id, StorageBase storage){
        return new Setting(id, storage, (setting) -> true);
    }

    public static @NotNull Setting of(String id, StorageBase storage, Requirement requirement){
        return new Setting(id, storage, requirement);
    }

    private Setting(String id, StorageBase storage, Requirement requirement){
        this.id = id;
        this.requirement = requirement;
        this.storage = storage;
    }

    public Setting field(Field<?> field) {
        fields.add(field);
        field.load(storage);
        return this;
    }

    public Setting field(int index, Field<?> field) {
        fields.add(index, field);
        field.load(storage);
        return this;
    }

    public Setting prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public Setting suffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public Setting description(String description) {
        this.description = description;
        return this;
    }

    public Setting group(Group group) {
        this.group = group;
        return this;
    }

    public Setting condition(Requirement requirement) {
        this.requirement = requirement;
        return this;
    }

    public String[] toTabCompleter(int index, String s) {
        if(size() <= index ) return new String[]{"<null>"};
        return fields.get(index).toTabCompleter(s);
    }

    public void assign(int index, String value) {
        fields.get(index).assignStr(value);
        if(storage != null) fields.get(index).set(storage);
    }

    public boolean isValidInput(int index, String value) {
        return fields.get(index).isValidInput(value);
    }


    public int size() {return fields.size();}
    public boolean isAccessible() {return requirement.check(this);}
    public String getId() {return id;}
    public String getPrefix() {return prefix;}
    public String getSuffix() {return suffix;}
    public String getDescription() {return description;}
    public Group getGroup() {return group;}
    public Field<?> getField(int index) {return fields.get(index);}
    public List<Field<?>> getFields() {return fields;}

}
