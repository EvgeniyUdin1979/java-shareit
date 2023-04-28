package ru.practicum.shareit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class CustomLocaleMessenger {
    @Autowired
    private MessageSource messageSource;

    public String getMessage(String text) {
        Locale local = LocaleContextHolder.getLocale();
        return messageSource.getMessage(text,null,local);
    }
}
