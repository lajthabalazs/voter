package hu.edudroid.quiz;

import it.unipr.ce.dsg.s2p.message.BasicMessage;

public class AnswerMessage extends BasicMessage {
	public static final String MESSAGE_FIELD_NAME = "message";
	public static final String ANSWER_MESSAGE_TYPE = "answer";
	
	private String answer;
	private String code;
	private String questionId;

	public AnswerMessage(String code, String questionId, String answer) {
		super();
		super.setType(ANSWER_MESSAGE_TYPE);
		this.code = code;
		this.answer = answer;
		this.questionId = questionId;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getAnswer() {
		return answer;
	}
	
	public String getQuestionId() {
		return questionId;
	}
}
