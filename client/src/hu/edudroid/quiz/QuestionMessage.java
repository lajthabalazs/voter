package hu.edudroid.quiz;

import it.unipr.ce.dsg.s2p.message.BasicMessage;

public class QuestionMessage extends BasicMessage {
	public static final String QUESTION_MESSAGE_TYPE = "question";
	public static final String QUESTION_FIELD_NAME = "question";
	public static final String QUESTION_ID_FIELD_NAME = "questionId";
	public static final String ANSWER_FIELD_NAME = "answer";
	
	private String question;
	private String questionId;
	private String[] answers;
	
	public QuestionMessage(String questionId, String question, String[] answers) {
		super();
		this.questionId = questionId;
		this.question = question;
		this.answers = answers;
		super.setType(QUESTION_MESSAGE_TYPE);
	}
	
	public String getQuestion() {
		return question;
	}

	public String[] getAnswers() {
		return answers;
	}

	public String getQuestionId() {
		return questionId;
	}
}
