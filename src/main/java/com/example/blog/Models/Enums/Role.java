package com.example.blog.Models.Enums;

public enum Role {
    USER("User"),
    ADMIN("Admin");
    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
