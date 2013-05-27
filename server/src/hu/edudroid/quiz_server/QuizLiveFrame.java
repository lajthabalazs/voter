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

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;

public class QuizLiveFrame extends JFrame implements QuizPeerListener, ActionListener {
	private static final long serialVersionUID = -5423453185160139085L;
	// Shows actual question, active peers, and peers who sent in a response
	JLabel question;
	JList answerList;
	JList clients;
	JButton nextQuestion;
	private DefaultListModel clientListModel;
	private DefaultListModel answerListModel;
	private boolean[] answered;
	private QuizServer server;
	private String[][] users;
	int actualQuestion = 0;
	
	public QuizLiveFrame(QuizServer server) {
		this.server = server;
		server.registerListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		Container contentPane = getContentPane();
		
		question = new JLabel();
		question.setMaximumSize(new Dimension(600, 600));
		nextQuestion = new JButton("Next");
		nextQuestion.addActionListener(this);
		answerList = new JList();
		answerList.setCellRenderer(new MultilineLabelRenderer());
		clients = new JList();
		clients.setBackground(Color.LIGHT_GRAY);
		question.setFont(new Font(Font.SERIF, Font.BOLD, 20));
		
		clientListModel = new DefaultListModel();
		clients.setModel(clientListModel);

		answerListModel = new DefaultListModel();
		answerList.setModel(answerListModel);

		contentPane.add(question, BorderLayout.PAGE_START);
		contentPane.add(answerList, BorderLayout.CENTER);
		contentPane.add(clients, BorderLayout.LINE_END);
		contentPane.add(nextQuestion, BorderLayout.PAGE_END);
		
		pack();
		setVisible(true);
		askQuestion();
	}
	
	private void askQuestion() {
		users = server.getUsers();
		answered = new boolean[users.length];
		for (int i = 0; i < users.length; i++) {
			answered[i] = false;
		}
		updateUI();
		server.askQuestion(actualQuestion);
	}
	
	private void updateUI() {
		if (server.getQuestionCount() > actualQuestion) {
			nextQuestion.setEnabled(server.getQuestionCount() > actualQuestion + 1);
			question.setText("<HTML>" + server.getQuestion(actualQuestion) + "</HTML>");
			String[] answers = server.getAnswers(actualQuestion);
			answerListModel.clear();
			for (int i = 0; i < answers.length; i++) {
				answerListModel.addElement("<HTML>" + answers[i] + "</HTML>");
			}
			updateClientList();
		}
		pack();
	}
	
	private void updateClientList() {
		clientListModel.clear();
		for (int i = 0; i < users.length; i++) {
			String item = users[i][1];
			if (answered[i]) {
				item = item + " ANSWERED";
			} else {
				item = item + " WAITING";
			}
			clientListModel.addElement(item);
		}
	}

	@Override public void messageSendingError(String sentMessage, Address destination, String messageType) {}
	@Override public void messageSendingSuccess(String sentMessage, Address destination, String messageType) {}

	@Override
	public void answerReceived(Address sender, AnswerMessage answer) {
		int index = -1;
		String code = answer.getCode();
		for (int i = 0; i < users.length; i++) {
			if (users[i][0].equals(code)) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			answered[index] = true;
			updateClientList();
		}
	}

	@Override
	public void questionReceived(Address sender, QuestionMessage question) {}

	@Override
	public void pingReceived(Address sender, PingMessage ping) {}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		actualQuestion ++;
		askQuestion();
	}
}

























