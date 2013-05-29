package hu.edudroid.quiz_server;

public class QuizAnswer {
	private String text;
	private int pointValue;
	
	public void setPointValue(int pointValue) {
		this.pointValue = pointValue;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getPointValue() {
		return pointValue;
	}
	
	public String getText() {
		return text;
	}
	
	@Override
	public String toString() {
		return "(" + pointValue + ") " + text;
	}
}
