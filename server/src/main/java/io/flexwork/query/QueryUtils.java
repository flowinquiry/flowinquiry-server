package io.flexwork.query;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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

        String field = filter.getField();
        Object value = filter.getValue();

        // Check for multiple fields to concatenate (e.g., "firstName,lastName" or "field1,field2")
        if (field.contains(",")) {
            String likePattern = "%" + value.toString().toLowerCase() + "%";
            String[] fields = field.split(",");

            // Concatenate specified fields with spaces between them
            Expression<String> concatenatedFields = cb.lower(root.get(fields[0]));

            for (int i = 1; i < fields.length; i++) {
                concatenatedFields =
                        cb.concat(
                                concatenatedFields, cb.concat(" ", cb.lower(root.get(fields[i]))));
            }

            // Apply the like condition to the concatenated fields
            return cb.like(concatenatedFields, likePattern);
        }

        // Check if the field requires a join
        if (field.contains(".")) {
            // Split the field by dot notation to get the join entity and target field
            String[] pathParts = field.split("\\.");
            String joinEntity = pathParts[0];
            String targetField = pathParts[1];

            // Perform the join dynamically
            Join<Object, Object> join = root.join(joinEntity, JoinType.INNER);

            // Create the predicate based on the operator
            switch (filter.getOperator()) {
                case "gt":
                    return cb.greaterThan(join.get(targetField), (Comparable) value);
                case "lt":
                    return cb.lessThan(join.get(targetField), (Comparable) value);
                case "eq":
                    return cb.equal(join.get(targetField), value);
                case "lk":
                    return cb.like(join.get(targetField), "%" + value + "%");
                case "in":
                    return join.get(targetField).in((List<?>) value);
                default:
                    throw new IllegalArgumentException("Invalid operator: " + filter.getOperator());
            }
        } else {
            // No join needed, access the field directly from the root
            switch (filter.getOperator()) {
                case "gt":
                    return cb.greaterThan(root.get(field), (Comparable) value);
                case "lt":
                    return cb.lessThan(root.get(field), (Comparable) value);
                case "eq":
                    return cb.equal(root.get(field), value);
                case "lk":
                    return cb.like(
                            cb.lower(root.get(field)), "%" + value.toString().toLowerCase() + "%");
                case "in":
                    return root.get(field).in((List<?>) value);
                default:
                    throw new IllegalArgumentException("Invalid operator: " + filter.getOperator());
            }
        }
    }
}
