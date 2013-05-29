package hu.edudroid.quiz_engine;

import it.unipr.ce.dsg.s2p.message.BasicMessage;
import it.unipr.ce.dsg.s2p.org.json.JSONException;
import it.unipr.ce.dsg.s2p.org.json.JSONObject;

public class TimeoutMessage extends BasicMessage {
	public static final String TIMEOUT_MESSAGE_TYPE = "timeout";
	private static final String QUESTION_ID_KEY = "questionId";
	
	private String questionId;


	public TimeoutMessage(String questionId, Base64Coder coder) {
		super();
		super.setType(TIMEOUT_MESSAGE_TYPE);
		this.questionId = coder.encode(questionId);
	}
	
	public TimeoutMessage(JSONObject jsonMsg, Base64Coder coder) {
		try {
			questionId = coder.decode(jsonMsg.getString(QUESTION_ID_KEY));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String getQuestionId() {
		return questionId;
	}
}