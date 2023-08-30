package org.koekepan.herobrineproxy.packet.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnMobPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.herobrineproxy.sps.EntityTracker;
import org.koekepan.herobrineproxy.sps.PlayerTracker;
import org.koekepan.herobrineproxy.sps.SPSPacket;

import java.util.UUID;

public class ServerSpawnMobPacketBehaviour implements Behaviour<Packet> {
    private IProxySessionNew proxySession;
//    private IServerSession serverSession;

    private ServerSpawnMobPacketBehaviour() {
    }

    public ServerSpawnMobPacketBehaviour(IProxySessionNew proxySession) {
        this.proxySession = proxySession;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerSpawnMobPacket serverSpawnMobPacket = (ServerSpawnMobPacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());        
        double x = serverSpawnMobPacket.getX();
        double y = serverSpawnMobPacket.getY();
        double z = serverSpawnMobPacket.getZ();

        int entityId = serverSpawnMobPacket.getEntityId();
        UUID uuid = serverSpawnMobPacket.getUUID();

        if (!PlayerTracker.isPlayer(entityId)){
            new EntityTracker(x, y, z, entityId, uuid);
        }

        SPSPacket spsPacket = new SPSPacket(packet, "clientBound", (int) x, (int) z, 0);
        proxySession.sendPacketToVASTnet_Client(spsPacket);
    }
}
