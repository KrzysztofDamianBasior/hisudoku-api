package org.hisudoku.hisudokuapi.users.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Data;

import org.hisudoku.hisudokuapi.users.entities.HSUser;
import org.hisudoku.hisudokuapi.users.enums.Role;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class HSUserPrincipal implements UserDetails {
    private final String id;
    private final String name;

    @JsonIgnore     // protects against accidental serialization of a sensitive field
    private final String password;

    private final Role role; //  alternatively- private final List<GrantedAuthority> authorities;

    private final LocalDateTime enrollmentDate;

    private final LocalDateTime updatedAt;

    private final String email;
    // private final String untrustedNotYetActivatedEmailAddress;

    @Builder
    public HSUserPrincipal(String id, String name, String password, String role, String enrollmentDate, String updatedAt, String email) {
        this.id = id;
        this.role = Role.valueOf(role); // that the name must be an exact match, or else it throws an IllegalArgumentException
        this.name = name;
        this.password = password;
        this.enrollmentDate = LocalDateTime.parse(enrollmentDate);
        this.updatedAt = LocalDateTime.parse(updatedAt);
        this.email = email;
    }

    @Builder
    public HSUserPrincipal(HSUser user) {
        this.name = user.getName();
        this.password = user.getPassword();
        this.role = Role.valueOf(user.getRole());
        this.id = user.getId();
        this.enrollmentDate = user.getAccountUsageInfo().getEnrollmentDate();
        this.updatedAt = user.getAccountUsageInfo().getUpdatedAt();
        this.email = user.getEmail();

        // if we kept the roles in a comma-separated string
        // this.authorities = Arrays.stream(user.getRoles().split(","))
        //         .map(SimpleGrantedAuthority::new)
        //         .collect(Collectors.toList());
    }

    @Override
    public List<SimpleGrantedAuthority> getAuthorities() {
        return this.role.getAuthorities();
    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return this.role.getAuthorities();
//    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}