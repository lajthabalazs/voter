package hu.edudroid.quiz_server;

import java.util.ArrayList;

public class QuizRound {
	private ArrayList<QuizQuestion> questions = new ArrayList<QuizQuestion>();
	
	public void addQuestion(QuizQuestion actualQuestion) {
		questions.add(actualQuestion);
	}

	public int getQuestionCount() {
		return questions.size();
	}
	
	/**
	 * Calculates score based on received answers
	 * @param answers The selected user's answer for each question.
	 * @param allAnswers The other users' answers. First index is by round, and individual user's answers are in the inner array.
	 * @return
	 */
	public int getScore(ArrayList<UserAnswer> answers, ArrayList<ArrayList<UserAnswer>> allAnswers) {
		int score = 0;
		for (int i = 0; i < answers.size(); i++) {
			if (answers.get(i) != null) {
				ArrayList<Integer> allAnswerIndexes = new ArrayList<Integer>();
				for (UserAnswer otherAnser : allAnswers.get(i)) {
					if (otherAnser != null) {
						allAnswerIndexes.add(otherAnser.getAnswer());
					}
				}
				int questionScore = questions.get(i).getScore(answers.get(i).getAnswer(), allAnswerIndexes);
				if (answers.get(i).isUsedDouble()) {
					questionScore = questionScore * 2;
				}
				score = score + questionScore;
				if (answers.get(i).isUsedDoubleOrNothing()) {
					if (questionScore == 0) {
						score = 0;
					} else {
						score = score * 2;
					}
				}
			}
		}
		return score;
	}

	public ArrayList<String> getQuestionIds() {
		ArrayList<String> roundsQuestions = new ArrayList<String>();
		for (QuizQuestion question : questions) {
			roundsQuestions.add(question.getQuestionId());
		}
		return roundsQuestions;
	}

	public QuizQuestion getQuestion(int index) {
		return questions.get(index);
	}	
}