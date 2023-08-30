package org.koekepan.herobrineproxy.packet.behaviours.server.world;

import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.herobrineproxy.sps.PlayerTracker;
import org.koekepan.herobrineproxy.sps.SPSPacket;

public class ServerChunkDataPacketBehaviour implements Behaviour<Packet> {
    private IProxySessionNew proxySession;
//    private IServerSession serverSession;

    private ServerChunkDataPacketBehaviour() {
    }

    public ServerChunkDataPacketBehaviour(IProxySessionNew proxySession) {
        this.proxySession = proxySession;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerChunkDataPacket serverChunkDataPacket = (ServerChunkDataPacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());        
//        int x = (int) serverChunkDataPacket.getColumn().getX()* 16;
////        int y = (int) serverChunkDataPacket.getColumn().getY();
//        int z = (int) serverChunkDataPacket.getColumn().getZ()* 16;

        int x = (int) PlayerTracker.getXByUsername(proxySession.getUsername());
        int z = (int) PlayerTracker.getZByUsername(proxySession.getUsername());

        SPSPacket spsPacket = new SPSPacket(packet, proxySession.getUsername(), x, z, 0);
//        SPSPacket spsPacket = new SPSPacket(packet, "clientBound", x, z, 500);
        proxySession.sendPacketToVASTnet_Client(spsPacket);
    }
}
