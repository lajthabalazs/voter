package hu.edudroid.quiz_server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

public class QuestionView extends JPanel {
	private static final long serialVersionUID = 6076606400333968482L;
	JLabel question;
	JList<String> answerList;
	JList<String> clients;
	JLabel remainingTimeField;
	private DefaultListModel<String> clientListModel;
	private DefaultListModel<String> answerListModel;

	public QuestionView() {
		super(new BorderLayout());
		question = new JLabel();
		question.setMaximumSize(new Dimension(600, 600));		
		answerList = new JList<String>();
		answerList.setCellRenderer(new MultilineLabelRenderer());
		clients = new JList<String>();
		clients.setBackground(Color.LIGHT_GRAY);
		question.setFont(new Font(Font.SERIF, Font.BOLD, 20));		
		remainingTimeField = new JLabel();
		remainingTimeField.setFont(new Font(Font.SERIF, Font.BOLD, 60));
		
		clientListModel = new DefaultListModel<String>();
		clients.setModel(clientListModel);

		answerListModel = new DefaultListModel<String>();
		answerList.setModel(answerListModel);

		add(question, BorderLayout.PAGE_START);
		add(answerList, BorderLayout.CENTER);
		add(clients, BorderLayout.LINE_END);
		add(remainingTimeField, BorderLayout.LINE_START);
	}
	
	public void updateUI(QuizQuestion quizQuestion, List<QuizPlayer> players, int remainingTime) {
		remainingTimeField.setText("" + remainingTime);
		if (question != null) {
			question.setText("<HTML>" + quizQuestion.getQuestionText() + "</HTML>");
			String[] answers = quizQuestion.getAnswerStrings();
			answerListModel.clear();
			for (int i = 0; i < answers.length; i++) {
				answerListModel.addElement("<HTML>" + answers[i] + "</HTML>");
			}
		} else {
			question.setText("Intermission");
			answerListModel.clear();
		}
		clientListModel.clear();
		String questionId = null;
		if (quizQuestion != null) {
			questionId = quizQuestion.getQuestionId();
		}
		for (QuizPlayer player : players) {
			String item = player.getName();
			if (questionId == null) { // nothing
			} else if (player.getAnswer(questionId) != null) {
				item = item + " ANSWERED";
			} else {
				item = item + " WAITING";
			}
			clientListModel.addElement(item);
		}
	}
	
}
