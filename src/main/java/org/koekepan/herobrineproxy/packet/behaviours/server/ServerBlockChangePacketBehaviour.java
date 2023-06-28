package org.koekepan.herobrineproxy.packet.behaviours.server;

import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockChangePacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.herobrineproxy.session.IServerSession;
import org.koekepan.herobrineproxy.sps.SPSPacket;

public class ServerBlockChangePacketBehaviour implements Behaviour<Packet> {
    private IProxySessionNew proxySession;
//    private IServerSession serverSession;

    private ServerBlockChangePacketBehaviour() {}
    public ServerBlockChangePacketBehaviour(IProxySessionNew proxySession) {
        this.proxySession = proxySession;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerBlockChangePacket serverBlockChangePacket = (ServerBlockChangePacket) packet;

        BlockChangeRecord blockRecord = serverBlockChangePacket.getRecord();
        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());

        SPSPacket spsPacket = new SPSPacket(packet, proxySession.getUsername(), blockRecord.getPosition().getX(), blockRecord.getPosition().getZ(), 0, "clientBound");
        proxySession.sendPacketToVASTnet_Client(spsPacket);
    }
}

