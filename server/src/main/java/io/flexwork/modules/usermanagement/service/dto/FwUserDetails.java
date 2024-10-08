package io.flexwork.modules.usermanagement.service.dto;

import static java.util.Comparator.comparing;

import io.flexwork.modules.usermanagement.domain.Authority;
import io.flexwork.modules.usermanagement.domain.User;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

/** Flexwork user details that enhance Spring security user details with richer information */
public class FwUserDetails implements UserDetails {

    private final Long userId;

    private final String username;

    private final String password;

    private final Set<GrantedAuthority> authorities;

    public FwUserDetails(User user) {
        this.userId = user.getId();
        this.username = user.getEmail();
        this.password = user.getPassword();

        SortedSet<GrantedAuthority> sortedAuthorities =
                new TreeSet<>(comparing(GrantedAuthority::getAuthority));

        List<SimpleGrantedAuthority> grantedAuthorities =
                user.getAuthorities().stream()
                        .map(Authority::getName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        for (GrantedAuthority grantedAuthority : grantedAuthorities) {
            Assert.notNull(
                    grantedAuthority, "GrantedAuthority list cannot contain any null elements");
            sortedAuthorities.add(grantedAuthority);
        }

        this.authorities = Collections.unmodifiableSet(sortedAuthorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public Long getUserId() {
        return userId;
    }
}
