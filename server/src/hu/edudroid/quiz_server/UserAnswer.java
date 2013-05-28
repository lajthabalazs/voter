package hu.edudroid.quiz_server;

public class UserAnswer {
	private int answer;
	private boolean usedDouble;
	private boolean usedDoubleOrNothing;
	
	public UserAnswer(int answer, boolean usedDouble, boolean usedDoubleOrNothing) {
		this.answer = answer;
		this.usedDouble = usedDouble;
		this.usedDoubleOrNothing = usedDoubleOrNothing;
	}

	public int getAnswer() {
		return answer;
	}

	public boolean isUsedDouble() {
		return usedDouble;
	}

	public boolean isUsedDoubleOrNothing() {
		return usedDoubleOrNothing;
	}
}
