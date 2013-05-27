package hu.edudroid.quiz_engine;

import it.unipr.ce.dsg.s2p.message.BasicMessage;
import it.unipr.ce.dsg.s2p.org.json.JSONException;
import it.unipr.ce.dsg.s2p.org.json.JSONObject;

public class AnswerMessage extends BasicMessage {
	public static final String ANSWER_MESSAGE_TYPE = "answer";
	private static final String CODE_KEY = "code";
	private static final String ANSWER_ID_KEY = "answerId";
	private static final String QUESTION_ID_KEY = "questionId";
	
	private String answerId;
	private String code;
	private String questionId;

	public AnswerMessage(String code, String questionId, String answerId) {
		super();
		super.setType(ANSWER_MESSAGE_TYPE);
		this.code = code;
		this.answerId = answerId;
		this.questionId = questionId;
	}
	
	public AnswerMessage(JSONObject jsonMsg) {
		try {
			code = jsonMsg.getString(CODE_KEY);
			answerId = jsonMsg.getString(ANSWER_ID_KEY);
			questionId = jsonMsg.getString(QUESTION_ID_KEY);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getCode() {
		return code;
	}
	
	public String getAnswerId() {
		return answerId;
	}
	
	public String getQuestionId() {
		return questionId;
	}
}
