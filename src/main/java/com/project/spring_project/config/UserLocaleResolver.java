package com.project.spring_project.config;

import com.project.spring_project.entity.User;
import com.project.spring_project.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;
import java.util.Optional;

@Component
public class UserLocaleResolver extends AcceptHeaderLocaleResolver {
    @Autowired
    private UserRepository userRepository;

    @Value("${app.locale.default:en}")
    String defaultLang;

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        System.out.println("resolveLocale");
        /*String headerLang = request.getHeader("Accept-Language");
        if (headerLang == null || headerLang.isBlank()) {
            return Locale.forLanguageTag(defaultLang);
        }*/

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String username = auth.getName();
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                String lang = userOpt.get().getLanguage();
                return Locale.forLanguageTag(lang);
            }
        }
        return Locale.forLanguageTag(defaultLang);
    }
}
