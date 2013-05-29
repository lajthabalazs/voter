package hu.edudroid.quiz_engine;

import it.unipr.ce.dsg.s2p.message.BasicMessage;
import it.unipr.ce.dsg.s2p.org.json.JSONArray;
import it.unipr.ce.dsg.s2p.org.json.JSONException;
import it.unipr.ce.dsg.s2p.org.json.JSONObject;

public class QuestionMessage extends BasicMessage {
	public static final String QUESTION_MESSAGE_TYPE = "question";
	private static final String QUESTION_FIELD_NAME = "question";
	private static final String QUESTION_ID_FIELD_NAME = "questionId";
	private static final String ANSWER_FIELD_NAME = "answers";
	private static final String USABLE_DOUBLES_FIELD_NAME = "usableDoublesLeft";
	private static final String CAN_USE_DOUBLE_OR_NOTHING_FIELD_NAME = "canUseDoubleOrNothing";
	
	private String question;
	private String questionId;
	private String[] answers;
	private int usableDoublesLeft = 0;
	private boolean canUseDoubleOrNothing = false;
	
	public QuestionMessage(String questionId, String question, String[] answers, int usableDoublesLeft, boolean canUseDoubleOrNothing, Base64Coder coder) {
		this.questionId = coder.encode(questionId);
		this.question = coder.encode(question);
		this.answers = coder.encode(answers);
		this.usableDoublesLeft = usableDoublesLeft;
		this.canUseDoubleOrNothing = canUseDoubleOrNothing;
		super.setType(QUESTION_MESSAGE_TYPE);
	}
	
	public QuestionMessage(JSONObject jsonMsg, Base64Coder coder) {
		System.out.println("Question message received");
		try {
			questionId = coder.decode(jsonMsg.getString(QUESTION_ID_FIELD_NAME));
			question = coder.decode(jsonMsg.getString(QUESTION_FIELD_NAME));
			JSONArray answerArray = jsonMsg.getJSONArray(ANSWER_FIELD_NAME);
			answers = new String[answerArray.length()];
			for (int i = 0; i < answerArray.length(); i++) {
				answers[i] = coder.decode(answerArray.getString(i));
			}
			usableDoublesLeft = jsonMsg.getInt(USABLE_DOUBLES_FIELD_NAME);
			canUseDoubleOrNothing = jsonMsg.getBoolean(CAN_USE_DOUBLE_OR_NOTHING_FIELD_NAME);
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

	public int getUsableDoublesLeft() {
		return usableDoublesLeft;
	}

	public boolean getCanUseDoubleOrNothing() {
		return canUseDoubleOrNothing;
	}
}
