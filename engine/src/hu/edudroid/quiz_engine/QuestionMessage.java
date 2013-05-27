package hu.edudroid.quiz_engine;

import it.unipr.ce.dsg.s2p.message.BasicMessage;
import it.unipr.ce.dsg.s2p.org.json.JSONArray;
import it.unipr.ce.dsg.s2p.org.json.JSONException;
import it.unipr.ce.dsg.s2p.org.json.JSONObject;

public class QuestionMessage extends BasicMessage {
	public static final String QUESTION_MESSAGE_TYPE = "question";
	private static final String QUESTION_FIELD_NAME = "question";
	private static final String QUESTION_ID_FIELD_NAME = "questionId";
	private static final String ANSWER_FIELD_NAME = "answer";
	
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
	
	public QuestionMessage(JSONObject jsonMsg) {
		try {
			questionId = jsonMsg.getString(QUESTION_ID_FIELD_NAME);
			question = jsonMsg.getString(QUESTION_FIELD_NAME);
			JSONArray answerArray = jsonMsg.getJSONArray(ANSWER_FIELD_NAME);
			answers = new String[answerArray.length()];
			for (int i = 0; i < answerArray.length(); i++) {
				answers[i] = answerArray.getString(i);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
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
