package io.flexwork.query;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.jpa.domain.Specification;

public class QueryUtils {

    public static <Entity> Specification<Entity> createSpecification(Optional<QueryDTO> queryDTO) {
        return queryDTO.map(
                        dto ->
                                (Specification<Entity>)
                                        (root, query, cb) -> {
                                            List<Predicate> predicates =
                                                    dto.getFilters().stream()
                                                            .map(
                                                                    filter ->
                                                                            createPredicate(
                                                                                    filter, root,
                                                                                    cb))
                                                            .collect(Collectors.toList());
                                            return cb.and(predicates.toArray(new Predicate[0]));
                                        })
                .orElse(null); // Return null if queryDTO is not present
    }

    private static <Entity> Predicate createPredicate(
            Filter filter, Root<Entity> root, CriteriaBuilder cb) {
        switch (filter.getOperator()) {
            case "gt":
                return cb.greaterThan(root.get(filter.getField()), (Comparable) filter.getValue());
            case "lt":
                return cb.lessThan(root.get(filter.getField()), (Comparable) filter.getValue());
            case "eq":
                return cb.equal(root.get(filter.getField()), filter.getValue());
            case "in":
                return root.get(filter.getField()).in((List<?>) filter.getValue());
            default:
                throw new IllegalArgumentException("Invalid operator: " + filter.getOperator());
        }
    }
}
