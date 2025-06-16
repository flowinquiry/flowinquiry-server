package io.flowinquiry.query;

/** Enum representing the available filter operators for query operations. */
public enum FilterOperator {
    GT("gt"), // Greater Than
    LT("lt"), // Less Than
    EQ("eq"), // Equals
    NE("ne"), // Not Equals
    LK("lk"), // Like
    IN("in"); // In

    private final String value;

    FilterOperator(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Convert a string value to the corresponding enum value.
     *
     * @param value the string value to convert
     * @return the corresponding enum value
     * @throws IllegalArgumentException if the value doesn't match any enum value
     */
    public static FilterOperator fromValue(String value) {
        for (FilterOperator operator : FilterOperator.values()) {
            if (operator.value.equals(value)) {
                return operator;
            }
        }
        throw new IllegalArgumentException("Invalid operator value: " + value);
    }
}
