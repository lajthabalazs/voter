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
		if (answers.size() > 0) {
			int[] scores = getScores(answers, allAnswers);
			return scores[answers.size() - 1];
		} else {
			return 0;
		}
	}

	/**
	 * Calculates score based on received answers for each question in the round.
	 * @param answers The selected user's answer for each question.
	 * @param allAnswers The other users' answers. First index is by round, and individual user's answers are in the inner array.
	 * @return Time series for each question in the round. Each item is an accumulated value: [0] score for the first answer [1] score for the first and second
	 * answer. And so on.
	 */
	public int[] getScores(ArrayList<UserAnswer> answers, ArrayList<ArrayList<UserAnswer>> allAnswers) {
		int[] scores = new int[answers.size()];
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
				if (i > 0) {
					scores[i] = scores[i-1] + questionScore;
				} else {
					scores[i] = questionScore;
				}
				if (answers.get(i).isUsedDoubleOrNothing()) {
					if (questionScore == 0) {
						scores[i] = 0;
					} else {
						scores[i] = scores[i] * 2;
					}
				}
			}
		}
		return scores;
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