package com.limpygnome.projectsandbox.website.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import javax.servlet.ServletContext;
import java.io.File;

/**
 * Created by limpygnome on 20/07/15.
 */
@Configuration
@ComponentScan("com.limpygnome.projectsandbox.website")
@PropertySource("classpath:settings.properties")
public class AppConfig
{

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties()
    {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
