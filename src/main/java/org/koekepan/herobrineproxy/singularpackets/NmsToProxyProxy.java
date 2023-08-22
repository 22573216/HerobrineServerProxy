package org.koekepan.herobrineproxy.singularpackets;

import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.packet.behaviours.ClientSessionPacketBehaviours;
import org.koekepan.herobrineproxy.packet.behaviours.ServerSessionPacketBehaviours;
import org.koekepan.herobrineproxy.session.*;
import org.koekepan.herobrineproxy.sps.ISPSConnection;
import org.koekepan.herobrineproxy.sps.SPSPacket;
import org.koekepan.herobrineproxy.sps.SPSPartition;

public class NmsToProxyProxy implements IProxySessionNew {

    ISession client_sps_Session;
    IServerSession serverSession;
    IServerSession newServerSession;
    ISPSConnection spsConnection;
    SPSPartition voronoiPartition;

    ClientSessionPacketBehaviours clientPacketBehaviours;
    ServerSessionPacketBehaviours serverPacketBehaviours;
    ServerSessionPacketBehaviours newServerPacketBehaviours;

    public NmsToProxyProxy(ISPSConnection spsConnection, String serverHost, int serverPort) {
        this.spsConnection = spsConnection;

        this.client_sps_Session = new SPSSession(spsConnection);

        this.serverSession = new PacketServerSession(serverHost, serverPort);


        this.clientPacketBehaviours = new ClientSessionPacketBehaviours(this);
        this.clientPacketBehaviours.registerDefaultBehaviours(client_sps_Session);  // Behaviours of packets to Server. should not exist since we don't send packets to server.
        this.client_sps_Session.setPacketBehaviours(clientPacketBehaviours);
    }

    public void setCompressionThreshold(int i){
        ( (PacketServerSession) this.serverSession).setCompressionThreshold(i);
    }

    @Override
    public void setVoronoiPartition(SPSPartition partition) { // Should not be needed
        this.voronoiPartition = partition;
        spsConnection.subscribeToPartition(partition);
    }

    @Override
    public String getUsername() {
        return client_sps_Session.getUsername();
    } // Should not be needed


    @Override
    public void setUsername(String username) { // Should not be needed
//        client_sps_Session.setUsername(username);
        serverSession.setUsername(username);
    }


    @Override
    public void sendPacketToVASTnet_Client(SPSPacket spsPacket) {

        //ConsoleIO.println("SPSToServerProxy::sendPacketToClient => Sending packet <"+packet.getClass().getSimpleName()+"> to client <"+clientSession.getHost()+":"+clientSession.getPort()+">");
//		ConsoleIO.println("SPSToServerProxy::sendPacketToClient=> Sending packet <"+packet.getClass().getSimpleName());
        client_sps_Session.sendSPSPacket(spsPacket);
    }


    @Override
    public void sendPacketToServer(Packet packet) { // Should not be needed
        ConsoleIO.println("SPSToServerProxy::sendPacketToServer => Sending packet <"+packet.getClass().getSimpleName()+"> to server <"+serverSession.getHost()+":"+serverSession.getPort()+">");
        serverSession.sendPacket(packet);
    }



    @Override
    public void setServerHost(String host) { // Should not be needed
        // TODO Auto-generated method stub
    }


    @Override
    public void setServerPort(int port) { // Should not be needed
        // TODO Auto-generated method stub
    }


    @Override
    public boolean isConnected() {
        return serverSession.isConnected();
    }


    @Override
    public void connect(String host, int port) {

        ConsoleIO.println("creating clientPacketBehaviours");
        this.clientPacketBehaviours.registerForwardingBehaviour();

//        this.spsConnection.addListener(this.serverSession);
        ConsoleIO.println("creating ServerSessionPacketBehaviours");
        this.serverPacketBehaviours = new ServerSessionPacketBehaviours(this, serverSession);
        ConsoleIO.println("ServerSessionPacketBehaviours 1");
        this.serverPacketBehaviours.registerForwardingBehaviour();
        ConsoleIO.println("ServerSessionPacketBehaviours 2");
        this.serverSession.setPacketBehaviours(serverPacketBehaviours); // Behaviours of packets sent to client. This should be as default

        ConsoleIO.println("connecting");
        serverSession.connect(); // Connect to the minecraft server.
    }


    @Override
    public void disconnect() {
        disconnectFromServer();
        //disconnectFromClient();
    }


    @Override
    public void disconnectFromServer() {
        if (isConnected() ) {
            serverSession.disconnect();
        }
    }


    private void disconnectFromClient() {
    }


    @Override
    public String getServerHost() {
        return serverSession.getHost();
    }


    @Override
    public int getServerPort() {
        return serverSession.getPort();
    }


    @Override
    public void migrate(String host, int port) {
    }


    @Override
    public void switchServer() {
    }


    @Override
    public void setPacketForwardingBehaviour() {
        this.serverPacketBehaviours.registerForwardingBehaviour();
    }


    @Override
    public void registerForPluginChannels() {
        this.serverSession.registerClientForChannels();
    }
}
