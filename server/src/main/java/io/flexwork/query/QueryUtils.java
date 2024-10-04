package io.flexwork.query;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.domain.Specification;

public class QueryUtils {
    public static List<QueryFilter> parseFiltersFromParams(Map<String, String> params) {
        List<QueryFilter> filters = new ArrayList<>();
        int i = 0;
        while (params.containsKey("filters[" + i + "][field]")) {
            String field = params.get("filters[" + i + "][field]");
            String operator = params.get("filters[" + i + "][operator]");
            String value = params.get("filters[" + i + "][value]");
            filters.add(new QueryFilter(field, operator, value));
            i++;
        }
        return filters;
    }

    // Build dynamic Specification for the filters
    public static <Entity> Specification<Entity> buildSpecification(List<QueryFilter> filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (QueryFilter filter : filters) {
                switch (filter.getOperator()) {
                    case "equals":
                        predicates.add(
                                criteriaBuilder.equal(
                                        root.get(filter.getField()), filter.getValue()));
                        break;
                    case "lt":
                        predicates.add(
                                criteriaBuilder.lessThan(
                                        root.get(filter.getField()),
                                        (Comparable) filter.getValue()));
                        break;
                    case "gt":
                        predicates.add(
                                criteriaBuilder.greaterThan(
                                        root.get(filter.getField()),
                                        (Comparable) filter.getValue()));
                        break;
                    case "in":
                        // Split the string value into a list (assuming values are comma-separated)
                        List<String> values = Arrays.asList(filter.getValue().split(","));
                        predicates.add(root.get(filter.getField()).in(values));
                        break;
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
