package io.flexwork.query;

import lombok.Data;

@Data
public class QueryFilter {
    private String field;
    private String operator;
    private String value;

    public QueryFilter(String field, String operator, String value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }
}
