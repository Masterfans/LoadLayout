package com.jrobot.layout;

public enum LoadStatus {
    LOADING(1),
    EMPTY(2),
    ERROR(3),
    CONTENT(4);
    private int type;

    private LoadStatus(int type) {
        this.type = type;
    }

    public int value() {
        return type;
    }

    public static LoadStatus valueOf(int type) {
        LoadStatus[] values = LoadStatus.values();
        for (LoadStatus v : values) {
            if (v.value() == type) {
                return v;
            }
        }
        return null;
    }
}
