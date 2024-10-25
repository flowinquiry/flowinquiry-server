package io.flexwork.query;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Filter {

    @NotEmpty private String field;

    @NotNull private String operator;

    @NotNull private Object value;
}
