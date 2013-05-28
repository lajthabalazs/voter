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


	public AnswerMessage(String code, String questionId, String answerId, Base64Coder coder) {
		super();
		super.setType(ANSWER_MESSAGE_TYPE);
		this.code = coder.encode(code);
		this.answerId = coder.encode(answerId);
		this.questionId = coder.encode(questionId);
	}
	
	public AnswerMessage(JSONObject jsonMsg, Base64Coder coder) {
		try {
			code = coder.decode(jsonMsg.getString(CODE_KEY));
			answerId = coder.decode(jsonMsg.getString(ANSWER_ID_KEY));
			questionId = coder.decode(jsonMsg.getString(QUESTION_ID_KEY));
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
