package org.koekepan.herobrineproxy.packet;

import org.koekepan.herobrineproxy.ConsoleIO;

import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;

import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

public class PacketAdapter extends SessionAdapter {

	private PacketListener listener;
	
	public PacketAdapter(PacketListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void packetReceived(PacketReceivedEvent event) {
		//ConsoleIO.println("PacketAdapter::packetReceived => Received packet <"+event.getPacket().getClass().getSimpleName()+"> from host <"+event.getSession().getHost()+":"+event.getSession().getPort()+">");
	//	ConsoleIO.println(event.getPacket().toString());
		listener.packetReceived(event.getPacket());
	}
	
	
	 @Override
	 public void disconnected(DisconnectedEvent event) {
		 Session session = event.getSession();
		 ConsoleIO.println("PacketAdapter::disconnected => Session has disconnected from host <"+session.getHost()+":"+session.getPort()+"> with reason "+event.getReason());
	 }
}
