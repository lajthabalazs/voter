package hu.edudroid.quiz_server;

public class QuizQuestion {
	public enum Type {
		QUIZ, VOTE
	}
	private QuizAnswer[] answers;
	private Type type;
}
