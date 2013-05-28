package hu.edudroid.quiz_engine;

import java.io.UnsupportedEncodingException;

import it.unipr.ce.dsg.s2p.message.BasicMessage;
import it.unipr.ce.dsg.s2p.org.json.JSONArray;
import it.unipr.ce.dsg.s2p.org.json.JSONException;
import it.unipr.ce.dsg.s2p.org.json.JSONObject;

public class QuestionMessage extends BasicMessage {
	public static final String QUESTION_MESSAGE_TYPE = "question";
	private static final String QUESTION_FIELD_NAME = "question";
	private static final String QUESTION_ID_FIELD_NAME = "questionId";
	private static final String ANSWER_FIELD_NAME = "answers";
	
	private String question;
	private String questionId;
	private String[] answers;
	private Base64Coder coder;	
	
	public QuestionMessage(String questionId, String question, String[] answers, Base64Coder coder) {
		this.coder = coder;
		this.questionId = coder.encode(questionId);
		this.question = coder.encode(question);
		this.answers = coder.encode(answers);
		super.setType(QUESTION_MESSAGE_TYPE);
	}
	
	public QuestionMessage(JSONObject jsonMsg, Base64Coder coder) {
		this.coder = coder;
		System.out.println("Question message received");
		try {
			questionId = jsonMsg.getString(QUESTION_ID_FIELD_NAME);
			try {
				questionId = new String(questionId.getBytes(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			System.out.println("Question id " + questionId);
			question = jsonMsg.getString(QUESTION_FIELD_NAME);
			try {
				question = new String(question.getBytes(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			System.out.println("Question " + question);
			JSONArray answerArray = jsonMsg.getJSONArray(ANSWER_FIELD_NAME);
			System.out.println("Parsing answers, " + answerArray.length() + " in total.");
			answers = new String[answerArray.length()];
			for (int i = 0; i < answerArray.length(); i++) {
				answers[i] = answerArray.getString(i);
				try {
					answers[i] = new String(answers[i].getBytes(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getQuestion() {
		return coder.decode(question);
	}

	public String[] getAnswers() {
		return coder.decode(answers);
	}

	public String getQuestionId() {
		return questionId;
	}
}
