package org.koekepan.herobrineproxy.singularpackets;

import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.session.IProxySessionConstructor;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.herobrineproxy.sps.SPSConnection;

public class PacketServer {
    private final int port;
    private final String host;
    private final IProxySessionConstructor packetServerProxy;
    private SPSConnection spsConnection;

    public PacketServer(int port, SPSConnection spsConnection) {
        this.port = port;
        this.spsConnection = spsConnection;
        host = null;
        packetServerProxy = null;
    }

    public PacketServer(String host, int port, IProxySessionConstructor packetServerProxy) {
        this.host = host;
        this.port = port;
        this.packetServerProxy = packetServerProxy;
    }

    public void start() throws Exception {
//        TcpSessionFactory tcpSessionFactory = new TcpSessionFactory(null);

//		Client new_client = new Client("10.42.0.1", 6543, new HerobrineProxyProtocol(), tcpSessionFactory);

//		clientForwarder = new PacketForwarder(server);
//		client.addListener(clientForwarder);
//
//		serverForwarder = new PacketForwarder(client);
//		new_client.addListener(serverForwarder);

        ConsoleIO.println("Creating a fake player on server to server as proxy connection with server");
//        IProxySessionNew proxySession = this.spsConnection.getSessionConstructor().createProxySession("proxy listener");
//        PacketServerProxy packetServerProxy = new PacketServerProxy(  );

        IProxySessionNew packetServerproxySession = packetServerProxy.createProxySession("ProxyListener2");
        String host = packetServerproxySession.getServerHost();
        int port = packetServerproxySession.getServerPort();
        packetServerproxySession.connect(host, port);


    }
}
