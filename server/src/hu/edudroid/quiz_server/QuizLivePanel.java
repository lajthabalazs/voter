package hu.edudroid.quiz_server;

import hu.edudroid.quiz_engine.AnswerMessage;
import hu.edudroid.quiz_engine.PingMessage;
import hu.edudroid.quiz_engine.QuestionMessage;
import hu.edudroid.quiz_engine.QuizPeerListener;
import it.unipr.ce.dsg.s2p.sip.Address;

import java.awt.Container;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SpringLayout;

public class QuizLivePanel extends JFrame implements QuizPeerListener {
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
	
	public QuizLivePanel(QuizServer server) {
		this.server = server;
		server.registerListener(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		Container contentPane = getContentPane();
		contentPane.setLayout(springLayout);
		
		question = new JLabel();
		nextQuestion = new JButton("Next");
		answerList = new JList();
		clients = new JList();
		
		clientListModel = new DefaultListModel();
		clients.setModel(clientListModel);

		answerListModel = new DefaultListModel();
		answerList.setModel(answerListModel);

		contentPane.add(question);
		contentPane.add(nextQuestion);
		contentPane.add(answerList);
		
		springLayout.putConstraint(SpringLayout.WEST, question, 5, SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.WEST, nextQuestion, 5, SpringLayout.EAST, question);
		springLayout.putConstraint(SpringLayout.EAST, contentPane, 5, SpringLayout.EAST, nextQuestion);
		springLayout.putConstraint(SpringLayout.WEST, answerList, 5, SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.EAST, answerList, 5, SpringLayout.EAST, contentPane);
		springLayout.putConstraint(SpringLayout.WEST, clients, 5, SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.EAST, clients, 5, SpringLayout.EAST, contentPane);
		
		springLayout.putConstraint(SpringLayout.NORTH, question, 5, SpringLayout.NORTH, contentPane);
		springLayout.putConstraint(SpringLayout.NORTH, nextQuestion, 5, SpringLayout.NORTH, contentPane);
		springLayout.putConstraint(SpringLayout.NORTH, answerList, 5, SpringLayout.SOUTH, question);
		springLayout.putConstraint(SpringLayout.NORTH, clients, 5, SpringLayout.SOUTH, answerList);
		springLayout.putConstraint(SpringLayout.SOUTH, contentPane, 5, SpringLayout.SOUTH, clients);
		
		pack();
		setVisible(true);
	}
	
	private void askQuestion(int index) {
		users = server.getUsers();
		answered = new boolean[users.length];
		for (int i = 0; i < users.length; i++) {
			answered[i] = false;
		}
		updateClientList();
		question.setText(server.getQuestion(index));
		String[] answers = server.getAnswers(index);
		answerListModel.clear();
		for (int i = 0; i < answers.length; i++) {
			answerListModel.addElement(answers[i]);
		}
		server.askQuestion(index);
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

	@Override
	public void messageSendingError() {}

	@Override
	public void messageSendingSuccess() {}

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
}

























