package hu.edudroid.udp_discovery;

import java.net.InetAddress;

public interface DiscoveryListener {
	void discoveryStarted(String service);
	void discoveryMessageSent(String service);
	void discoveryTimedOut(String service);
	void discoveryError(String service);
	void discoverySuccess(String service, InetAddress sender, int senderPort);
}