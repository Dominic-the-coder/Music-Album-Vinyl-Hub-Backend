package com.example.mini_project.services;

import com.example.mini_project.auth.JwtService;
import com.example.mini_project.models.LoginResponse;
import com.example.mini_project.models.User;
import com.example.mini_project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public LoginResponse login(
            String email,
            String password
    ) {

        User user =
                userRepository.findByEmail(email);

        if (user == null) {
            return null;
        }

        if (!passwordEncoder.matches(
                password,
                user.getPassword()
        )) {
            return null;
        }

        String token =
                jwtService.generateToken(
                        user.getId(),
                        user.getEmail()
                );

        return new LoginResponse(
                user.getId(),
                token
        );
    }
}