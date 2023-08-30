package org.koekepan.herobrineproxy.packet.behaviours.server.world;

import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerMultiBlockChangePacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.herobrineproxy.sps.SPSPacket;

public class ServerMultiBlockChangePacketBehaviour implements Behaviour<Packet> {
    private IProxySessionNew proxySession;
//    private IServerSession serverSession;

    private ServerMultiBlockChangePacketBehaviour() {}
    public ServerMultiBlockChangePacketBehaviour(IProxySessionNew proxySession) {
        this.proxySession = proxySession;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerMultiBlockChangePacket serverMultiBlockChangePacket = (ServerMultiBlockChangePacket) packet;
        BlockChangeRecord[] blockRecords = serverMultiBlockChangePacket.getRecords();
//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());

        double[] sum = {0, 0, 0};  // {x, y, z}

        for (BlockChangeRecord record : blockRecords) {
            sum[0] += record.getPosition().getX();
            sum[1] += record.getPosition().getY();
            sum[2] += record.getPosition().getZ();
        }

        SPSPacket spsPacket = new SPSPacket(packet, "clientBound", (int) sum[0]/blockRecords.length, (int) sum[2]/blockRecords.length, 0);
        proxySession.sendPacketToVASTnet_Client(spsPacket);

    }
}

