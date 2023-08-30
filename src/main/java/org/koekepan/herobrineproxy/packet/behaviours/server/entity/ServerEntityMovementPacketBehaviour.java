package org.koekepan.herobrineproxy.packet.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMovementPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.herobrineproxy.sps.EntityTracker;
import org.koekepan.herobrineproxy.sps.PlayerTracker;
import org.koekepan.herobrineproxy.sps.SPSPacket;

public class ServerEntityMovementPacketBehaviour implements Behaviour<Packet> {
    private IProxySessionNew proxySession;
//    private IServerSession serverSession;

    private ServerEntityMovementPacketBehaviour() {
    }

    public ServerEntityMovementPacketBehaviour(IProxySessionNew proxySession) {
        this.proxySession = proxySession;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerEntityMovementPacket serverEntityMovementPacket = (ServerEntityMovementPacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());        
        double deltaX = serverEntityMovementPacket.getMovementX();
        double deltaY = serverEntityMovementPacket.getMovementY();
        double deltaZ = serverEntityMovementPacket.getMovementZ();

        double x;
        double z;

        int entityId = serverEntityMovementPacket.getEntityId();

        if (EntityTracker.isEntity(entityId)) {
            EntityTracker.moveByEntityId(entityId, deltaX, deltaY, deltaZ);
            x = EntityTracker.getXByEntityId(entityId);
            z = EntityTracker.getZByEntityId(entityId);

        } else if (PlayerTracker.isPlayer(entityId)){
            PlayerTracker.moveByEntityId(entityId, deltaX, deltaY, deltaZ);
            x = PlayerTracker.getXByEntityId(entityId);
            z = PlayerTracker.getZByEntityId(entityId);

        } else {
            throw new RuntimeException("No entity found with Entity Id: " + entityId);
        }

        SPSPacket spsPacket = new SPSPacket(packet, "clientBound", (int) x, (int) z, 0);
//        SPSPacket spsPacket = new SPSPacket(packet, proxySession.getUsername(), (int) x, (int) z, 5);
        proxySession.sendPacketToVASTnet_Client(spsPacket);
    }
}
