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
import dev.Tejveer.EcomUserAuthService.Entity.User;
import dev.Tejveer.EcomUserAuthService.Exception.ResourceNotFoundException;
import dev.Tejveer.EcomUserAuthService.Exception.SellerNotVerifiedException;
import dev.Tejveer.EcomUserAuthService.Service.Interface.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignupRequest signupRequestDTO) {
        SignUpResponse response = userService.signUp(signupRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequestDTO,
                                              HttpServletResponse httpServletResponse) throws ResourceNotFoundException {

        AuthResponse authResponse = userService.login(loginRequestDTO);
//            generateCookies(httpServletResponse, authResponse);
        return ResponseEntity.ok(authResponse);

    }

    @PostMapping("/token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) throws AuthenticationException {
        AuthResponse authResponse = userService.refreshAccessToken(refreshTokenRequest);
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getUserProfile() throws ResourceNotFoundException {
        UserProfileResponse profileResponse = userService.getUserProfile();
        return ResponseEntity.ok(profileResponse);
    }

    @PutMapping("/update/me")
    public ResponseEntity<UserProfileResponse> updateUserProfile(@RequestBody UpdateUserProfileRequest updateUserProfileRequest)
            throws ResourceNotFoundException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserProfileResponse userProfileResponse = userService.updateUserProfile(user.getId().toString(),
                updateUserProfileRequest);
        return ResponseEntity.ok(userProfileResponse);
    }

    /**
     * Request of the user for conversion to seller by taking the seller info in request body.
     * @param sellerProfileRequest
     * @return
     * @throws ResourceNotFoundException
     */
    @PutMapping("/create/seller")
    public ResponseEntity<SellerProfileResponse> createSeller(@RequestBody CreateSellerProfileRequest sellerProfileRequest)
            throws ResourceNotFoundException {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SellerProfileResponse response = userService.createSeller(userId, sellerProfileRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/role/{userId}")
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
