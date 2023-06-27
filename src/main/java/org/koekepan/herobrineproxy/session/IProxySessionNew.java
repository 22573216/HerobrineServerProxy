package org.koekepan.herobrineproxy.session;

import org.koekepan.herobrineproxy.sps.SPSPartition;

import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.herobrineproxy.sps.SPSPacket;

public interface IProxySessionNew {
	
	public String getUsername();
	public String getServerHost();
	public int getServerPort();
	
	public void setUsername(String username);
	public void setServerHost(String host);
	public void setServerPort(int port);
	public void setVoronoiPartition(SPSPartition partition);

	public void connect(String host, int port);
	public boolean isConnected();
	public void disconnect();
	public void disconnectFromServer();
	
	public void sendPacketToVASTnet_Client(SPSPacket spsPacket);
	public void sendPacketToServer(Packet packet);
	
	public void migrate(String host, int port);
	public void switchServer();
	void setPacketForwardingBehaviour();
	void registerForPluginChannels();
}
