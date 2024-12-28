package io.flowinquiry.modules.usermanagement.service.mapper;

import io.flowinquiry.modules.usermanagement.domain.User;
import org.mapstruct.Named;

public class UserMapperUtils {

    @Named("mapUserToFullName")
    public static String mapUserToFullName(User user) {
        try {
            return user != null
                    ? (user.getFirstName() != null ? user.getFirstName() : "")
                            + " "
                            + (user.getLastName() != null ? user.getLastName() : "")
                    : "";
        } catch (Exception e) {
            return "";
        }
    }
}
