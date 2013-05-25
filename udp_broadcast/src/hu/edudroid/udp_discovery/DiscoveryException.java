package hu.edudroid.udp_discovery;

public class DiscoveryException extends Exception {

	private static final long serialVersionUID = 1041917624292341949L;

	public DiscoveryException(Exception e) {
		super(e);
	}

	public DiscoveryException(String string) {
		super(string);
	}

}
