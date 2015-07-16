package com.limpygnome.projectsandbox.website.tasks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by limpygnome on 20/04/2015.
 */
public class StatisticsTask
{
    private final static Logger LOG = LogManager.getLogger(StatisticsTask.class);

    public void compileStatistics()
    {
        LOG.info("Updating statistics...");

        LOG.info("Statistics updated.");
    }

}
