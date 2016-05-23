package com.projectsandbox.components.server.config;

import com.projectsandbox.components.server.world.map.repository.file.FileSystemMapBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private List<FileSystemMapBuilder> builders;

    @Bean(name = "fileSystemMapBuilders")
    public Map<String, FileSystemMapBuilder> fileMapBuilders()
    {
        Map<String, FileSystemMapBuilder> map = new HashMap<>();

        for ( FileSystemMapBuilder builder : builders)
        {
            map.put(builder.getBuilderName(), builder);
        }

        return map;
    }

}
