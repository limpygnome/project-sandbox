package com.limpygnome.projectsandbox.website.config;

import com.limpygnome.projectsandbox.website.task.PurgeInactiveSessions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by limpygnome on 20/07/15.
 */
@Configuration
@EnableScheduling
public class TaskConfig
{

    @Bean
    public PurgeInactiveSessions purgeInactiveSessionsTask()
    {
        return new PurgeInactiveSessions();
    }

}
