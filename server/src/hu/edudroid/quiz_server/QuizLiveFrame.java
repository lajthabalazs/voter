package hu.edudroid.quiz_server;

import hu.edudroid.quiz_engine.AnswerMessage;
import hu.edudroid.quiz_engine.PingMessage;
import hu.edudroid.quiz_engine.QuestionMessage;
import hu.edudroid.quiz_engine.QuizPeerListener;
import it.unipr.ce.dsg.s2p.sip.Address;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;

public class QuizLiveFrame extends JFrame implements QuizPeerListener, ActionListener {
	private static final long serialVersionUID = -5423453185160139085L;
	// Shows actual question, active peers, and peers who sent in a response
	JLabel question;
	JList<String> answerList;
	JList<String> clients;
	JButton nextQuestion;
	private DefaultListModel<String> clientListModel;
	private DefaultListModel<String> answerListModel;
	private QuizServer server;
	private QuizGame model;
	
	public QuizLiveFrame(QuizServer server, QuizGame model) {
		this.server = server;
		this.model = model;
		server.registerListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		Container contentPane = getContentPane();
		
		question = new JLabel();
		question.setMaximumSize(new Dimension(600, 600));
		nextQuestion = new JButton("Next");
		nextQuestion.addActionListener(this);
		answerList = new JList<String>();
		answerList.setCellRenderer(new MultilineLabelRenderer());
		clients = new JList<String>();
		clients.setBackground(Color.LIGHT_GRAY);
		question.setFont(new Font(Font.SERIF, Font.BOLD, 20));
		
		clientListModel = new DefaultListModel<String>();
		clients.setModel(clientListModel);

		answerListModel = new DefaultListModel<String>();
		answerList.setModel(answerListModel);

		contentPane.add(question, BorderLayout.PAGE_START);
		contentPane.add(answerList, BorderLayout.CENTER);
		contentPane.add(clients, BorderLayout.LINE_END);
		contentPane.add(nextQuestion, BorderLayout.PAGE_END);
		
		pack();
		setVisible(true);
	}
	
	private void updateUI() {
		nextQuestion.setEnabled(model.hasNextQuestion());
		QuizQuestion quizQestion = model.getActualQuestion();
		if (question != null) {
			question.setText("<HTML>" + quizQestion.getQuestionText() + "</HTML>");
			String[] answers = quizQestion.getAnswerStrings();
			answerListModel.clear();
			for (int i = 0; i < answers.length; i++) {
				answerListModel.addElement("<HTML>" + answers[i] + "</HTML>");
			}
		}
		updateClientList();
		pack();
	}
	
	private void updateClientList() {
		clientListModel.clear();
		QuizQuestion quizQuestion = model.getActualQuestion();
		if (quizQuestion != null) {
			String questionId = quizQuestion.getQuestionId();
			List<QuizPlayer> players = model.getPlayers();
			for (QuizPlayer player : players) {
				String item = player.getName();
				if (player.getAnswer(questionId) != null) {
					item = item + " ANSWERED";
				} else {
					item = item + " WAITING";
				}
				clientListModel.addElement(item);
			}
		} else {
		}
	}

	@Override public void messageSendingError(String sentMessage, Address destination, String messageType) {}
	@Override public void messageSendingSuccess(String sentMessage, Address destination, String messageType) {}

	@Override
	public void answerReceived(Address sender, AnswerMessage answer) {
		// TODO subscribe to model changes instead!
		updateClientList();
	}

	@Override
	public void questionReceived(Address sender, QuestionMessage question) {}

	@Override
	public void pingReceived(Address sender, PingMessage ping) {}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO ask question, next block, etc
		int actualRound = model.getActualRoundIndex();
		if (model.playRound(actualRound + 1)) {
			updateUI();
		} else {
			System.err.println("No next round.");
		}
		int actualQuestion = model.getActualQuestionIndex();
		if (model.playQuestion(actualQuestion + 1)) {
			updateUI();
		} else {
			System.err.println("No next question.");
		}

	}
}

























