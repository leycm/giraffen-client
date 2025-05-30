package org.leycm.giraffen.settings;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Setting {
    private final List<Field<?>> fields = new ArrayList<>();
    private final String id;
    private final Requirement requirement;

    private String prefix;
    private String suffix;

    @Contract("_ -> new")
    public static @NotNull Setting of(String id){
        return new Setting(id, (setting) -> true);
    }

    public static @NotNull Setting of(String id, Requirement requirement){
        return new Setting(id, requirement);
    }

    private Setting(String id, Requirement requirement){
        this.id = id;
        this.requirement = requirement;
    }

    public Setting field(int index, Field<?> field) {
        fields.add(index, field);
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

    public String[] toTabCompleter(int index) {
        return fields.get(index).toTabCompleter();
    }

    public int size() {return fields.size();}
    public boolean isAccessible() {return requirement.check(this);}
    public String getId() {return id;}
    public String getPrefix() {return prefix;}
    public String getSuffix() {return suffix;}
    public Field<?> getField(int index) {return fields.get(index);}
    public List<Field<?>> getFields() {return fields;}

}
