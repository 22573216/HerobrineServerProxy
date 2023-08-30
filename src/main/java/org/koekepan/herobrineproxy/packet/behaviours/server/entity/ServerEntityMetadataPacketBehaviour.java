package org.koekepan.herobrineproxy.packet.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMetadataPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.herobrineproxy.sps.SPSPacket;

public class ServerEntityMetadataPacketBehaviour implements Behaviour<Packet> {
    private IProxySessionNew proxySession;
//    private IServerSession serverSession;

    private ServerEntityMetadataPacketBehaviour() {
    }

    public ServerEntityMetadataPacketBehaviour(IProxySessionNew proxySession) {
        this.proxySession = proxySession;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerEntityMetadataPacket serverEntityMetadataPacket = (ServerEntityMetadataPacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());        
//        int x = (int) serverEntityMetadataPacket.getPosition().getX();
//        int y = (int) serverEntityMetadataPacket.getPosition().getY();
//        int z = (int) serverEntityMetadataPacket.getPosition().getZ();
//        ConsoleIO.println(serverEntityMetadataPacket.getMetadata().);

        SPSPacket spsPacket = new SPSPacket(packet, proxySession.getUsername(), 500, 500, 2000);
        proxySession.sendPacketToVASTnet_Client(spsPacket);
    }
}
