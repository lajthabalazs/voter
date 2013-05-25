package hu.edudroid.udp_discovery;

import hu.edudroid.udp_multicast.MulticastGroupClient;
import hu.edudroid.udp_multicast.UdpMulticastListener;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * @author lajthabalazs
 *
 */
public class DiscoveryAgent implements UdpMulticastListener{

	private static final int DISCOVERY_PORT = 34212;
	private static final String DISCOVERY_ADDRESS = "224.0.6.148"; // Unassigned block: 224.0.6.144-224.0.6.255
	private static final Integer TRIES = 20;
	private static final String SEARCH_PREFIX = "SEARCH ";
	private static final String RESPONSE_PREFIX = "RESPONSE ";
	private static final long DISCOVERY_PERIOD = 1000;
	private static final int MAX_DATA_LENGTH = 256;
	private static DiscoveryAgent agent;
	
	private static DiscoveryAgent getDiscoveryAgent() throws IOException {
		if (agent == null) {
			agent = new DiscoveryAgent();
		}
		return agent;
	}
	
	private List<String> services = new ArrayList<String>();
	private HashMap<String, List<DiscoveryListener>> listeners = new HashMap<String, List<DiscoveryListener>>(); // Stores listeners registered for services
	private HashMap<String, Integer> triesLeft = new HashMap<String, Integer>(); // Stores recent tries for discovery
	private Timer timer = new Timer();
	private MulticastGroupClient client;
	
	
	private DiscoveryAgent () throws IOException {
		client = new MulticastGroupClient(DISCOVERY_PORT, DISCOVERY_ADDRESS, MAX_DATA_LENGTH);
		// Starts discovery
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				discover();
			}
		}, DISCOVERY_PERIOD, DISCOVERY_PERIOD);
	}
	
	private void find(String service) throws DiscoveryException {
		if (!isValidServiceName(service)) {
			throw new DiscoveryException("Invalid service name " + service);
		}
		synchronized (triesLeft) {
			Integer tries = triesLeft.get(service);
			if (tries == null || tries <= 0) {
				List<DiscoveryListener> listenerList = listeners.get(service);
				if (listenerList != null) {
					for (DiscoveryListener listener : listenerList){
						listener.discoveryStarted(service);
					}
				}
			}
			triesLeft.put(service, TRIES);		
		}
	}
	
	private boolean isValidServiceName(String service) {
		return ((!service.contains(" "))&&(service.length() < 10));
	}

	private void removeListener(String service, DiscoveryListener listener) {
		synchronized (listeners) {
			List<DiscoveryListener> listenerList = agent.listeners.get(service);
			if (listenerList == null) {
				return;
			}
			listenerList.remove(listener);
			if (listenerList.size() == 0) {
				listeners.remove(service);
			}
		}
	}
	
	private void registerListener(String service, DiscoveryListener listener) {
		synchronized (listeners) {
			List<DiscoveryListener> listenerList = listeners.get(service);
			if (listenerList == null) {
				listenerList = new ArrayList<DiscoveryListener>();
				listeners.put(service, listenerList);
			}
			listenerList.add(listener);
		}
	}
	
	private void discover() {
		synchronized (triesLeft) {
			Set<String> timedOutDiscoveries = new HashSet<String>();
			for (String service :triesLeft.keySet()) {
				int tries = triesLeft.get(service);
				try {
					// Sends discovery message
					client.sendMulticast(SEARCH_PREFIX + service);
					synchronized (listeners) {
						List<DiscoveryListener> listenerList = listeners.get(service);
						if (listenerList != null) {
							for (DiscoveryListener listener : listenerList){
								listener.discoveryMessageSent(service);
							}
						}					
					}
				} catch (IOException e) {
					e.printStackTrace();
					synchronized (listeners) {
						List<DiscoveryListener> listenerList = listeners.get(service);
						if (listenerList != null) {
							for (DiscoveryListener listener : listenerList){
								listener.discoveryError(service);
							}
						}
					}
				}
				tries --;
				if (tries <= 0) {
					timedOutDiscoveries.add(service);
				} else {
					triesLeft.put(service, tries);
				}
			}
			for(String service : timedOutDiscoveries) {
				triesLeft.remove(service);
				List<DiscoveryListener> listenerList = listeners.get(service);
				if (listenerList != null) {
					for (DiscoveryListener listener : listenerList){
						listener.discoveryTimedOut(service);
					}
				}
			}
		}
	}
	
	private void promote(String service) throws DiscoveryException {
		if (!isValidServiceName(service)) {
			throw new DiscoveryException("Invalid service name " + service);
		}
		services.add(service);
	}

	private void stopPromotion(String service) throws DiscoveryException {
		if (!isValidServiceName(service)) {
			throw new DiscoveryException("Invalid service name " + service);
		}
		services.remove(service);
	}

	@Override
	public void messageReceived(InetAddress sender, int senderPort, String message) {
		if (message.startsWith(SEARCH_PREFIX)) {
			String service = message.substring(SEARCH_PREFIX.length());
			if (services.contains(service)) {
				try {
					client.sendDirect(sender, senderPort, RESPONSE_PREFIX + service);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (message.startsWith(RESPONSE_PREFIX)) {
			String service = message.substring(RESPONSE_PREFIX.length());
			synchronized (triesLeft) {
				if (triesLeft.containsKey(service)) {
					triesLeft.remove(service);
				}
				synchronized (listeners) {
					List<DiscoveryListener> listenerList = listeners.get(service);
					for (DiscoveryListener listener : listenerList) {
						listener.discoverySuccess(service, sender, senderPort);
					}
				}
			}
		}
	}
	
	/**
	 * Start the promotion of a service
	 * @param service The name of the service
	 * @throws DiscoveryException
	 */
	public static void promoteService(String service) throws DiscoveryException {
		try {
			agent = getDiscoveryAgent();
			agent.promote(service);
 		} catch (IOException e) {
			e.printStackTrace();
			throw new DiscoveryException(e);
		}		
	}

	/**
	 * Stop the promotion of a service.
	 * @param service The name of the service
	 * @throws DiscoveryException
	 */
	public static void stopServicePromotion(String service) throws DiscoveryException {
		try {
			agent = getDiscoveryAgent();
			agent.stopPromotion(service);
 		} catch (IOException e) {
			e.printStackTrace();
			throw new DiscoveryException(e);
		}		
	}

	/**
	 * Start discovery. The listener will be notified of discovery events
	 * @param service The service to find
	 * @param listener The listener to notify in case of a successful discovery
	 * @throws DiscoveryException
	 */
	public static void register(String service, DiscoveryListener listener) throws DiscoveryException {
		try {
			agent = getDiscoveryAgent();
			agent.registerListener(service, listener);
			agent.find(service);
 		} catch (IOException e) {
			e.printStackTrace();
			throw new DiscoveryException(e);
		}
	}
	
	/**
	 * Unregister from discovery. This won't stop discovery.
	 * @param service The name of the service to unregister form
	 * @param listener The listener to unregister
	 * @throws DiscoveryException
	 */
	public static void unregister(String service, DiscoveryListener listener) throws DiscoveryException {
		try {
			agent = getDiscoveryAgent();
			agent.removeListener(service, listener);
		} catch (IOException e) {
			e.printStackTrace();
			throw new DiscoveryException(e);
		}
	}
}
