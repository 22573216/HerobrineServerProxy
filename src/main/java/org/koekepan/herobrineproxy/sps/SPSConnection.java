package org.koekepan.herobrineproxy.sps;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.HerobrineProxyProtocol;
import org.koekepan.herobrineproxy.packet.EstablishConnectionPacket;
import org.koekepan.herobrineproxy.packet.PacketListener;
import org.koekepan.herobrineproxy.session.IProxySessionConstructor;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.herobrineproxy.session.ISession;
import org.koekepan.herobrineproxy.entity.*;

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityTeleportPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMovementPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnMobPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityDestroyPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;

import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.io.buffer.ByteBufferNetInput;
import com.github.steveice10.packetlib.io.buffer.ByteBufferNetOutput;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.packet.PacketProtocol;
import com.google.gson.Gson;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SPSConnection implements ISPSConnection {
		
	int SPSPort;
	String SPSHost;
	private Socket socket;
	int connectionID;
	private SPSProxyProtocol protocol;
	
	private Map<String, ISession> listeners = new HashMap<String, ISession>();
	private Map<Integer, Entity> entities = new HashMap<Integer, Entity>();
	
	private IProxySessionConstructor sessionConstructor;
	
	public SPSConnection(String SPSHost, int SPSPort) {
		this.SPSHost = SPSHost;
		this.SPSPort = SPSPort;
		this.protocol = new SPSProxyProtocol();
	}
	
	
	public SPSConnection(String SPSHost, int SPSPort, IProxySessionConstructor sessionConstructor) {
		this(SPSHost, SPSPort);
		this.sessionConstructor = sessionConstructor;
	}


	private boolean initializeConnection() {
		String URL = "http://"+this.SPSHost+":"+this.SPSPort;
		ConsoleIO.println(URL);
		boolean result = false;
		try {
			this.socket = IO.socket(URL);
			result = true;
		} catch (URISyntaxException e) {
				e.printStackTrace();
		}
		return result;
	}

	public void initialiseListeners() {
		socket.on("ID", new Emitter.Listener() {
			@Override
			public void call(Object... data) {
				receiveConnectionID((int) data[0]);
			}
		});

		socket.on("publication", new Emitter.Listener() {
			@Override
			public void call(Object... data) {
				SPSPacket packet = receivePublication(data);
				String username = packet.username;
				int x = packet.x;
				int y = packet.y;
				int radius = packet.radius;
				
				if (packet.packet instanceof EstablishConnectionPacket) {
					EstablishConnectionPacket loginPacket = (EstablishConnectionPacket)packet.packet;
					//LoginStartPacket loginPacket = (LoginStartPacket)packet.packet;
					username = loginPacket.getUsername();
					if (loginPacket.establishConnection()) {
						ConsoleIO.println("SPSConnection::publication Must establish new connection for session <"+username+">");
						IProxySessionNew proxySession = sessionConstructor.createProxySession(username);
						String host = proxySession.getServerHost();
						int port = proxySession.getServerPort();
						proxySession.connect(host, port);					
					} else {
						ConsoleIO.println("SPSConnection::publication Must disconnect session of user <"+username+">");
						IProxySessionNew proxySession = sessionConstructor.getProxySession(username);
						if (proxySession != null) {
							proxySession.disconnect();
						} else {
							ConsoleIO.println("SPSConnection::publication => Received a packet for an unknown session <"+username+">");
						}
					}
				} else if (listeners.containsKey(username)) {					
					//listeners.get(username).packetReceived(packet.packet);
//					ConsoleIO.println("SPSConnection::publication => Sending packet <"+packet.packet.getClass().getSimpleName()+"> for player <"+username+"> at <"+x+":"+y+":"+radius+">");
					
					listeners.get(username).sendPacket(packet.packet);
				} else {
					ConsoleIO.println("SPSConnection::publication => Received a packet for an unknown session <"+username+">");
				}
			}
		});
	}


	@Override
	public void connect() {
		if (initializeConnection()) {
			initialiseListeners();
			socket.connect();
		}
	}

	
	@Override
	public void disconnect() {
		socket.disconnect();
	}

	
	private Packet retrievePacket(String publication) {
		Gson gson = new Gson();
		Packet packet = null;
		try {
			//ConsoleIO.println(data[0].toString());
			byte[] payload = gson.fromJson(publication, byte[].class);
			packet = this.bytesToPacket(payload);
		} catch (Throwable e) {
			ConsoleIO.println(e.toString());
		}
		//ConsoleIO.println("SPSConnection::retrievePacket => Retrieved packet <"+packet.getClass().getSimpleName()+">");
		return packet;
	}
	
	
	@Override
	public SPSPacket receivePublication(Object... data) {
		int connectionID = (int)data[0];
		String username = (String)data[1];
		int x = (int)data[2];
		int y = (int)data[3];
		int radius = (int)data[4];
		String publication = data[5].toString();
		String channel = (String)data[6];
		Packet packet = retrievePacket(publication);
		SPSPacket spsPacket = new SPSPacket(packet, username, x, y, radius, channel);
		return spsPacket;
	}

	
	@Override
	public void receiveConnectionID(int connectionID) {
		this.connectionID = connectionID;
		ConsoleIO.println("Received connectionID: <"+connectionID+">");
	}
	
	
	@Override
	public void publish(SPSPacket packet) { 
		ConsoleIO.println(packet.packet.getClass().getSimpleName());
		
		if (packet.packet instanceof ServerSpawnMobPacket) {
			ConsoleIO.println("SPAWN MOB");
			
			double x = ((ServerSpawnMobPacket) packet.packet).getX();
			double z = ((ServerSpawnMobPacket) packet.packet).getZ();
			int id = ((ServerSpawnMobPacket) packet.packet).getEntityId();
			
			if (!entities.containsKey(id)) {
				Entity entity = new Entity(id, x, z);
				entities.put(id, entity);
			}
		}
		if (packet.packet instanceof ServerSpawnGlobalEntityPacket) {
			ConsoleIO.println("SPAWN GLOBAL MOB");
			
			double x = ((ServerSpawnGlobalEntityPacket) packet.packet).getX();
			double z = ((ServerSpawnGlobalEntityPacket) packet.packet).getZ();
			int id = ((ServerSpawnGlobalEntityPacket) packet.packet).getEntityId();
			
			if (!entities.containsKey(id)) {
				Entity entity = new Entity(id, x, z);
				entities.put(id, entity);
			}
		}
		
		if (packet.packet instanceof ServerEntityDestroyPacket) {
			ConsoleIO.println("DESTROY ENTITY");
			int ids[] = ((ServerEntityDestroyPacket) packet.packet).getEntityIds();
			for (int id : ids) {
				if (entities.containsKey(id)) {
					ConsoleIO.println("REMOVING ENTITY FROM MAP");
					entities.remove(id);
				}
			}
		}
		
		if (packet.packet instanceof ServerEntityMovementPacket) {
//			ConsoleIO.println(packet.packet.getClass().getSimpleName());
			double moveX = ((ServerEntityMovementPacket) packet.packet).getMovementX();
			double moveZ = ((ServerEntityMovementPacket) packet.packet).getMovementZ();
			int entityId = ((ServerEntityMovementPacket) packet.packet).getEntityId();
			if (entities.containsKey(entityId)) {
				Entity entity = entities.get(entityId);
				entity.move(moveX, moveZ);
			}
		}
		if (packet.packet instanceof ServerEntityTeleportPacket) {
			double x = ((ServerEntityTeleportPacket) packet.packet).getX();
			double z = ((ServerEntityTeleportPacket) packet.packet).getZ();
			int entityId = ((ServerEntityTeleportPacket) packet.packet).getEntityId();
			if (entities.containsKey(entityId)) {
				Entity entity = entities.get(entityId);
				entity.setX(x);
				entity.setZ(z);
			}
		}
		//convert to JSON
		Gson gson = new Gson();
		byte[] payload = this.packetToBytes(packet.packet);
		String json = gson.toJson(payload);
//		ConsoleIO.println("Connection <"+connectionID+"> sent packet <"+packet.packet.getClass().getSimpleName()+"> on channel <"+packet.channel+">");
		
//		HANDLE PUBLISHING OF SPS PACKETS
		int entityId = getEntityId(packet);
		if (entityId != -1) {
			publishEntityPacket(entityId, packet, json);
		} else if (packet.packet instanceof ServerVehicleMovePacket) {
			double x = ((ServerVehicleMovePacket) packet.packet).getX();
			double z = ((ServerVehicleMovePacket) packet.packet).getZ();
			socket.emit("publish", connectionID, packet.username, (int) x, (int) z, 5, json, packet.channel);
		} else {
			socket.emit("publish", connectionID, packet.username, 0, 0, 0, json, packet.channel);
		}
		
	}


	@Override
	public void subscribe(String channel) {
		socket.emit("subscribe", channel);
		// TODO Auto-generated method stub

	}

	@Override
	public void unsubscribed(String channel) {
		// TODO Auto-generated method stub
	}
	
	private int getEntityId(SPSPacket packet) {		
		if (packet.packet instanceof ServerEntityMovementPacket) {
			return ((ServerEntityMovementPacket) packet.packet).getEntityId();
		} else if (packet.packet instanceof ServerEntityAnimationPacket) {
			return ((ServerEntityAnimationPacket) packet.packet).getEntityId();
		} else if (packet.packet instanceof ServerEntityHeadLookPacket) {
			return ((ServerEntityHeadLookPacket) packet.packet).getEntityId();
		} else if (packet.packet instanceof ServerEntityVelocityPacket) {
			return ((ServerEntityVelocityPacket) packet.packet).getEntityId();
		} else if (packet.packet instanceof ServerEntityMetadataPacket) {
			return ((ServerEntityMetadataPacket) packet.packet).getEntityId();	
		} else if (packet.packet instanceof ServerEntityAttachPacket) {
			return ((ServerEntityAttachPacket) packet.packet).getEntityId();
		} else if (packet.packet instanceof ServerEntityEffectPacket) {
			return ((ServerEntityEffectPacket) packet.packet).getEntityId();
		} else if (packet.packet instanceof ServerEntityEquipmentPacket) {
			return ((ServerEntityEquipmentPacket) packet.packet).getEntityId();
		} else if (packet.packet instanceof ServerEntityPropertiesPacket) {
			return ((ServerEntityPropertiesPacket) packet.packet).getEntityId();
		} else if (packet.packet instanceof ServerEntityRemoveEffectPacket) {
			return ((ServerEntityRemoveEffectPacket) packet.packet).getEntityId();
		} else if (packet.packet instanceof ServerEntitySetPassengersPacket) {
			return ((ServerEntitySetPassengersPacket) packet.packet).getEntityId();
		} else if (packet.packet instanceof ServerEntityTeleportPacket) {
			return ((ServerEntityTeleportPacket) packet.packet).getEntityId();
		}  else {
			return -1;
		}
	}
	
	private void publishEntityPacket(int entityId, SPSPacket packet, String payload) {
		if (entities.containsKey(entityId)) {
			Entity entity = entities.get(entityId);
			double x = entity.getX();
			double z = entity.getZ();
			socket.emit("publish", connectionID, packet.username, (int) x, (int) z, 5, payload, packet.channel);
		} else {
			socket.emit("publish", connectionID, packet.username, 0, 0, 0, payload, packet.channel);
		}
	}
		
	private byte[] packetToBytes(Packet packet) {
		ByteBuffer buffer = ByteBuffer.allocate(75000);
		ByteBufferNetOutput output = new ByteBufferNetOutput(buffer);
		
		int packetId = protocol.getOutgoingId(packet.getClass());
		try {
			protocol.getPacketHeader().writePacketId(output, packetId);
			packet.write(output);
		} catch (Exception e) {
			ConsoleIO.println("Exception: "+e.toString());
		}
		byte[] payload = new byte[buffer.position()];
		buffer.flip();
		buffer.get(payload);
		return payload;
	}
	
	
	private Packet bytesToPacket(byte[] payload) {
		ByteBuffer buffer = ByteBuffer.wrap(payload);
		ByteBufferNetInput input = new ByteBufferNetInput(buffer);
		Packet packet = null;
		try {
			int packetId = protocol.getPacketHeader().readPacketId(input);
		//	ConsoleIO.println("SPSConnection::byteToPacket => Protocol status <"+protocol.getSubProtocol().toString()+">");
			packet = protocol.createIncomingPacket(packetId);
			packet.read(input);
			
		} catch (Exception e) {
			ConsoleIO.println("Exception: "+e.toString());
		}
		return packet;
	}


	@Override
	public void addListener(ISession listener) {
		String username = listener.getUsername();
		listeners.put(username, listener);
	}
	
	
	@Override
	public void removeListener(ISession listener) {
		String username = listener.getUsername();
		listeners.remove(username);
	}


	@Override
	public String getHost() {
		return this.SPSHost;
	}


	@Override
	public int getPort() {
		return this.SPSPort;
	}
}
