package org.koekepan.herobrineproxy.packet;

import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.herobrineproxy.sps.SPSPacket;

public interface IPacketSession {
	public void send(Packet packet);

	public void sendSPS(SPSPacket spsPacket);
}
