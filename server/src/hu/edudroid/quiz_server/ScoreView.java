package hu.edudroid.quiz_server;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
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
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		question = new JLabel("QUESTION");
		add(question);
		
		answerList = new JList<String>();
		answerListModel = new DefaultListModel<String>();
		answerList.setModel(answerListModel);
		add(answerList);
		
		clients = new JList<String>();
		clientListModel = new DefaultListModel<String>();
		clients.setModel(clientListModel);
		add(clients);
	}

	public void updateUI(QuizGame model, int roundIndex, QuizQuestion quizQuestion, List<QuizPlayer> players) {
		if (quizQuestion != null) {
			question.setText(quizQuestion.getQuestionText());
			answerListModel.clear();
			ArrayList<QuizAnswer> answers = quizQuestion.getAnswers();
			// Answers
			for (int i = 0; i < answers.size(); i++) {
				String answerString = answers.get(i).getText() + " (" + answers.get(i).getPointValue() + " points) :";
				for (QuizPlayer player : players) {
					UserAnswer answer = player.getAnswer(quizQuestion.getQuestionId());
					if ((answer != null) && (answer.getAnswer() == i)) {
						answerString = answerString + " " + player.getName();
						if (answer.isUsedDouble() || answer.isUsedDoubleOrNothing()) {
							answerString = answerString + " (";
							if (answer.isUsedDouble()) {
								answerString = answerString + "d";
							}
							if (answer.isUsedDouble() && answer.isUsedDoubleOrNothing()) {
								answerString = answerString + ",";
							}
							if (answer.isUsedDoubleOrNothing()) {
								answerString = answerString + "d/n";
							}
							answerString = answerString + " )";
						}
					}
				}
				answerListModel.addElement(answerString);
			}
			// Scores
			clientListModel.clear();
			for (QuizPlayer player : players) {
				int playerScore = model.getScore(roundIndex, player.getCode());
				String scoreLine = player.getName() + " " + playerScore + " points";
				clientListModel.addElement(scoreLine);
			}
		}
	}
}