package org.koekepan.herobrineproxy.packet;

import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.sps.SPSPacket;

public class PacketSession implements IPacketSession{
	
	public Session session;
	
	public PacketSession(Session client) {
		this.session = client;
	}
	
	
	public void send(Packet packet) {
		session.send(packet);
	}

	public void sendSPS(SPSPacket spsPacket) {
		ConsoleIO.println("I'm in Packetsession.java and not spsPacketSession.java?");
	}
}
