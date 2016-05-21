package com.limpygnome.projectsandbox.server.network.performance;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.network.packet.PacketService;
import com.limpygnome.projectsandbox.server.service.EventLogicCycleService;
import com.limpygnome.projectsandbox.server.service.EventServerPostStartup;
import com.limpygnome.projectsandbox.shared.jpa.repository.GameRepository;
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
    /*
        The number of fake player we want in the game.
     */
    private static final int MOCK_PLAYERS = 800;

    @Autowired
    private PacketService packetService;
    @Autowired
    private GameRepository gameRepository;

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
        for (int i = 0; i < MOCK_PLAYERS; i++)
        {
            players.add(new TestPlayer());
        }
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
        if (testPlayer.socket == null)
        {
            // Setup fake socket
            testPlayer.socket = new MockSocket();

            // Join server...
            ByteBuffer packet = packetIdentity();
            packetService.handleInbound(testPlayer.socket, packet);
        }
        else
        {
            int randomAction = random.nextInt(9);
            ByteBuffer packet = null;

            switch (randomAction)
            {
                case 0:
                case 1:
                case 2:
                case 3:
                    // Go forwards
                    packet = packetForwards();
                    break;
                case 4:
                    // Go backwards
                    packet = packetBackwards();
                    break;
                case 5:
                    // Turn left
                    packet = packetLeft();
                    break;
                case 6:
                    // Turn right
                    packet = packetRight();
                    break;
                default:
                    // Do nothing....
                    packet = null;
                    break;
            }

            // Send data if any
            if (packet != null)
            {
                packetService.handleInbound(testPlayer.socket, packet);
            }
        }
    }

    private ByteBuffer packetIdentity()
    {
        // Setup fake game session
        GameSession gameSession = new GameSession("mock user");
        gameRepository.createGameSession(gameSession);

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
        return packetMovement((short) 1);
    }

    private ByteBuffer packetBackwards()
    {
        return packetMovement((short) 4);
    }

    private ByteBuffer packetLeft()
    {
        return packetMovement((short) 2);
    }

    private ByteBuffer packetRight()
    {
        return packetMovement((short) 8);
    }

    private ByteBuffer packetMovement(short value)
    {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.put((byte) 'P');
        byteBuffer.put((byte) 'M');
        byteBuffer.putShort(value);
        return byteBuffer;
    }

    private ByteBuffer convert(byte[] data)
    {
        return ByteBuffer.wrap(data);
    }

}
