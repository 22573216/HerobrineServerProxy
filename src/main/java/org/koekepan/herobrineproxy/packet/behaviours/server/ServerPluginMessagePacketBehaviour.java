package org.koekepan.herobrineproxy.packet.behaviours.server;

import com.github.steveice10.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import com.github.steveice10.packetlib.packet.Packet;
import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.herobrineproxy.sps.SPSPartition;
import org.koekepan.performance.LatencyData;;

public class ServerPluginMessagePacketBehaviour implements Behaviour<Packet> {
	private IProxySessionNew proxySession;
	
	@SuppressWarnings("unused")
	private ServerPluginMessagePacketBehaviour() {}
	
	
	public ServerPluginMessagePacketBehaviour(IProxySessionNew proxySession) {
		this.proxySession = proxySession;
	}

	
	@Override
	public void process(Packet packet) {
		ServerPluginMessagePacket pluginMessagePacket = (ServerPluginMessagePacket)packet;
//		ConsoleIO.println("ServerPluginMessagePacketBehaviour::process => PluginMessagePacket for channel <"+pluginMessagePacket.getChannel()+">");
		if (pluginMessagePacket.getChannel().equals("Koekepan|migrate")) {
//			byte[] payload = pluginMessagePacket.getData();
//			String hostname = this.readStringFromData(payload);
//			ConsoleIO.println("ServerPluginMessagePacketBehaviour::process => Received a migration message for client <"+proxySession.getUsername()+"> to migrate to server <"+hostname+">");
//			proxySession.migrate(hostname, proxySession.getServerPort()); 
		} else if (pluginMessagePacket.getChannel().equals("Koekepan|kick")) {
//				byte[] payload = pluginMessagePacket.getData();
//				String reason = this.readStringFromData(payload);
//				ConsoleIO.println("ServerPluginMessagePacketBehaviour::process => Received a kick message for client <"+proxySession.getUsername()+"> with reason <"+reason+">");
//				if (proxySession.isConnected()) {
//					proxySession.disconnect();
//				}
		} else if (pluginMessagePacket.getChannel().equals("Koekepan|latency")) {
			byte[] payload = pluginMessagePacket.getData();
			this.echoLatencyPacket(proxySession, "Koekepan|latency", payload);
			//System.out.println("ServerPluginMessagePacketBehaviour::process => Received latency measurement packet for client <"+client.getName()+">");	
		} else if (pluginMessagePacket.getChannel().equals("Koekepan|partition")) {
			ConsoleIO.println("ServerPluginMessagePacketBehaviour::process => Received a partition message for client <"+proxySession.getUsername()+">");	
			try {

				byte[] payload = pluginMessagePacket.getData();
				ByteArrayInputStream bis = new ByteArrayInputStream(payload);
				DataInputStream in = new DataInputStream(bis);
			
				// Read location data
				double x = in.readDouble();
				double y = in.readDouble();
				double z = in.readDouble();
				float pitch = in.readFloat();
				float yaw = in.readFloat();
			
				// Read volume data
				int length = in.readInt();

				double[] xPoints = new double[length];
				double[] yPoints = new double[length];
				for (int i = 0; i < length; i++) {
					xPoints[i] = in.readDouble();
					yPoints[i] = in.readDouble();
				}
				
				// Read world UID data
				Long MSB = in.readLong();
				Long LSB = in.readLong();
				UUID worldUID = new UUID(MSB, LSB);
				
				ConsoleIO.println("ServerPluginMessagePacketBehaviour::process => <x,y,z,pitch,yaw> = <"+x+","+y+","+z+","+pitch+","+yaw+">");

				String returnValue = "";			
				for (int i = 0; i < length; i++) {
					returnValue += "("+xPoints[i]+","+yPoints[i]+")\n";
				}	
				ConsoleIO.println("ServerPluginMessagePacketBehaviour::process => Volume: "+returnValue);
				
				ConsoleIO.println("ServerPluginMessagePacketBehaviour::process => World UUID = <"+worldUID+">");

				SPSPartition partition = new SPSPartition(xPoints, yPoints);
				proxySession.setVoronoiPartition(partition);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
	}
	
	/**
     * Reads a compressed int from the buffer. To do so it maximally reads 5 byte-sized chunks whose most significant
     * bit dictates whether another byte should be read.
     */
    private int readVarIntFromBuffer(ByteBuf buffer)
    {
        int var1 = 0;
        int var2 = 0;
        byte var3;

        do
        {
            var3 = buffer.readByte();
            var1 |= (var3 & 127) << var2++ * 7;

            if (var2 > 5)
            {
                throw new RuntimeException("VarInt too big");
            }
        }
        while ((var3 & 128) == 128);

        return var1;
    }
    	
	
	private String readStringFromData(byte[] data) {
		ByteBuf buffer = Unpooled.wrappedBuffer(data);

        int length = this.readVarIntFromBuffer(buffer);
		byte[] readBytes = new byte[length];
    	
    	buffer.readBytes(readBytes);
		String message = new String(readBytes, Charsets.UTF_8);
        return message;
	}
	
	
	private void echoLatencyPacket(IProxySessionNew proxySession, String channel, byte[] payload) {
		long currentTime = System.currentTimeMillis();
		LatencyData data = new LatencyData();
		data.reconstructFromBytes(payload);
		if (data.clientSentTime == -1) {
			data.clientSentTime = currentTime;					
			ClientPluginMessagePacket echoPacket = new ClientPluginMessagePacket(channel, data.convertToBytes());
			proxySession.sendPacketToServer(echoPacket);
		} else {
			data.clientReceiveTime = currentTime;
			//log.info("HerobrineMigrationListener::echoLatencyPacket => CurrentTime <"+currentTime+"> Server <"+data.serverSentTime+","+data.serverReceiveTime+"> Serverside RTT <"+(data.serverReceiveTime - data.serverSentTime)+"> Client <"+data.clientSentTime+","+data.clientReceiveTime+"> Clientside Latency <"+(data.clientReceiveTime - data.clientSentTime)+">");	
		}		
	}
}
