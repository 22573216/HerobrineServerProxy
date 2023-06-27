package org.koekepan.herobrineproxy.session;


import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.packet.behaviours.ClientSessionPacketBehaviours;
import org.koekepan.herobrineproxy.packet.behaviours.ServerSessionPacketBehaviours;
import org.koekepan.herobrineproxy.sps.ISPSConnection;
import org.koekepan.herobrineproxy.sps.SPSPacket;
import org.koekepan.herobrineproxy.sps.SPSPartition;

import com.github.steveice10.packetlib.packet.Packet;

public class SPSToServerProxy implements IProxySessionNew {

	ISession client_sps_Session;
	IServerSession serverSession;
	IServerSession newServerSession;
	ISPSConnection spsConnection;
	SPSPartition voronoiPartition;
	
	ClientSessionPacketBehaviours clientPacketBehaviours;
	ServerSessionPacketBehaviours serverPacketBehaviours;
	ServerSessionPacketBehaviours newServerPacketBehaviours;
		
	public SPSToServerProxy(ISPSConnection spsConnection, String serverHost, int serverPort) {
		this.spsConnection = spsConnection;
		this.client_sps_Session = new SPSSession(spsConnection);
		this.serverSession = new ServerSession(serverHost, serverPort);
		this.clientPacketBehaviours = new ClientSessionPacketBehaviours(this);
		this.clientPacketBehaviours.registerDefaultBehaviours(client_sps_Session);
		this.client_sps_Session.setPacketBehaviours(clientPacketBehaviours);
	}
	
	@Override
	public void setVoronoiPartition(SPSPartition partition) {
		this.voronoiPartition = partition;
		spsConnection.subscribeToPartition(partition);
	}
		
	@Override
	public String getUsername() {
		return client_sps_Session.getUsername();
	}
	
	
	@Override
	public void setUsername(String username) {
		client_sps_Session.setUsername(username);
		serverSession.setUsername(username);
	}
	

	@Override
	public void sendPacketToVASTnet_Client(SPSPacket spsPacket) {
		
		//ConsoleIO.println("SPSToServerProxy::sendPacketToClient => Sending packet <"+packet.getClass().getSimpleName()+"> to client <"+clientSession.getHost()+":"+clientSession.getPort()+">");
//		ConsoleIO.println("SPSToServerProxy::sendPacketToClient=> Sending packet <"+packet.getClass().getSimpleName());
		client_sps_Session.sendSPSPacket(spsPacket);
	}

	
	@Override
	public void sendPacketToServer(Packet packet) {
		ConsoleIO.println("SPSToServerProxy::sendPacketToServer => Sending packet <"+packet.getClass().getSimpleName()+"> to server <"+serverSession.getHost()+":"+serverSession.getPort()+">");
		serverSession.sendPacket(packet);
	}
	
	

	@Override
	public void setServerHost(String host) {
		// TODO Auto-generated method stub
	}

	
	@Override
	public void setServerPort(int port) {
		// TODO Auto-generated method stub
	}

	
	@Override
	public boolean isConnected() {
		return serverSession.isConnected();
	}
	

	@Override
	public void connect(String host, int port) {
		this.clientPacketBehaviours.registerForwardingBehaviour();	
		this.spsConnection.addListener(this.serverSession);
		
		this.serverPacketBehaviours = new ServerSessionPacketBehaviours(this, serverSession);
		this.serverPacketBehaviours.registerForwardingBehaviour();
		this.serverSession.setPacketBehaviours(serverPacketBehaviours);	
		serverSession.connect();
	}
	
	
	@Override
	public void disconnect() {
		disconnectFromServer();
		//disconnectFromClient();
	}
	
	
	@Override
	public void disconnectFromServer() {
		if (isConnected() ) {
			serverSession.disconnect();
		}
	}
	
	
	private void disconnectFromClient() {
		if (client_sps_Session.isConnected()) {
			client_sps_Session.disconnect();
		}
	}


	@Override
	public String getServerHost() {
		return serverSession.getHost();
	}


	@Override
	public int getServerPort() {
		return serverSession.getPort();
	}


	@Override
	public void migrate(String host, int port) {
		ConsoleIO.println("SPSToServerProxy::migrate => Migrating player <"+getUsername()+"> to new server <"+host+":"+port+">");
		newServerSession = new ServerSession(getUsername(), host, port);
		this.newServerPacketBehaviours = new ServerSessionPacketBehaviours(this, newServerSession);
		this.newServerPacketBehaviours.registerMigrationBehaviour();
		this.newServerSession.setPacketBehaviours(newServerPacketBehaviours);	
		newServerSession.connect();
	}
	
	
	@Override
	public void switchServer() {
		ConsoleIO.println("SWITCHING SERVER SESSIONS");
		serverSession = newServerSession;
		spsConnection.addListener(serverSession);
		this.serverPacketBehaviours.clearBehaviours();
		this.serverPacketBehaviours = this.newServerPacketBehaviours;
		this.newServerPacketBehaviours = null;
	}

	
	@Override 
	public void setPacketForwardingBehaviour() {
		this.serverPacketBehaviours.registerForwardingBehaviour();
	}
	
	
	@Override 
	public void registerForPluginChannels() {
		this.serverSession.registerClientForChannels();
	}
}
