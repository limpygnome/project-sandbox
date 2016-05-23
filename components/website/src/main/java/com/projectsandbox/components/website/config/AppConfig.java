package com.projectsandbox.components.website.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Created by limpygnome on 20/07/15.
 */
@Configuration
@ComponentScan({
        "com.limpygnome.projectsandbox.shared.config",
        "com.limpygnome.projectsandbox.website"
})
@PropertySource("classpath:settings.properties")
public class AppConfig
{

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties()
    {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
