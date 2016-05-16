package com.limpygnome.projectsandbox.server.network.packet;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.service.EventLogicCycleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Simple manager used to collect total bytes going in and out of the server.
 */
@Service
public class PacketStatsManager implements EventLogicCycleService
{
    private final static Logger LOG = LogManager.getLogger(PacketStatsManager.class);

    @Autowired
    private Controller controller;

    /**
     * The rate at which to output network stats, in debug mode, to the logs.
     */
    private static final long STATS_INTERVAL_MS = 5000;

    private long lastStatsOutputTime;
    private long totalBytesIn;
    private long totalBytesOut;

    private long lastTotalBytesIn;
    private long lastTotalBytesOut;

    @Override
    public void logic()
    {
        // TODO: why not use sleep instead?

        // Check logging enabled
        if (LOG.isDebugEnabled())
        {
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastStatsOutputTime >= STATS_INTERVAL_MS)
            {
                // Capture snapshot of data in/out, so we don't block I/O doing logic...
                long totalBytesIn;
                long totalBytesOut;

                synchronized (this)
                {
                    totalBytesIn = this.totalBytesIn;
                    totalBytesOut = this.totalBytesOut;
                }

                // Compute total
                long totalBytes = totalBytesIn + totalBytesOut;

                // Compute speeds
                float timePeriod = (currentTime - lastStatsOutputTime) / 1000.0f;
                float totalBytesSecondIn = (totalBytesIn - lastTotalBytesIn) / timePeriod;
                float totalBytesSecondOut = (totalBytesOut - lastTotalBytesOut) / timePeriod;

                // Convert to friendly strings
                String totalIn = humanBytes(totalBytesIn);
                String totalInSecond = humanBytes((long) totalBytesSecondIn);
                String totalOut = humanBytes(totalBytesOut);
                String totalOutSecond = humanBytes((long) totalBytesSecondOut);
                String total = humanBytes(totalBytes);

                long totalPlayers = controller.playerService.getPlayers().size();

                LOG.debug("in: {} ({}s), out: {} ({}s), total: {}, plys: {}", totalIn, totalInSecond, totalOut, totalOutSecond, total, totalPlayers);

                // Save for next time
                lastTotalBytesIn = totalBytesIn;
                lastTotalBytesOut = totalBytesOut;

                // Update time at which stats begun...
                lastStatsOutputTime = currentTime;
            }
        }
    }

    private String humanBytes(long bytes)
    {
        // Use SI/metric standard...
        if (bytes < 1_000L)
        {
            return bytes + " B";
        }
        else if (bytes < 1_000_000L)
        {
            return ((float) bytes / 1_000.0f) + " kB";
        }
        else if (bytes < 1_000_000_000L)
        {
            return ((float) bytes / 1_000_000.0f) + " MB";
        }
        else
        {
            return ((float) bytes / 1_000_000_000.0f) + " GB";
        }
    }

    public synchronized void incrementIn(long bytes)
    {
        totalBytesIn += bytes;
    }

    public synchronized void incrementOut(long bytes)
    {
        totalBytesOut += bytes;
    }

}
