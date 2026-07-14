package dev.Tejveer.EcomUserAuthService.Service.Interface;

import dev.Tejveer.EcomUserAuthService.DTO.AuthResponse;
import dev.Tejveer.EcomUserAuthService.DTO.CreateSellerProfileRequest;
import dev.Tejveer.EcomUserAuthService.DTO.LoginRequest;
import dev.Tejveer.EcomUserAuthService.DTO.RefreshTokenRequest;
import dev.Tejveer.EcomUserAuthService.DTO.SellerProfileResponse;
import dev.Tejveer.EcomUserAuthService.DTO.SignUpResponse;
import dev.Tejveer.EcomUserAuthService.DTO.SignupRequest;
import dev.Tejveer.EcomUserAuthService.DTO.UpdateUserProfileRequest;
import dev.Tejveer.EcomUserAuthService.DTO.UserProfileResponse;
import dev.Tejveer.EcomUserAuthService.Entity.SellerProfile;
import dev.Tejveer.EcomUserAuthService.Exception.ResourceNotFoundException;

public interface UserService {
    SignUpResponse signUp(SignupRequest signupRequestDTO);

    AuthResponse login(LoginRequest loginRequestDTO) throws ResourceNotFoundException;

    AuthResponse refreshAccessToken(RefreshTokenRequest refreshTokenRequest);

    UserProfileResponse getUserProfile() throws ResourceNotFoundException;

    UserProfileResponse updateUserProfile(String userId, UpdateUserProfileRequest updateUserProfileRequest)
            throws ResourceNotFoundException;

    SellerProfileResponse createSeller(String userId, CreateSellerProfileRequest sellerProfileRequest) throws ResourceNotFoundException;

    boolean updateRoleFromUserToSeller(String sellerUserId) throws ResourceNotFoundException;

}
