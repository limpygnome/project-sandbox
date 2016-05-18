package com.limpygnome.projectsandbox.server.network.performance;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.service.EventLogicCycleService;
import com.limpygnome.projectsandbox.server.service.EventServerPostStartup;
import org.springframework.stereotype.Service;

/**
 * Created by limpygnome on 18/05/16.
 */
@Service
public class PerformanceTestService implements EventServerPostStartup, EventLogicCycleService
{

    @Override
    public void eventServerPostStartup(Controller controller)
    {
        // Create fake players...
    }

    @Override
    public void logic()
    {
        // Run logic for fake players to send data etc...
    }

}
