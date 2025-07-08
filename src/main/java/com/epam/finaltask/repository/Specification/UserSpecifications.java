package com.epam.finaltask.repository.Specification;

import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class UserSpecifications {

    public static Specification<User> idEquals(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        try {
            UUID uuid = UUID.fromString(id);
            return (root, query, cb) -> cb.equal(root.get("id"), uuid);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static Specification<User> usernameContains(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%");
    }

    public static Specification<User> emailContains(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<User> hasRole(String role) {
        if (role == null || role.isBlank()) {
            return null;
        }
        try {
            Role roleEnum = Role.valueOf(role);
            return (root, query, cb) -> cb.equal(root.get("role"), roleEnum);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static Specification<User> isActive(Boolean active) {
        if (active == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("active"), active);
    }
}
