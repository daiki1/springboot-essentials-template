package com.project.spring_project.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Configuration
public class InternationalizationConfig {

    /**
     * Configures the message source for internationalization.
     * <p>
     * This method sets up a ResourceBundleMessageSource to load messages from the "messages" properties file.
     *
     * @return a MessageSource object configured for internationalization
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(Locale.ENGLISH);
        return messageSource;
    }

    /**
     * Configures the LocaleResolver for the application.
     * <p>
     * This method sets up a UserLocaleResolver to resolve the user's locale based on their preferences.
     *
     * @return a LocaleResolver object configured for user locale resolution
     */
    @Bean
    public LocaleResolver localeResolver() {
        return new UserLocaleResolver();
    }

    /**
     * Configures the LocalValidatorFactoryBean for validation.
     * <p>
     * This method sets up a LocalValidatorFactoryBean to use the message source for validation messages.
     *
     * @return a LocalValidatorFactoryBean object configured for validation
     */
    @Bean
    public LocalValidatorFactoryBean getValidator() {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.setValidationMessageSource(messageSource());
        return factory;
    }
}