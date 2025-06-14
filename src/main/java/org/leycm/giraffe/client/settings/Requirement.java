package org.leycm.giraffe.client.settings;

@FunctionalInterface
public interface Requirement {
    boolean check(Setting setting);
}
