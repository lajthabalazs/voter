package hu.edudroid.quiz_server;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class QuizLiveFrame extends JFrame implements QuizGameListener, ActionListener {
	private static final long serialVersionUID = -5423453185160139085L;
	private static final String ACTION_NEXT_ROUND = "Next round"; 
	private static final String ACTION_NEXT_QUESTION = "Next question"; 
	JButton nextQuestion;
	JButton nextRound;
	QuestionView questionView;
	ScoreView scoreView;
	private QuizServer server;
	private QuizGame model;
	private boolean answerTime = false;
	private int timeLeft;
	private Timer timer = new Timer();
	private CountDownTask task;
	
	public QuizLiveFrame(QuizServer server, QuizGame model) {
		this.server = server;
		this.model = model;
		model.registerListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		Container contentPane = getContentPane();
		
		nextQuestion = new JButton("Next question");
		nextQuestion.addActionListener(this);
		nextRound = new JButton("Next round");
		nextRound.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(nextQuestion, BorderLayout.CENTER);
		buttonPanel.add(nextRound, BorderLayout.LINE_END);
		
		contentPane.add(buttonPanel, BorderLayout.PAGE_END);
		questionView = new QuestionView();
		scoreView = new ScoreView();
		JPanel centerPanel = new JPanel(new FlowLayout());
		centerPanel.add(questionView);
		centerPanel.add(scoreView);
		contentPane.add(centerPanel, BorderLayout.CENTER);
		setVisible(true);
		updateUI();
		pack();
	}
	
	private void updateUI() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				nextRound.setEnabled(model.hasNextRound());
				nextQuestion.setEnabled(model.hasNextQuestion());
				QuizQuestion quizQuestion = model.getActualQuestion();
				List<QuizPlayer> players = model.getPlayers();
				if (answerTime) {
					System.out.println("Updating question view");
					scoreView.setVisible(false);
					questionView.setVisible(true);
					questionView.updateUI(quizQuestion, players, timeLeft);
				} else {					
					scoreView.setVisible(true);
					questionView.setVisible(false);
					scoreView.updateUI(model, model.getActualRoundIndex(), quizQuestion, players);
				}
			}
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getActionCommand().equals(ACTION_NEXT_ROUND)) {
			int actualRound = model.getActualRoundIndex();
			if (model.playRound(actualRound + 1)) {
				answerTime = true;
				server.sendQuestion();
				updateUI();
				startCountDown();
			} else {
				System.err.println("No next round.");
			}
		} else if (arg0.getActionCommand().equals(ACTION_NEXT_QUESTION)) {
			int actualQuestion = model.getActualQuestionIndex();
			if (model.playQuestion(actualQuestion + 1)) {
				answerTime = true;
				server.sendQuestion();
				updateUI();
				startCountDown();
			} else {
				System.err.println("No next question.");
			}
		}
	}

	private void startCountDown() {		
		if (task != null) {
			task.cancel();
		}
		task = new CountDownTask(10);
		updateUI();
		timer.schedule(task, 1000, 1000);
	}

	@Override
	public void modelChanged() {
		updateUI();
	}

	private class CountDownTask extends TimerTask {

		public CountDownTask(int totalTime) {
			timeLeft = totalTime;
		}

		@Override
		public void run() {
			timeLeft--;
			if (timeLeft <= 0) {
				timeLeft = 0;
				answerTime = false;
				server.sendEndOfQuestionTime();
				this.cancel();
			}
			updateUI();
		}
	}
}