package hu.edudroid.quiz;

import it.unipr.ce.dsg.s2p.message.BasicMessage;

public class PingMessage extends BasicMessage {
	public static final String PING_MESSAGE_TYPE = "ping";
	
	public PingMessage() {
		super();
		super.setType(PING_MESSAGE_TYPE);
	}
}
