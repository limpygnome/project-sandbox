package com.limpygnome.projectsandbox.website.task;

import com.limpygnome.projectsandbox.shared.jpa.provider.GameProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

import static com.limpygnome.projectsandbox.website.constant.SessionConstants.PURGE_SESSIONS_INTERVAL_MS;
import static com.limpygnome.projectsandbox.website.constant.SessionConstants.PURGE_SESSIONS_INITIAL_DELAY_MS;

/**
 * A scheduled task to purge inactive sessions.
 */
public class PurgeInactiveSessions
{
    private final static Logger LOG = LogManager.getLogger(PurgeInactiveSessions.class);

    @Scheduled(initialDelay = PURGE_SESSIONS_INITIAL_DELAY_MS, fixedRate = PURGE_SESSIONS_INTERVAL_MS)
    public void purge()
    {
        LOG.debug("Purging inactive sessions...");

        // Setup DB connection
        GameProvider gameProvider = new GameProvider();

        gameProvider.begin();

        // Remove inactive sessions
        if (!gameProvider.removeInactiveGameSessions())
        {
            LOG.error("Failed to purge inactive sessions");
            gameProvider.rollback();
        }
        else
        {
            gameProvider.commit();
        }

        // Dispose DB connection
        gameProvider.close();

        LOG.debug("Finished purging inactive sessions");
    }

}
