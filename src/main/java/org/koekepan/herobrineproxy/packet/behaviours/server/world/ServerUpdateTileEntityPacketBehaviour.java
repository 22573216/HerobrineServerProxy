package org.koekepan.herobrineproxy.packet.behaviours.server.world;

import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUpdateTileEntityPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.herobrineproxy.sps.SPSPacket;

public class ServerUpdateTileEntityPacketBehaviour implements Behaviour<Packet> {
    private IProxySessionNew proxySession;
//    private IServerSession serverSession;

    private ServerUpdateTileEntityPacketBehaviour() {
    }

    public ServerUpdateTileEntityPacketBehaviour(IProxySessionNew proxySession) {
        this.proxySession = proxySession;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerUpdateTileEntityPacket serverUpdateTileEntityPacket = (ServerUpdateTileEntityPacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());        
        int x = (int) serverUpdateTileEntityPacket.getPosition().getX();
        int y = (int) serverUpdateTileEntityPacket.getPosition().getY();
        int z = (int) serverUpdateTileEntityPacket.getPosition().getZ();

        SPSPacket spsPacket = new SPSPacket(packet, "clientBound", x, z, 0);
        proxySession.sendPacketToVASTnet_Client(spsPacket);
    }
}
