package org.koekepan.herobrineproxy.singularpackets;

import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.PacketSentEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.packet.PacketListener;

public class PacketAdapter_singularpackets extends SessionAdapter {

	private PacketListener listener;

	public PacketAdapter_singularpackets(PacketListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void packetReceived(PacketReceivedEvent event) {
		ConsoleIO.println("PacketAdapter_singularpackets::packetReceived => Received packet <"+event.getPacket().getClass().getSimpleName()+"> from host <"+event.getSession().getHost()+":"+event.getSession().getPort()+">");
	//	ConsoleIO.println(event.getPacket().toString());

//		ConsoleIO.println("Received Packet for " + this.listener.getClass() );
		listener.packetReceived(event.getPacket());
	}
	
	
	 @Override
	 public void disconnected(DisconnectedEvent event) {
		 Session session = event.getSession();
		 ConsoleIO.println("PacketAdapter_singularpackets::disconnected => Session has disconnected from host <"+session.getHost()+":"+session.getPort()+"> with reason "+event.getReason());
	 }

	 @Override
	 public void packetSent(PacketSentEvent event) {
		ConsoleIO.println("PacketAdapter_singularpackets::sentPacket: " + event.<Packet>getPacket().getClass().getSimpleName());
	 }

}
