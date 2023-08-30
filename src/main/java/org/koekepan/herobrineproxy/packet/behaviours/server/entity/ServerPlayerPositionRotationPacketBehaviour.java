package org.koekepan.herobrineproxy.packet.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.herobrineproxy.sps.PlayerTracker;
import org.koekepan.herobrineproxy.sps.SPSPacket;

public class ServerPlayerPositionRotationPacketBehaviour implements Behaviour<Packet> {
    private IProxySessionNew proxySession;
//    private IServerSession serverSession;

    private ServerPlayerPositionRotationPacketBehaviour() {
    }

    public ServerPlayerPositionRotationPacketBehaviour(IProxySessionNew proxySession) {
        this.proxySession = proxySession;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerPlayerPositionRotationPacket serverPlayerPositionRotationPacket = (ServerPlayerPositionRotationPacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());        
        double x = serverPlayerPositionRotationPacket.getX();
        double y = serverPlayerPositionRotationPacket.getY();
        double z = serverPlayerPositionRotationPacket.getZ();

        String username = proxySession.getUsername();

        PlayerTracker.setXByUsername(username, x);
        PlayerTracker.setYByUsername(username, y);
        PlayerTracker.setZByUsername(username, z);

        SPSPacket spsPacket = new SPSPacket(packet, username, (int) x, (int) z, 0); // Player specific
        proxySession.sendPacketToVASTnet_Client(spsPacket);
    }
}
