package dev.Tejveer.EcomUserAuthService.Service.Interface;

import dev.Tejveer.EcomUserAuthService.DTO.AuthResponse;
import dev.Tejveer.EcomUserAuthService.DTO.LoginRequestDto;
import dev.Tejveer.EcomUserAuthService.DTO.RefreshTokenRequestDto;
import dev.Tejveer.EcomUserAuthService.DTO.SignUpResponseDTO;
import dev.Tejveer.EcomUserAuthService.DTO.SignupRequestDto;
import dev.Tejveer.EcomUserAuthService.Exception.ResourceNotFoundException;

public interface UserService {
    SignUpResponseDTO signUp(SignupRequestDto signupRequestDTO);

    AuthResponse login(LoginRequestDto loginRequestDTO) throws ResourceNotFoundException;

    AuthResponse refreshAccessToken(RefreshTokenRequestDto refreshTokenRequest);

}
