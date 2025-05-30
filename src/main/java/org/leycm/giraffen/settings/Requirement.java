package org.leycm.giraffen.settings;

@FunctionalInterface
public interface Requirement {
    boolean check(Setting setting);
}
