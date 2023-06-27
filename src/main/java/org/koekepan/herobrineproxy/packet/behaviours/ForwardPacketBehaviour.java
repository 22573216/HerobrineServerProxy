package org.koekepan.herobrineproxy.packet.behaviours;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.session.IProxySessionNew;

import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.herobrineproxy.sps.SPSPacket;

public class ForwardPacketBehaviour implements Behaviour<Packet>{

	private IProxySessionNew proxySession;
	private boolean toServer;
	
	@SuppressWarnings("unused")
	private ForwardPacketBehaviour() {}
	
	
	public ForwardPacketBehaviour(IProxySessionNew proxySession, boolean toServer) {
		this.proxySession = proxySession;
		this.toServer = toServer;
	}

	
	@Override
	public void process(Packet packet) {
		if (toServer) {
			proxySession.sendPacketToServer(packet);
		} else {
//			spsPacket = new SPSPacket(packet, )
			SPSPacket spsPacket = new SPSPacket(packet, this.proxySession.getUsername(), 500, 500, 2000, "clientBound");
			proxySession.sendPacketToVASTnet_Client(spsPacket);
		}
	}
}
