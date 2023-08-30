package org.koekepan.herobrineproxy.packet.behaviours.login;

import com.github.steveice10.mc.protocol.packet.login.server.LoginSuccessPacket;
import com.github.steveice10.packetlib.packet.Packet;


import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.herobrineproxy.sps.PlayerTracker;
import org.koekepan.herobrineproxy.sps.SPSPacket;

import java.util.UUID;

public class ServerLoginSuccessPacketBehaviour implements Behaviour<Packet> {
		
	private IProxySessionNew proxySession;
	
	@SuppressWarnings("unused")
	private ServerLoginSuccessPacketBehaviour() {}
	
	
	public ServerLoginSuccessPacketBehaviour(IProxySessionNew proxySession) {
		this.proxySession = proxySession;
	}

	
	@Override
	public void process(Packet packet) {
		LoginSuccessPacket loginSuccessPacket = (LoginSuccessPacket)packet;	
		ConsoleIO.println("ServerLoginSuccessPacketBehaviour::process => Player \""+loginSuccessPacket.getProfile().getName()+"\" has successfully logged into the server");

		String username = loginSuccessPacket.getProfile().getName();
		UUID UUID = loginSuccessPacket.getProfile().getId();
		try {
			new PlayerTracker(username, UUID);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		SPSPacket spsPacket = new SPSPacket(packet, loginSuccessPacket.getProfile().getName(), 500, 500, 2000, "clientBound"); // TODO: should be published either globally or to a new channel just for login?
		proxySession.sendPacketToVASTnet_Client(spsPacket);
	}
}