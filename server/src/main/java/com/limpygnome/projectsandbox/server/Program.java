
package com.limpygnome.projectsandbox.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.management.ManagementFactory;

/**
 *
 * @author limpygnome
 */
public class Program
{
    private final static Logger LOG = LogManager.getLogger(Program.class);

    public static void main(String[] args) throws Exception
    {
        if (LOG.isDebugEnabled())
        {
            String pid = ManagementFactory.getRuntimeMXBean().getName();
            LOG.debug("Launching Project Sandbox server - pid: {}", pid);
        }

        Controller controller = new Controller();
        controller.start();
    }
}
