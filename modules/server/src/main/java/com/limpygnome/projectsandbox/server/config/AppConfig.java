package com.limpygnome.projectsandbox.server.config;

import com.limpygnome.projectsandbox.server.world.map.repository.file.FileSystemMapBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Primary context configuration class for Spring.
 */
@Configuration
@ComponentScan({
        "com.limpygnome.projectsandbox.shared.config",
        "com.limpygnome.projectsandbox.server"
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
