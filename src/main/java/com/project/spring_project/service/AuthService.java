package com.project.spring_project.service;

import com.project.spring_project.entity.User;
import com.project.spring_project.payload.request.AuthRequest;
import com.project.spring_project.payload.response.AuthResponse;
import com.project.spring_project.repository.UserRepository;
import com.project.spring_project.secutrity.jwt.JwtTokenProvider;
import com.project.spring_project.secutrity.services.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtService;
    private final UserRepository userRepository;

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        String token = jwtService.generateToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        user.setActiveToken(token);
        userRepository.save(user);

        return new AuthResponse(token);
    }
}