package hu.edudroid.udp_multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashSet;

public class MulticastGroupClient {
	
	private final MulticastSocket socket;
	private final SocketListener listener;
	private final InetAddress group;
	private final int port;
	private HashSet<UdpMulticastListener> listeners = new HashSet<UdpMulticastListener>();

	public MulticastGroupClient (int port, String address, int maxDataLength) throws IOException {
		this.port = port;
		socket = new MulticastSocket(port);
		group = InetAddress.getByName(address);
		socket.joinGroup(group);
		listener = new SocketListener(maxDataLength);
		new Thread(listener).start();
	}
	
	public void sendMulticast(String message) throws IOException {
		if (socket.isClosed()) {
			throw new IOException("Socket already closed.");
		}
	    DatagramPacket packet;
	    byte[] data = message.getBytes();
	    packet = new DatagramPacket(data, data.length, group, port);
	    socket.send(packet);
	}
	
	public void sendDirect(InetAddress destination, int destinationPort, String message) throws IOException {
	    DatagramPacket packet;
	    byte[] data = message.getBytes();
	    packet = new DatagramPacket(data, data.length, destination, destinationPort);
		socket.send(packet);
	}
	
	private class SocketListener implements Runnable {
		private int dataLength;
		private boolean running;

		public SocketListener(int maxDataLength) {
			this.dataLength = maxDataLength;
			running = true;
		}
		@Override
		public void run() {
			DatagramPacket packet = new DatagramPacket(new byte[dataLength], 0);
			while(running) {
				try {
					socket.receive(packet);
					String message = new String(packet.getData(), 0, packet.getLength(), "UTF8");
					for (UdpMulticastListener listener : listeners) {
						listener.messageReceived(packet.getAddress(), packet.getPort(), message);
					}
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
	}
	
	public void close() {
		listener.running = false;
		socket.close();
	}
	
	public void registerListener(UdpMulticastListener listener) {
		listeners .add(listener);
	}
	
	public void unregisterListener(UdpMulticastListener listener) {
		listeners.remove(listener);
	}
}