package com.projectsandbox.components.server.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Primary context configuration class for Spring.
 */
@Configuration
@EnableAsync
@EnableScheduling
@ComponentScan({
        "com.projectsandbox.components.shared",
        "com.projectsandbox.components.server"
})
public class AppConfig
{

}
