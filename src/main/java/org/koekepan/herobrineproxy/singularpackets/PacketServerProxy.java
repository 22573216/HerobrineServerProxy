package org.koekepan.herobrineproxy.singularpackets;

import com.github.steveice10.packetlib.Server;
import io.socket.client.Socket;
import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.session.IProxySessionConstructor;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.herobrineproxy.session.SPSToServerProxy;
import org.koekepan.herobrineproxy.sps.ISPSConnection;
import org.koekepan.herobrineproxy.sps.SPSConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// the main class for the proxy
// this class creates a proxy session for every client session that is connected

public class PacketServerProxy implements IProxySessionConstructor {


    //private ScheduledExecutorService serverPoll = Executors.newSingleThreadScheduledExecutor();

    private String VastHost = null;
    private int VastPort = 0;
    private String MinecraftServerHost = null;
    private int MinecraftServerPort = 0;
    private ISPSConnection spsConnection;

    private Server server = null;
    private Map<String, IProxySessionNew> sessions = new HashMap<String, IProxySessionNew>();

    public Socket socket;

    public PacketServerProxy(final String MinecraftServerHost, final int MinecraftServerPort, SPSConnection spsConnection) {
        this.MinecraftServerHost = MinecraftServerHost;
        this.MinecraftServerPort = MinecraftServerPort;

//        // setup new SPS connection to matcher on proxy startup
        this.spsConnection = spsConnection;
//        this.spsConnection.connect();

        PacketServer packetServer = new PacketServer(MinecraftServerHost, MinecraftServerPort, this);
        try {
            packetServer.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public Server getServer() {
        return server;
    }

    // returns whether the proxy server is currently listening for client connections
    public boolean isListening() {
        return server != null && server.isListening();
    }


    // initializes the proxy
    public void bind() {
        server.bind(true);
    }


    // closes the proxy
    public void close() {
        server.close(true);
    }


    public String getMinecraftServerHost() {
        return MinecraftServerHost;
    }


    public int getMinecraftServerPort() {
        return MinecraftServerPort;
    }


    public List<IProxySessionNew> getSessions() {
        return new ArrayList<IProxySessionNew>(sessions.values());
    }

    @Override
    public IProxySessionNew createProxySession(String username) {



        NmsToProxyProxy newProxySession = new NmsToProxyProxy(this.spsConnection, this.MinecraftServerHost, this.MinecraftServerPort);
        newProxySession.setUsername(username);
//        sessions.put(username, newProxySession);

        return newProxySession;
    }

    @Override
    public IProxySessionNew getProxySession(String username) {
        return sessions.get(username);
    }
}
