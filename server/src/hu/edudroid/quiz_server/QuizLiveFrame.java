package hu.edudroid.quiz_server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

public class QuizLiveFrame extends JFrame implements QuizGameListener, ActionListener {
	private static final long serialVersionUID = -5423453185160139085L;
	private static final String ACTION_NEXT_ROUND = "Next round"; 
	private static final String ACTION_NEXT_QUESTION = "Next question"; 
	// Shows actual question, active peers, and peers who sent in a response
	JLabel question;
	JList<String> answerList;
	JList<String> clients;
	JButton nextQuestion;
	JButton nextRound;
	JLabel remainingTimeField;
	private DefaultListModel<String> clientListModel;
	private DefaultListModel<String> answerListModel;
	private QuizServer server;
	private QuizGame model;
	
	public QuizLiveFrame(QuizServer server, QuizGame model) {
		this.server = server;
		this.model = model;
		model.registerListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		Container contentPane = getContentPane();
		
		question = new JLabel();
		question.setMaximumSize(new Dimension(600, 600));
		
		nextQuestion = new JButton("Next question");
		nextQuestion.addActionListener(this);
		nextRound = new JButton("Next round");
		nextRound.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(nextQuestion, BorderLayout.CENTER);
		buttonPanel.add(nextRound, BorderLayout.LINE_END);
		
		answerList = new JList<String>();
		answerList.setCellRenderer(new MultilineLabelRenderer());
		clients = new JList<String>();
		clients.setBackground(Color.LIGHT_GRAY);
		question.setFont(new Font(Font.SERIF, Font.BOLD, 20));
		
		clientListModel = new DefaultListModel<String>();
		clients.setModel(clientListModel);

		answerListModel = new DefaultListModel<String>();
		answerList.setModel(answerListModel);

		remainingTimeField = new JLabel();
		remainingTimeField.setFont(new Font(Font.SERIF, Font.BOLD, 60));
		
		contentPane.add(question, BorderLayout.PAGE_START);
		contentPane.add(answerList, BorderLayout.CENTER);
		contentPane.add(remainingTimeField, BorderLayout.LINE_START);
		contentPane.add(clients, BorderLayout.LINE_END);
		contentPane.add(buttonPanel, BorderLayout.PAGE_END);
		
		pack();
		setVisible(true);
		updateUI();
	}
	
	private void updateUI() {
		nextRound.setEnabled(model.hasNextRound());
		nextQuestion.setEnabled(model.hasNextQuestion());
		QuizQuestion quizQestion = model.getActualQuestion();
		if (question != null) {
			question.setText("<HTML>" + quizQestion.getQuestionText() + "</HTML>");
			String[] answers = quizQestion.getAnswerStrings();
			answerListModel.clear();
			for (int i = 0; i < answers.length; i++) {
				answerListModel.addElement("<HTML>" + answers[i] + "</HTML>");
			}
		} else {
			question.setText("Intermission");
			answerListModel.clear();
		}
		clientListModel.clear();
		QuizQuestion quizQuestion = model.getActualQuestion();
		String questionId = null;
		if (quizQuestion != null) {
			questionId = quizQuestion.getQuestionId();
		}
		List<QuizPlayer> players = model.getPlayers();
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
		pack();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getActionCommand().equals(ACTION_NEXT_ROUND)) {
			int actualRound = model.getActualRoundIndex();
			if (model.playRound(actualRound + 1)) {
				server.sendQuestion();
				updateUI();
				startCountDown();
			} else {
				System.err.println("No next round.");
			}
		} else if (arg0.getActionCommand().equals(ACTION_NEXT_QUESTION)) {
			int actualQuestion = model.getActualQuestionIndex();
			if (model.playQuestion(actualQuestion + 1)) {
				server.sendQuestion();
				updateUI();
				startCountDown();
			} else {
				System.err.println("No next question.");
			}
		}
	}

	private void startCountDown() {
		Timer timer = new Timer();
		timer.schedule(new CountDownTask(30), 1000, 1000);
	}

	@Override
	public void modelChanged() {
		updateUI();
	}

	private class CountDownTask extends TimerTask {
		private int timeLeft;

		public CountDownTask(int totalTime) {
			timeLeft = totalTime;
		}

		@Override
		public void run() {
			timeLeft--;
			remainingTimeField.setText("" + timeLeft);
			if (timeLeft <= 0) {
				remainingTimeField.setText("0");
				server.sendEndOfQuestionTime();
				this.cancel();
				updateUI();
			}
		}
	}
}