package io.flexwork.query;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupFilter {

    private List<Filter> filters; // Simple filters in this group

    private List<GroupFilter> groups; // Nested groups

    private String logicalOperator; // "AND" or "OR"
}
