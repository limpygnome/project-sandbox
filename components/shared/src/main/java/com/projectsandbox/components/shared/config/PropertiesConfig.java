package com.projectsandbox.components.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Created by limpygnome on 01/07/16.
 */
@Configuration
@PropertySource("classpath:settings.properties")
public class PropertiesConfig
{

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties()
    {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
