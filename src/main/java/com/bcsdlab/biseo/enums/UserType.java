package com.bcsdlab.biseo.enums;

public enum UserType {
    NONE(0), COUNCIL(1);

    private final int level;

    UserType(int level) {
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }
}
