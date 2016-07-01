package com.projectsandbox.components.website.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({
        "com.projectsandbox.components.shared",
        "com.projectsandbox.components.website"
})
public class AppConfig
{

}
