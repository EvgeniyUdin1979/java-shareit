package ru.practicum.shareit.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(basePackageClasses = AppConfig.class)
@PropertySource(value = "classpath:messages.properties", encoding = "UTF-8")
public class AppConfig implements WebMvcConfigurer {


}
