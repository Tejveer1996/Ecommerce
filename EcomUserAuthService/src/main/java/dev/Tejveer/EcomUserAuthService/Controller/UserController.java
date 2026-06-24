package dev.Tejveer.EcomUserAuthService.Controller;

import dev.Tejveer.EcomUserAuthService.DTO.AuthResponse;
import dev.Tejveer.EcomUserAuthService.DTO.LoginRequestDto;
import dev.Tejveer.EcomUserAuthService.DTO.RefreshTokenRequestDto;
import dev.Tejveer.EcomUserAuthService.DTO.SignUpResponseDTO;
import dev.Tejveer.EcomUserAuthService.DTO.SignupRequestDto;
import dev.Tejveer.EcomUserAuthService.Exception.ResourceNotFoundException;
import dev.Tejveer.EcomUserAuthService.Service.Interface.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<SignUpResponseDTO> signUp(@RequestBody SignupRequestDto signupRequestDTO) {
        SignUpResponseDTO response = userService.signUp(signupRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequestDto loginRequestDTO,
                                              HttpServletResponse httpServletResponse) throws ResourceNotFoundException {

        AuthResponse authResponse = userService.login(loginRequestDTO);
//            generateCookies(httpServletResponse, authResponse);
        return ResponseEntity.ok(authResponse);

    }

    @PostMapping("/token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequest) throws AuthenticationException {
        AuthResponse authResponse = userService.refreshAccessToken(refreshTokenRequest);
        return ResponseEntity.ok(authResponse);
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
