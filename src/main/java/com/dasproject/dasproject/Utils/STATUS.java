package com.dasproject.dasproject.Utils;

public enum STATUS {
    WAITING("Waiting in queue..."),
    BUILDING("Building project..."),
    BUILDING_SUCCESS("Build successfully..."),
    BUILDING_ERROR("Error during build..."),
    RUNNING("Running..."),
    RUNNING_SUCCESS("Run successfully..."),
    RUNNING_ERROR("Error during run..."),
    COMPLETED("Build finished!");

    private final String message;

    STATUS(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
