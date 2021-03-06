package hu.edudroid.quiz_engine;

import it.unipr.ce.dsg.s2p.message.BasicMessage;
import it.unipr.ce.dsg.s2p.org.json.JSONException;
import it.unipr.ce.dsg.s2p.org.json.JSONObject;

public class PingMessage extends BasicMessage {
	public static final String PING_MESSAGE_TYPE = "ping";
	private static final String CODE_KEY = "code";
	private String code;
	
	public PingMessage(String code, Base64Coder coder) {
		super();
		this.code = coder.encode(code);
		super.setType(PING_MESSAGE_TYPE);
	}

	public PingMessage(JSONObject jsonMsg, Base64Coder coder) {
		try {
			code = coder.decode(jsonMsg.getString(CODE_KEY));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getCode() {
		return code;
	}
}