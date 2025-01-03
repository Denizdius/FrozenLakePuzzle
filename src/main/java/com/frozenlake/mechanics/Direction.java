package com.frozenlake.mechanics;

public enum Direction {
    UP("u", "Up"),
    DOWN("d", "Down"),
    LEFT("l", "Left"),
    RIGHT("r", "Right");

    private final String code;
    private final String displayName;

    Direction(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Direction fromCode(String code) {
        for (Direction dir : values()) {
            if (dir.code.equals(code.toLowerCase())) {
                return dir;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 