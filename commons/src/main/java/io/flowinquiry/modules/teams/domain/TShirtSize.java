package io.flowinquiry.modules.teams.domain;

public enum TShirtSize {
    S("Small"),
    M("Medium"),
    L("Large"),
    XL("Extra-Large");

    private final String description;

    TShirtSize(String description) {
        this.description = description;
    }
}
