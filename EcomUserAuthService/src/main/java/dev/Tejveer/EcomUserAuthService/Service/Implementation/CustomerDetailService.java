package dev.Tejveer.EcomUserAuthService.Service.Implementation;

import dev.Tejveer.EcomUserAuthService.Entity.User;
import dev.Tejveer.EcomUserAuthService.Exception.ResourceNotFoundException;
import dev.Tejveer.EcomUserAuthService.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CustomerDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(()-> new UsernameNotFoundException("Invalid username"));
    }

    public User getUserById(UUID userId) throws ResourceNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User with given userId not found"));
    }
}
