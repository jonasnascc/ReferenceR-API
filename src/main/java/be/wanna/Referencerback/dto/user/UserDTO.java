package be.wanna.Referencerback.dto.user;

import be.wanna.Referencerback.entity.user.UserRole;

public record UserDTO(String id, String name, UserRole role) {
}
