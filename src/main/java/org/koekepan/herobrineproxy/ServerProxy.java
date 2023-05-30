package org.koekepan.herobrineproxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.koekepan.herobrineproxy.session.*;
import org.koekepan.herobrineproxy.sps.*;

import io.socket.client.*;

import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;

// the main class for the proxy
// this class creates a proxy session for every client session that is connected

public class ServerProxy implements IProxySessionConstructor{


	//private ScheduledExecutorService serverPoll = Executors.newSingleThreadScheduledExecutor();

	private String VastHost = null;
	private int VastPort = 0;
	private String MinecraftServerHost = null;
	private int MinecraftServerPort = 0;
	private ISPSConnection spsConnection;
	
	private Server server = null;
	private Map<String, IProxySessionNew> sessions = new HashMap<String, IProxySessionNew>();
	
	public Socket socket;
	
	public ServerProxy(final String VastHost, final int VastPort, final String MinecraftServerHost, final int MinecraftServerPort, final String ThisProxyHost, final int ThisProxyPort) {
		this.VastHost = VastHost;
		this.VastPort = VastPort;
		this.MinecraftServerHost = MinecraftServerHost;
		this.MinecraftServerPort = MinecraftServerPort;
		
		// setup new SPS connection to matcher on proxy startup
		this.spsConnection = new SPSConnection(this.VastHost, this.VastPort, this); //connects to vast
		this.spsConnection.connect();

		// setup proxy server and add listener to create and store/discard proxy sessions as clients connect/disconnect
		server = new Server(ThisProxyHost, ThisProxyPort, HerobrineProxyProtocol.class, new TcpSessionFactory());
		
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
		SPSToServerProxy newProxySession = new SPSToServerProxy(this.spsConnection, this.MinecraftServerHost, this.MinecraftServerPort);
		newProxySession.setUsername(username);
		sessions.put(username, newProxySession);
		return newProxySession;
	}

	@Override
	public IProxySessionNew getProxySession(String username) {
		return sessions.get(username);
	}	
}
