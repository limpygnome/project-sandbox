package com.limpygnome.projectsandbox.server.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Primary context configuration class for Spring.
 */
@Configuration
@ComponentScan({
        "com.limpygnome.projectsandbox.server"
})
public class AppConfig
{
}
