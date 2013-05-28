package hu.edudroid.quiz_server;

import java.util.HashMap;

public class QuizPlayer {
	private String code;
	private String name;
	private int leftDoubles;
	private int leftDoubleOrNothings;
	private HashMap<String, UserAnswer> answers = new HashMap<String, UserAnswer>();

	/**
	 * @param code The code identifying the user
	 * @param name The displayed name of the user
	 * @param leftDoubles The number of double actions left. -1 for unlimited amount.
	 * @param leftDoubleOrNothings The number of double or nothing actions left. -1 for unlimited amount.
	 */
	public QuizPlayer(String code, String name, int leftDoubles, int leftDoubleOrNothings) {
		this.code = code;
		this.name = name;
		this.leftDoubles = leftDoubles;
		this.leftDoubleOrNothings = leftDoubleOrNothings;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public int getLeftDoubles() {
		return leftDoubles;
	}

	public int getLeftDoubleOrNothings() {
		return leftDoubleOrNothings;
	}
	
	public void addAnswer(String questionId, int answer, boolean usedDouble, boolean usedDoubleOrNothing) {
		if (leftDoubles == 0) {
			usedDouble = false;
		} else if (leftDoubles > 0){
			leftDoubles--;
		}
		if (leftDoubleOrNothings == 0) {
			usedDoubleOrNothing = false;
		} else if (leftDoubleOrNothings > 0){
			leftDoubleOrNothings--;
		}
		UserAnswer userAnswer = new UserAnswer(answer, usedDouble, usedDoubleOrNothing);
		answers.put(questionId, userAnswer);
	}
	
	public UserAnswer getAnswer(String questionId) {
		return answers.get(questionId);
	}
}