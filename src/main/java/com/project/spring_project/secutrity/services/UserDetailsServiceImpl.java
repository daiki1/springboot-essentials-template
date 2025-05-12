package com.project.spring_project.secutrity.services;


import com.project.spring_project.entity.user.User;
import com.project.spring_project.repository.user.UserRepository;
import com.project.spring_project.util.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final LocalizationService localizationService;

    /**
     * Loads user details by username.
     *
     * @param username the username of the user
     * @return a CustomUserDetails object containing user details
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(localizationService.get("user.name.not.found", username)));
        return new CustomUserDetails(user);
    }
}