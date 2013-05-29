package hu.edudroid.quiz_server;

import java.util.ArrayList;

public class QuizQuestion {
	
	public enum Type {
		QUIZ, VOTE;

		public static Type parse(String string) {
			if (string.equals("QUIZ")) {
				return QUIZ;
			} else {
				return VOTE;
			}
		}
	}
	
	private String questionId;
	private String text;
	private ArrayList<QuizAnswer> answers = new ArrayList<QuizAnswer>();
	private Type type;
	
	public QuizQuestion(String questionId, String text, Type type) {
		this.questionId = questionId;
		this.text = text;
		this.type = type;
	}
	
	public void addAnswer(QuizAnswer answer) {
		answers.add(answer);
	}
	
	public int getScore(int answer, ArrayList<Integer> allAnswers) {
		try {
			if (type == Type.QUIZ) {
				return answers.get(answer).getPointValue();
			} else {
				int[] popularity = new int[answers.size()];
				for (int i = 0; i < allAnswers.size(); i++) {
					popularity[allAnswers.get(i)]++;
				}
				int maxPopularity = 0;
				int bestAnswer = 0;
				for (int i = 0; i < popularity.length; i++) {
					if (popularity[i] > maxPopularity) {
						bestAnswer = i;
						maxPopularity = popularity[i];
					}
				}
				if (answer == bestAnswer) {
					// Best answer
					return answers.get(answer).getPointValue();
				} else if (popularity[answer] == maxPopularity) {
					// One of the best answers
					return answers.get(answer).getPointValue();
				} else {
					// Not the best answer
					return 0;
				}
			}
		} catch (Exception e) {
			return -1;
		}
	}
	
	public String getQuestionId() {
		return questionId;
	}
	
	public String getQuestionText() {
		return text;
	}

	public String[] getAnswerStrings() {
		String[] ret = new String[answers.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = answers.get(i).getText();
		}
		return ret;
	}
	
	@Override
	public String toString() {
		return questionId + "(" + type + ") : " + text;
	}
}