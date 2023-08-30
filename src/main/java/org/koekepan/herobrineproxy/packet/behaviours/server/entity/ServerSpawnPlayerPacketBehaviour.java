package org.koekepan.herobrineproxy.packet.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.herobrineproxy.sps.PlayerTracker;
import org.koekepan.herobrineproxy.sps.SPSPacket;

import java.util.UUID;

public class ServerSpawnPlayerPacketBehaviour implements Behaviour<Packet> {
    private IProxySessionNew proxySession;
//    private IServerSession serverSession;

    private ServerSpawnPlayerPacketBehaviour() {
    }

    public ServerSpawnPlayerPacketBehaviour(IProxySessionNew proxySession) {
        this.proxySession = proxySession;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerSpawnPlayerPacket serverSpawnPlayerPacket = (ServerSpawnPlayerPacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());        
        double x = serverSpawnPlayerPacket.getX();
        double y = serverSpawnPlayerPacket.getY();
        double z = serverSpawnPlayerPacket.getZ();

        int entityId = serverSpawnPlayerPacket.getEntityId();
        UUID uuid = serverSpawnPlayerPacket.getUUID();

        try {
            PlayerTracker.setEntityId(entityId, uuid);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        SPSPacket spsPacket = new SPSPacket(packet, "clientBound", (int) x, (int) z, 0);
        proxySession.sendPacketToVASTnet_Client(spsPacket);
    }
}
