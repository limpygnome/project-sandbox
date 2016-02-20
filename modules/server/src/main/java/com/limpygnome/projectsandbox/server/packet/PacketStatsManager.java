package com.limpygnome.projectsandbox.server.packet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Simple manager used to collect total bytes going in and out of the server.
 */
public class PacketStatsManager
{
    private final static Logger LOG = LogManager.getLogger(PacketStatsManager.class);

    /**
     * The rate at which to output network stats, in debug mode, to the logs.
     */
    private static final long STATS_INTERVAL_MS = 5000;

    private long lastStatsOutput;
    private long totalBytesIn;
    private long totalBytesOut;

    public void logic()
    {
        // Check logging enabled
        if (LOG.isDebugEnabled())
        {
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastStatsOutput >= STATS_INTERVAL_MS)
            {
                // Update time at which stats begun...
                lastStatsOutput = currentTime;

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

                // Convert to friendly strings
                String totalIn = humanBytes(totalBytesIn);
                String totalOut = humanBytes(totalBytesOut);
                String total = humanBytes(totalBytes);

                LOG.debug("network statistics - in: {}, out: {}, total: {}", totalIn, totalOut, total);
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
