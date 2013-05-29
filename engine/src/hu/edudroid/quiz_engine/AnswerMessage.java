package hu.edudroid.quiz_engine;

import it.unipr.ce.dsg.s2p.message.BasicMessage;
import it.unipr.ce.dsg.s2p.org.json.JSONException;
import it.unipr.ce.dsg.s2p.org.json.JSONObject;

public class AnswerMessage extends BasicMessage {
	public static final String ANSWER_MESSAGE_TYPE = "answer";
	private static final String CODE_KEY = "code";
	private static final String ANSWER_ID_KEY = "answerId";
	private static final String QUESTION_ID_KEY = "questionId";
	private static final String ASK_FOR_DOUBLE_KEY = "askForDouble";
	private static final String ASK_FOR_DOUBLE_OR_NOTHING_KEY = "askForDoubleOrNothing";
	
	private String answerId;
	private String code;
	private String questionId;
	private boolean askForDouble;
	private boolean askForDoubleOrNothing;	

	public AnswerMessage(String code, String questionId, String answerId, boolean askForDouble, boolean askForDoubleOrNothing, Base64Coder coder) {
		super();
		super.setType(ANSWER_MESSAGE_TYPE);
		this.code = coder.encode(code);
		this.answerId = coder.encode(answerId);
		this.questionId = coder.encode(questionId);
		this.askForDouble = askForDouble;
		this.askForDoubleOrNothing = askForDoubleOrNothing;
	}
	
	public AnswerMessage(JSONObject jsonMsg, Base64Coder coder) {
		try {
			code = coder.decode(jsonMsg.getString(CODE_KEY));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			answerId = coder.decode(jsonMsg.getString(ANSWER_ID_KEY));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			questionId = coder.decode(jsonMsg.getString(QUESTION_ID_KEY));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			askForDouble = jsonMsg.getBoolean(ASK_FOR_DOUBLE_KEY);
		} catch (JSONException e) {
			e.printStackTrace();
			askForDouble = false;
		}
		try {
			askForDoubleOrNothing = jsonMsg.getBoolean(ASK_FOR_DOUBLE_OR_NOTHING_KEY);
		} catch (JSONException e) {
			e.printStackTrace();
			askForDoubleOrNothing = false;
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

	public boolean getAskForDouble() {
		return askForDouble;
	}

	public boolean getAskForDoubleOrNothing() {
		return askForDoubleOrNothing;
	}
}
