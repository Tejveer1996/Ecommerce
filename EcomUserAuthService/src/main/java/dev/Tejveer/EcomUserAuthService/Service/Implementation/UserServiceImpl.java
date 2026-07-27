package dev.Tejveer.EcomUserAuthService.Service.Implementation;

import dev.Tejveer.EcomUserAuthService.Config.JwtUtils;
import dev.Tejveer.EcomUserAuthService.DTO.AddressRequest;
import dev.Tejveer.EcomUserAuthService.DTO.AddressResponseDTO;
import dev.Tejveer.EcomUserAuthService.DTO.AuthResponse;
import dev.Tejveer.EcomUserAuthService.DTO.CreateSellerProfileRequest;
import dev.Tejveer.EcomUserAuthService.DTO.LoginRequest;
import dev.Tejveer.EcomUserAuthService.DTO.RefreshTokenRequest;
import dev.Tejveer.EcomUserAuthService.DTO.SellerProfileResponse;
import dev.Tejveer.EcomUserAuthService.DTO.SignUpResponse;
import dev.Tejveer.EcomUserAuthService.DTO.SignupRequest;
import dev.Tejveer.EcomUserAuthService.DTO.TokenDetails;
import dev.Tejveer.EcomUserAuthService.DTO.UpdateUserProfileRequest;
import dev.Tejveer.EcomUserAuthService.DTO.UserProfileResponse;
import dev.Tejveer.EcomUserAuthService.Entity.Address;
import dev.Tejveer.EcomUserAuthService.Entity.AddressType;
import dev.Tejveer.EcomUserAuthService.Entity.Roles;
import dev.Tejveer.EcomUserAuthService.Entity.SellerProfile;
import dev.Tejveer.EcomUserAuthService.Entity.User;
import dev.Tejveer.EcomUserAuthService.Exception.ResourceNotFoundException;
import dev.Tejveer.EcomUserAuthService.Repository.AddressRepository;
import dev.Tejveer.EcomUserAuthService.Repository.SellerRepository;
import dev.Tejveer.EcomUserAuthService.Repository.UserRepository;
import dev.Tejveer.EcomUserAuthService.Service.Interface.UserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
    ModelMapper modelMapper;
    SellerRepository sellerRepository;
    AddressRepository addressRepository;


    @Override
    public SignUpResponse signUp(SignupRequest signupRequestDTO) {
        // Check whether user exist in the db.
        try {
            Optional<User> existingUser = userRepository.findByEmail(signupRequestDTO.getEmail());
            if (existingUser.isPresent() && existingUser.get().getRoles().contains(Roles.USER)) {
                throw new IllegalArgumentException("User already exist for email : " + signupRequestDTO.getEmail());
            }


            // Save new user in the db.
            User newUser = User.builder()
                    .name(signupRequestDTO.getName())
                    .email(signupRequestDTO.getEmail())
                    .password(bCryptPasswordEncoder.encode(signupRequestDTO.getPassword()))
                    .roles(List.of(Roles.USER))
                    .build();
            return SignUpResponse.fromUser((userRepository.save(newUser)));
        } catch (Exception e) {
            throw new BadCredentialsException("Error :: " + e.getMessage());
        }
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) throws ResourceNotFoundException {
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
    public AuthResponse refreshAccessToken(RefreshTokenRequest refreshTokenRequest) {
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

    @Override
    public UserProfileResponse getUserProfile() throws ResourceNotFoundException {
        try {
            String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userRepository.findById(UUID.fromString(userId)).get();
            SellerProfileResponse sellerProfileResponse = modelMapper.map(user.getSellerProfile(), SellerProfileResponse.class);
            List<AddressResponseDTO> addresses = addressRepository.findByUserId(UUID.fromString(userId)).stream()
                    .map(address -> modelMapper.map(address, AddressResponseDTO.class))
                    .collect(Collectors.toList());
            return UserProfileResponse.builder()
                    .id(user.getId().toString())
                    .name(user.getName())
                    .email(user.getEmail())
                    .phoneNumber(user.getPhoneNumber())
                    .addresses(addresses)
                    .profilePictureUrl(user.getProfilePictureUrl())
                    .createdAt(user.getCreatedAt().toString())
                    .roles(user.getRoles())
                    .build();
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error : " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public UserProfileResponse updateUserProfile(String userId, UpdateUserProfileRequest updateUserProfileRequest)
            throws ResourceNotFoundException {
        try {
            User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(
                    () -> new IllegalArgumentException("Invalid user Id")
            );
            List<AddressResponseDTO> addresses = addressRepository.findByUserId(UUID.fromString(userId)).stream()
                    .map(address -> modelMapper.map(address, AddressResponseDTO.class))
                    .collect(Collectors.toList());

            user.setName(updateUserProfileRequest.getName());
            user.setPhoneNumber(updateUserProfileRequest.getPhoneNumber());
            user.setProfilePictureUrl(updateUserProfileRequest.getProfilePictureUrl());
            return UserProfileResponse.builder()
                    .id(user.getId().toString())
                    .name(user.getName())
                    .email(user.getEmail())
                    .phoneNumber(user.getPhoneNumber())
                    .profilePictureUrl(user.getProfilePictureUrl())
                    .addresses(addresses)
                    .createdAt(user.getCreatedAt().toString())
                    .roles(user.getRoles())
                    .build();
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error :: " + e.getMessage());
        }
    }


    @Transactional
    @Override
    public SellerProfileResponse createSeller(String userId, CreateSellerProfileRequest sellerProfileRequest) {
        try {
            User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(
                    () -> new ResourceNotFoundException("Invalid user")
            );
            if (user.getSellerProfile() != null) {
                throw new ResourceNotFoundException("Seller already exist");
            }
            SellerProfile sellerProfile = SellerProfile.builder()
                    .storeName(sellerProfileRequest.getStoreName())
                    .storeDescription(sellerProfileRequest.getStoreDescription())
                    .gst(sellerProfileRequest.getGst())
                    .user(user)
                    .build();
            user.assignSellerProfile(sellerProfile);

            // Store address must set the userId too
            Address storeAddress = modelMapper.map(sellerProfileRequest.getStoreAddress(), Address.class);
            storeAddress.setUser(user);
            storeAddress.setAddressType(AddressType.STORE);

            user.addAddress(storeAddress);

            return modelMapper.map(sellerRepository.findById(UUID.fromString(userId)), SellerProfileResponse.class);
        } catch (Exception e) {
            throw new BadCredentialsException("Error : " + e.getMessage());
        }
    }

    @Override
    public boolean updateRoleFromUserToSeller(String sellerUserId) throws ResourceNotFoundException {
        User user = userRepository.findById(UUID.fromString(sellerUserId)).orElseThrow(
                () -> new ResourceNotFoundException("Invalid sellerUserId")
        );
        SellerProfile sellerProfile = user.getSellerProfile();
        if (sellerProfile.isVerified() && !user.getRoles().contains(Roles.SELLER)) {
            List<Roles> userRoles = user.getRoles();
            userRoles.add(Roles.SELLER);
            user.setRoles(userRoles);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public SellerProfileResponse getSellerProfile(String userId) {
        return null;
    }
}
