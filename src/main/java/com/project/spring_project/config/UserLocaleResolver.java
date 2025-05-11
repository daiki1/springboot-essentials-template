package com.project.spring_project.config;

import com.project.spring_project.entity.User;
import com.project.spring_project.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    /**
     * Resolves the locale based on the user's authentication and preferences.
     * <p>
     * This method checks if the user is authenticated and retrieves their language preference.
     * If no preference is found, it falls back to the Accept-Language header or the default language.
     *
     * @param request the HttpServletRequest object
     * @return the resolved Locale object
     */
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        // Check if the user is authenticated and retrieve their language preference
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String username = auth.getName();
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent() && userOpt.get().getLanguage()!=null) {
                String lang = userOpt.get().getLanguage();
                return Locale.forLanguageTag(lang);
            }
        }

        // Fallback to the Accept-Language header if no user preference is found
        String headerLang = request.getHeader("Accept-Language");
        if (headerLang != null && !headerLang.isBlank()) {
            return Locale.forLanguageTag(headerLang);
        }

        // Fallback to the default language if no user preference or Accept-Language header is found
        return Locale.forLanguageTag(defaultLang);
    }
}
