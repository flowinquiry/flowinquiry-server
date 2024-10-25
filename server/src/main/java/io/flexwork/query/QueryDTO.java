package io.flexwork.query;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryDTO {

    private List<Filter> filters;
}
