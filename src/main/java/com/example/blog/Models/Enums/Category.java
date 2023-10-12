package com.example.blog.Models.Enums;

public enum Category {
    TECHNOLOGY("Technology"),
    ART_DESIGN("Art & Design"),
    TRAVEL("Travel"),
    HEALTH_FITNESS("Health & Fitness");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}