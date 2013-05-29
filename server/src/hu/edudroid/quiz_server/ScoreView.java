package hu.edudroid.quiz_server;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

public class ScoreView extends JPanel{

	private static final long serialVersionUID = 4018448472765372198L;
	
	JLabel question;
	JList<String> answerList;
	JList<String> clients;
	private DefaultListModel<String> clientListModel;
	private DefaultListModel<String> answerListModel;


	public ScoreView() {
	}

	public void updateUI(QuizGame model, QuizQuestion quizQuestion, List<QuizPlayer> players) {
		
	}
}