package dev.Tejveer.EcomUserAuthService.DTO;

import dev.Tejveer.EcomUserAuthService.Entity.Roles;
import dev.Tejveer.EcomUserAuthService.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class SignUpResponseDTO {
    private String id;
    private String name;
    private String email;
    private List<Roles> roles;

    public static SignUpResponseDTO fromUser(User user) {
        if (user == null) {
            return null;
        }
        return SignUpResponseDTO.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .name(user.getName())
                .roles(user.getRoles())
                .build();

    }

}
