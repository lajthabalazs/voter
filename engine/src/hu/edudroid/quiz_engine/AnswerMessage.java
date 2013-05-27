package hu.edudroid.quiz_engine;

import it.unipr.ce.dsg.s2p.message.BasicMessage;

public class AnswerMessage extends BasicMessage {
	public static final String MESSAGE_FIELD_NAME = "message";
	public static final String ANSWER_MESSAGE_TYPE = "answer";
	
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
