package org.koekepan.herobrineproxy.sps;

import com.github.steveice10.packetlib.packet.Packet;

public class SPSPacket {
	public String username;
	public int x;
	public int y;
	public int radius;
	public String channel;
	public Packet packet;


	
	public SPSPacket(Packet packet, String username, String channel) {
		this.username = username;
		this.channel = channel;
		this.packet = packet;
//		this.x = 3;
//		this.y = 0;
//		this.radius = 1;
	}
	
	
	public SPSPacket(Packet packet, String username, int x, int y, int radius, String channel) {
		this(packet, username, channel);
		this.x = x;
		this.y = y;
		this.radius = radius;
	}

	public SPSPacket(Packet packet, String channel, int x, int y, int radius) {
		this.packet = packet;
		this.channel = channel;
		this.username = "Herobrine";
		this.x = x;
		this.y = y;
		this.radius = radius;
	}
	
	
}
