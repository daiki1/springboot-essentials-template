package com.project.spring_project.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LocalizationService {
    @Autowired
    private MessageSource messageSource;

    /**
     * Retrieves a localized message based on the provided key and arguments.
     *
     * @param key  The message key to look up.
     * @param args Optional arguments to format the message.
     * @return The localized message, or the key itself if not found.
     */
    public String get(String key, Object... args) {
        try {
            return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            return key; // fallback to the raw key if not found
        }
    }

}
