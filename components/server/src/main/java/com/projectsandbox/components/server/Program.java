
package com.projectsandbox.components.server;

import com.projectsandbox.components.server.config.AppConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.management.ManagementFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * The entry point for starting the game server.
 */
public class Program
{
    private final static Logger LOG = LogManager.getLogger(Program.class);

    public static void main(String[] args) throws Exception
    {
        // Setup Spring context...
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);

        // Output process ID (PID) if debug mode...
        if (LOG.isDebugEnabled())
        {
            String pid = ManagementFactory.getRuntimeMXBean().getName();
            LOG.debug("Launching Project Sandbox server - pid: {}", pid);
        }

        // Fetch controller instance using Spring and start server
        Controller controller = applicationContext.getBean(Controller.class);
        controller.startAndJoin();

        // Dispose Spring context...
        ((ConfigurableApplicationContext) applicationContext).close();
    }

}
