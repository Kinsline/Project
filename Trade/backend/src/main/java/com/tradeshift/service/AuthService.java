package com.tradeshift.service;

import com.tradeshift.Repositories.UserRepository;
import com.tradeshift.entities.User;
import com.tradeshift.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

   
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

   
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

   
    public String register(User user) {
       
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default role if not provided
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_USER");
        }

        // Save user to DB
        userRepository.save(user);

        // Generate JWT token for the registered user
        return jwtUtil.generateToken(user.getEmail());
    }

    // ✅ Login existing user and return JWT token
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // Compare raw password with encoded password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Generate and return JWT
        return jwtUtil.generateToken(user.getEmail());
    }
}
