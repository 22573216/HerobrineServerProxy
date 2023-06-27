package org.koekepan.herobrineproxy.packet;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.github.steveice10.packetlib.packet.Packet;

import org.koekepan.herobrineproxy.ConsoleIO;
//import org.apache.logging.log4j.LogManager;
import org.koekepan.herobrineproxy.behaviour.BehaviourHandler;
import org.koekepan.herobrineproxy.sps.SPSPacket;

public class PacketHandler implements Runnable, PacketListener {
	
	private IPacketSession packetSession;

	private Queue<Packet> incomingPackets = new ConcurrentLinkedQueue<Packet>();
	private Queue<PacketWrapper> outgoingPackets = new ConcurrentLinkedQueue<>();
	
	private BehaviourHandler<Packet> behaviours;

	public PacketHandler(BehaviourHandler<Packet> behaviours, IPacketSession packetSession) {
		this.behaviours = behaviours;
		this.packetSession = packetSession;
	}

	
	public void setPacketSession(IPacketSession packetSession) {
		this.packetSession = packetSession;
	}
	
	
	public Queue<Packet> getIncomingPackets() {
		return incomingPackets;
	}
	
	
//	public Queue<SPSPacket> getPackets() {
//		return outgoingPackets;
//	}
	
	public BehaviourHandler<Packet> getBehaviours() {
		return behaviours;
	}
	
	
	public void setBehaviours(BehaviourHandler<Packet> behaviours) {
		this.behaviours = behaviours;
		//behaviours.printBehaviours();
	}

	
	public void process(Packet packet) {
		this.behaviours.process(packet);
	}

	
	private void addPacketToIncomingQueue(Packet packet) {
		try {
		//	ConsoleIO.println("PacketHandler::addPacketToIncomingQueue => Attempting to add packet <"+packet.getClass().getSimpleName()+"> that has registered behaviour <"+behaviours.hasBehaviour(packet.getClass())+"> to queue");
			//behaviours.printBehaviours();
			if (behaviours.hasBehaviour(packet.getClass())) {
				//ConsoleIO.println("PacketHandler::addPacketToIncomingQueue => Has registered behaviour for packet <"+packet.getClass().getSimpleName()+">");
				incomingPackets.add(packet);
			}
		} catch (IllegalStateException e) {
			ConsoleIO.println("PacketHandler::addPacketToIncomingQueue => IllegalStateException!");
			ConsoleIO.println(e.getMessage());
			e.printStackTrace();
			ConsoleIO.println(packet.toString());
		}
	}
	
	
	private void addPacketToOutgoingQueue(SPSPacket spsPacket) {
		try {
		 	//ConsoleIO.println("PacketHandler::addPacketToOutgoingQueue => Adding packet <"+packet.getClass().getSimpleName()+"> to queue");
			//if (behaviours.hasBehaviour(packet.getClass())) {
			//ConsoleIO.println("PacketHandler::addPacketToQueue => Has registered behaviour for packet <"+packet.getClass().getSimpleName()+">");
//			outgoingPackets.add(spsPacket);
			outgoingPackets.add(new PacketWrapper(spsPacket));
			//}

		} catch (IllegalStateException e) {
			ConsoleIO.println("PacketHandler::addPacketToOutgoingQueue => IllegalStateException!");
			//log.error("PacketHandler::addPacketToQueue => IllegalStateException!", e);
			e.printStackTrace();
			ConsoleIO.println(spsPacket.packet.toString());
		}
		//outgoingPackets.add(packet);
	}
	

	@Override
	public void packetReceived(Packet packet) {
		addPacketToIncomingQueue(packet);
	}

	
	@Override
	public void sendPacket(Packet packet) {
//		this.addPacketToOutgoingQueue(packet);
		outgoingPackets.add(new PacketWrapper(packet));
	}

	public void sendSPSPacket(SPSPacket spsPacket) {
		this.addPacketToOutgoingQueue(spsPacket);
	}
	
	
	@Override
	public void run() {
		Packet packet = null;
		SPSPacket spsPacket = null;
		try {
			packet = incomingPackets.poll();
			if (packet != null) { // NOT OUTGOING PACKETS!
				ConsoleIO.println("PacketHandler::run => Processing packet <"+packet.getClass().getSimpleName()+">. Packets remaining in queue: "+incomingPackets.size());
				behaviours.process(packet);
			}
			
			
//			spsPacket = outgoingPackets.peek();
			PacketWrapper wrapper = outgoingPackets.peek();
			//ConsoleIO.println("PacketHandler::run => <"+outgoingPackets.size()+"> packets in outgoing queue");
			if (wrapper != null && packetSession != null) {
			//	ConsoleIO.println("PacketHandler::run => Sending outgoing packet  <"+packet.getClass().getSimpleName()+">");
				wrapper = outgoingPackets.poll();
				if (wrapper.getSPSPacket() != null) {
					// It's an SPSPacket
					spsPacket = wrapper.getSPSPacket();
					packetSession.sendSPS(spsPacket);
				} else if (wrapper.getPacket() != null) {
					// It's a normal Packet
					packet = wrapper.getPacket();
					packetSession.send(packet);
				}
			}			
		} catch (Exception e) {
			ConsoleIO.println("PacketHandler::run => Exception occurred while processing packet <"+packet.getClass().getSimpleName()+">");
			ConsoleIO.println(e.getMessage());
			e.printStackTrace();
			ConsoleIO.println(packet.toString());
		}
	}

}

class PacketWrapper {
	private SPSPacket spsPacket;
	private Packet packet;

	public PacketWrapper(SPSPacket spsPacket) {
		this.spsPacket = spsPacket;
	}

	public PacketWrapper(Packet packet) {
		this.packet = packet;
	}

	public SPSPacket getSPSPacket() {
		return spsPacket;
	}

	public Packet getPacket() {
		return packet;
	}
}
