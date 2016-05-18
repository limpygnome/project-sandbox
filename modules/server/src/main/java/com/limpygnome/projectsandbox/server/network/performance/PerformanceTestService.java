package com.limpygnome.projectsandbox.server.network.performance;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.network.Socket;
import com.limpygnome.projectsandbox.server.network.packet.PacketService;
import com.limpygnome.projectsandbox.server.service.EventLogicCycleService;
import com.limpygnome.projectsandbox.server.service.EventServerPostStartup;
import com.limpygnome.projectsandbox.shared.jpa.provider.GameProvider;
import com.limpygnome.projectsandbox.shared.model.GameSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * A service for performance testing the server-side, which injects mock/fake guest players into the game.
 */
@Service
public class PerformanceTestService implements EventServerPostStartup, EventLogicCycleService
{
    @Autowired
    private PacketService packetService;
    @Autowired
    private GameProvider gameProvider;

    private List<TestPlayer> players;
    private Random random;

    public PerformanceTestService()
    {
        players = new LinkedList<>();
        random = new Random(System.currentTimeMillis());
    }

    @Override
    public void eventServerPostStartup(Controller controller)
    {
        // Create fake players...
        players.add(new TestPlayer());
    }

    @Override
    public void logic()
    {
        // Run logic for fake players to send data etc...
        for (TestPlayer player : players)
        {
            playerLogic(player);
        }
    }

    public void playerLogic(TestPlayer testPlayer)
    {
        Socket socket = testPlayer.socket;

        if (socket == null)
        {
            // Setup fake socket
            socket = new MockSocket();

            // Join server...
            ByteBuffer packet = packetIdentity();
            packetService.handleInbound(socket, packet);
        }
        else
        {
            int randomAction = random.nextInt(8);
            ByteBuffer packet = null;

            switch (randomAction)
            {
                case 0:
                    // Go forwards
                    //packet = packetForwards();
                    break;
                case 1:
                    // Go backwards
                    //packet = packetBackwards();
                    break;
                case 2:
                    // Turn left
                    //packet = packetLeft();
                    break;
                case 3:
                    // Turn right
                    //packet = packetRight();
                    break;
                default:
                    // Do nothing....
                    packet = null;
                    break;
            }

            // Send data if any
            if (packet != null)
            {
                packetService.handleInbound(socket, packet);
            }
        }
    }

    private ByteBuffer packetIdentity()
    {
        // Setup fake game session
        GameSession gameSession = new GameSession("mock user");
        gameProvider.createGameSession(gameSession);

        // Convert token to raw
        byte[] token = gameSession.getToken().getBytes();

        // Build packet
        ByteBuffer buffer = ByteBuffer.allocate(2 + token.length);
        buffer.put((byte) 'P');
        buffer.put((byte) 'S');
        buffer.put(token);

        return buffer;
    }

    private ByteBuffer packetForwards()
    {
        return null;
    }

    private ByteBuffer packetBackwards()
    {
        return null;
    }

    private ByteBuffer packetLeft()
    {
        return null;
    }

    private ByteBuffer packetRight()
    {
        return null;
    }

    private ByteBuffer convert(byte[] data)
    {
        return ByteBuffer.wrap(data);
    }

}
