package hu.edudroid.quiz_engine;

import it.unipr.ce.dsg.s2p.message.BasicMessage;

public class PingMessage extends BasicMessage {
	public static final String PING_MESSAGE_TYPE = "ping";
	public static final String CODE_KEY = "code";
	private String code;
	
	public PingMessage(String code) {
		super();
		this.code = code;
		super.setType(PING_MESSAGE_TYPE);
	}

	public String getCode() {
		return code;
	}
}
