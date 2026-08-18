package dev.Tejveer.EcomUserAuthService.Controller;

import dev.Tejveer.EcomUserAuthService.DTO.AuthResponse;
import dev.Tejveer.EcomUserAuthService.DTO.CreateSellerProfileRequest;
import dev.Tejveer.EcomUserAuthService.DTO.LoginRequest;
import dev.Tejveer.EcomUserAuthService.DTO.RefreshTokenRequest;
import dev.Tejveer.EcomUserAuthService.DTO.SellerProfileResponse;
import dev.Tejveer.EcomUserAuthService.DTO.SignUpResponse;
import dev.Tejveer.EcomUserAuthService.DTO.SignupRequest;
import dev.Tejveer.EcomUserAuthService.DTO.UpdateUserProfileRequest;
import dev.Tejveer.EcomUserAuthService.DTO.UserProfileResponse;
import dev.Tejveer.EcomUserAuthService.Exception.ResourceNotFoundException;
import dev.Tejveer.EcomUserAuthService.Exception.SellerNotVerifiedException;
import dev.Tejveer.EcomUserAuthService.Service.Interface.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;
import java.util.List;

@Tag(
        name = "User APIs",
        description = "Operations related to user authentication and profile management"
)
@RestController
@RequestMapping("apis/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "Sign up", description = "Register a new user account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User Registered Successfully"),
            @ApiResponse(responseCode = "400", description = "Validation Failed", content = @Content)
    })
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignupRequest signupRequestDTO) {
        SignUpResponse response = userService.signUp(signupRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Login", description = "Authenticate a user and issue access/refresh tokens")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login Successful"),
            @ApiResponse(responseCode = "404", description = "User Not Found", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequestDTO,
                                              HttpServletResponse httpServletResponse) throws ResourceNotFoundException {

        AuthResponse authResponse = userService.login(loginRequestDTO);
//            generateCookies(httpServletResponse, authResponse);
        return ResponseEntity.ok(authResponse);

    }

    @Operation(summary = "Refresh access token", description = "Exchange a valid refresh token for a new access token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token Refreshed Successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid Or Expired Refresh Token", content = @Content)
    })
    @PostMapping("/token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) throws AuthenticationException {
        AuthResponse authResponse = userService.refreshAccessToken(refreshTokenRequest);
        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Get current user profile", description = "Fetch the profile of the currently authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile Fetched Successfully"),
            @ApiResponse(responseCode = "404", description = "User Not Found", content = @Content)
    })
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserProfileResponse> getUserProfile() throws ResourceNotFoundException {
        UserProfileResponse profileResponse = userService.getUserProfile();
        return ResponseEntity.ok(profileResponse);
    }

    @Operation(summary = "Update current user profile", description = "Update the profile of the currently authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile Updated Successfully"),
            @ApiResponse(responseCode = "400", description = "Validation Failed", content = @Content),
            @ApiResponse(responseCode = "404", description = "User Not Found", content = @Content)
    })
    @PutMapping("/update/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserProfileResponse> updateUserProfile(@RequestBody UpdateUserProfileRequest updateUserProfileRequest)
            throws ResourceNotFoundException {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserProfileResponse userProfileResponse = userService.updateUserProfile(userId, updateUserProfileRequest);
        return ResponseEntity.ok(userProfileResponse);
    }

    /**
     * Request of the user for conversion to seller by taking the seller info in request body.
     *
     * @param sellerProfileRequest
     * @return
     * @throws ResourceNotFoundException
     */
    @Operation(summary = "Request seller conversion", description = "Submit seller profile details to request conversion from user to seller")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Seller Profile Request Submitted"),
            @ApiResponse(responseCode = "400", description = "Validation Failed", content = @Content),
            @ApiResponse(responseCode = "404", description = "User Not Found", content = @Content)
    })
    @PutMapping("/create/seller")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SellerProfileResponse> createSeller(@RequestBody CreateSellerProfileRequest sellerProfileRequest)
            throws ResourceNotFoundException {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SellerProfileResponse response = userService.createSeller(userId, sellerProfileRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Approve user-to-seller role update", description = "Update a user's role to seller once seller verification is complete")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role Updated Successfully"),
            @ApiResponse(responseCode = "400", description = "Seller Not Yet Verified Or Already A Seller", content = @Content)
    })
    @PutMapping("/update/role/user-seller/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity updateRole(@PathVariable String userId) throws SellerNotVerifiedException, ResourceNotFoundException {
        try {
            boolean updated = userService.updateRoleFromUserToSeller(userId);
            if (updated) {
                return ResponseEntity.ok("User : " + userId + " has been updated to seller role");
            } else {
                throw new SellerNotVerifiedException("Seller is already a seller or yet to verified");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Seller profile", description = "Get the seller profile if the user is a seller too")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Validation Failed", content = @Content),
            @ApiResponse(responseCode = "404", description = "User is not seller", content = @Content)
    })
    @GetMapping("/seller-profile")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<SellerProfileResponse> getSellerProfile() throws ResourceNotFoundException {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SellerProfileResponse response = userService.getSellerProfile(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Seller profile list", description = "Get all the seller profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profiles fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Validation Failed", content = @Content),
            @ApiResponse(responseCode = "404", description = "User is not seller", content = @Content)
    })
    @GetMapping("/seller-profile/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SellerProfileResponse>> getAllSellerProfiles() {
        return ResponseEntity.ok(userService.getAllSellerProfile());
    }

//    private void generateCookies(HttpServletResponse httpServletResponse, AuthResponse authResponse){
//
//        // Access token cookie
//        Cookie accessTokenCookie = new Cookie("accessToken", authResponse.getAccessToken());
//        accessTokenCookie.setHttpOnly(true);
//        accessTokenCookie.setPath("/");
//        accessTokenCookie.setMaxAge(15*60);
//        accessTokenCookie.setSecure(true);
//
//        httpServletResponse.addCookie(accessTokenCookie);
//
//        // Refresh token cookie
//        Cookie refreshTokenCookie = new Cookie("refreshToken", authResponse.getRefreshToken());
//        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setPath("/");
//        refreshTokenCookie.setMaxAge(7*24*60*60);
//        refreshTokenCookie.setSecure(true);
//
//        httpServletResponse.addCookie(refreshTokenCookie);
//    }
}
