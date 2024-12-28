package io.flowinquiry.query;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryDTO {

    private List<GroupFilter> groups;

    private List<Filter> filters;
}
