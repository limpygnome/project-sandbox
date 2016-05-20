package com.limpygnome.projectsandbox.website.task;

import com.limpygnome.projectsandbox.shared.jpa.repository.GameRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import static com.limpygnome.projectsandbox.website.constant.SessionConstants.PURGE_SESSIONS_INTERVAL_MS;
import static com.limpygnome.projectsandbox.website.constant.SessionConstants.PURGE_SESSIONS_INITIAL_DELAY_MS;

/**
 * A scheduled task to purge inactive sessions.
 */
public class PurgeInactiveSessions
{
    private final static Logger LOG = LogManager.getLogger(PurgeInactiveSessions.class);

    @Autowired
    private GameRepository gameRepository;

    @Scheduled(initialDelay = PURGE_SESSIONS_INITIAL_DELAY_MS, fixedRate = PURGE_SESSIONS_INTERVAL_MS)
    public void purge()
    {
        LOG.debug("Purging inactive sessions...");

        // Remove inactive sessions
        if (!gameRepository.removeInactiveGuestGameSessions())
        {
            LOG.error("Failed to purge inactive sessions");
        }

        LOG.debug("Finished purging inactive sessions");
    }

}
