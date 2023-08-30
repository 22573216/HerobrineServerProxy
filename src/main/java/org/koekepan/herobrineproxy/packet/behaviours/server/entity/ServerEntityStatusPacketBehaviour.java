package org.koekepan.herobrineproxy.packet.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityStatusPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.herobrineproxy.sps.EntityTracker;
import org.koekepan.herobrineproxy.sps.PlayerTracker;
import org.koekepan.herobrineproxy.sps.SPSPacket;

public class ServerEntityStatusPacketBehaviour implements Behaviour<Packet> {
    private IProxySessionNew proxySession;
//    private IServerSession serverSession;

    private ServerEntityStatusPacketBehaviour() {
    }

    public ServerEntityStatusPacketBehaviour(IProxySessionNew proxySession) {
        this.proxySession = proxySession;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerEntityStatusPacket serverEntityStatusPacket = (ServerEntityStatusPacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());


        int entityId = serverEntityStatusPacket.getEntityId();
        double x;
        double z;

        if (PlayerTracker.isPlayer(entityId)){
            x = PlayerTracker.getXByEntityId(entityId);
            z = PlayerTracker.getZByEntityId(entityId);
        } else {
            x = EntityTracker.getXByEntityId(entityId);
            z = EntityTracker.getZByEntityId(entityId);
        }


        SPSPacket spsPacket = new SPSPacket(packet, "clientBound", (int) x, (int) z, 0);
//        SPSPacket spsPacket = new SPSPacket(packet, proxySession.getUsername(), (int) x, (int) z, 0);
        proxySession.sendPacketToVASTnet_Client(spsPacket);
    }
}
