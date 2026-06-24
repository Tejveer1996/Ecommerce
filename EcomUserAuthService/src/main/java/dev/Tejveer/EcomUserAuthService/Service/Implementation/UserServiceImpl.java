package dev.Tejveer.EcomUserAuthService.Service.Implementation;

import dev.Tejveer.EcomUserAuthService.Config.JwtUtils;
import dev.Tejveer.EcomUserAuthService.DTO.AuthResponse;
import dev.Tejveer.EcomUserAuthService.DTO.LoginRequestDto;
import dev.Tejveer.EcomUserAuthService.DTO.RefreshTokenRequestDto;
import dev.Tejveer.EcomUserAuthService.DTO.SignUpResponseDTO;
import dev.Tejveer.EcomUserAuthService.DTO.SignupRequestDto;
import dev.Tejveer.EcomUserAuthService.DTO.TokenDetails;
import dev.Tejveer.EcomUserAuthService.Entity.Roles;
import dev.Tejveer.EcomUserAuthService.Entity.User;
import dev.Tejveer.EcomUserAuthService.Exception.ResourceNotFoundException;
import dev.Tejveer.EcomUserAuthService.Repository.UserRepository;
import dev.Tejveer.EcomUserAuthService.Service.Interface.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    AuthenticationManager authenticationManager;
    CustomerDetailService customerDetailService;
    BCryptPasswordEncoder bCryptPasswordEncoder;
    JwtUtils jwtUtils;


    @Override
    public SignUpResponseDTO signUp(SignupRequestDto signupRequestDTO) {
        // Check whether user exist in the db.
        Optional<User> existingUser = userRepository.findByEmail(signupRequestDTO.getEmail());
        if (existingUser.isPresent() && existingUser.get().getRoles().contains(Roles.USER)) {
            throw new BadCredentialsException("User already exist for email : " + signupRequestDTO.getEmail());
        }


        // Save new user in the db.
        User newUser = User.builder()
                .name(signupRequestDTO.getName())
                .email(signupRequestDTO.getEmail())
                .password(bCryptPasswordEncoder.encode(signupRequestDTO.getPassword()))
                .roles(List.of(Roles.USER))
                .build();
        return SignUpResponseDTO.fromUser((userRepository.save(newUser)));
    }

    @Override
    public AuthResponse login(LoginRequestDto loginRequest) throws ResourceNotFoundException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            User user = (User) authentication.getPrincipal();
            TokenDetails accessToken = jwtUtils.generateToken(user);
            TokenDetails refreshToken = jwtUtils.generateRefreshToken(user);
            return AuthResponse.builder()
                    .accessToken(accessToken.getToken())
                    .refreshToken(refreshToken.getToken())
                    .accessTokenExpiry(accessToken.getExpiry())
                    .refreshTokenExpiry(refreshToken.getExpiry())
                    .tokenType("Bearer")
                    .build();
        } catch (Exception e) {
            log.error("Error :: ", e);
            throw new ResourceNotFoundException("Either email or password is incorrect");
        }
    }

    @Override
    public AuthResponse refreshAccessToken(RefreshTokenRequestDto refreshTokenRequest) {
        try {
            String userId = jwtUtils.validateRefreshToken(refreshTokenRequest.getRefreshToken());
            User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(
                    () -> new ResourceNotFoundException("Invalid user")
            );
            TokenDetails accessToken = jwtUtils.generateToken(user);
            TokenDetails refreshToken = jwtUtils.generateRefreshToken(user);
            return AuthResponse.builder()
                    .accessToken(accessToken.getToken())
                    .refreshToken(refreshToken.getToken())
                    .accessTokenExpiry(accessToken.getExpiry())
                    .refreshTokenExpiry(refreshToken.getExpiry())
                    .tokenType("Bearer")
                    .build();
        } catch (Exception e) {
            throw new AuthenticationServiceException("Invalid refresh token");
        }


    }

}
