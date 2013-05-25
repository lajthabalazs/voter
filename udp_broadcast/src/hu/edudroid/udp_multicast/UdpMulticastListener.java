package hu.edudroid.udp_multicast;

import java.net.InetAddress;

public interface UdpMulticastListener {
	public void messageReceived(InetAddress sender, int senderPort, String message);
}
