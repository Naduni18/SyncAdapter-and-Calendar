package net.chmielowski.syncadaptertest;

import javax.inject.Inject;

public class Dependency {
    @Inject
    Dependency() {}

    @Override
    public String toString() {
        return "I'm a dependency!";
    }
}
