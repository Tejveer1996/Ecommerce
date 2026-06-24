package dev.Tejveer.EcomUserAuthService.DTO;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class TokenDetails {
    private String token;
    private Long expiry;
}
