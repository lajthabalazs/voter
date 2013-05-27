package hu.edudroid.quiz_server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import it.unipr.ce.dsg.s2p.sip.Address;
import hu.edudroid.quiz_engine.AnswerMessage;
import hu.edudroid.quiz_engine.PingMessage;
import hu.edudroid.quiz_engine.QuestionMessage;
import hu.edudroid.quiz_engine.QuizPeer;
import hu.edudroid.quiz_engine.QuizPeerListener;

public class QuizServer implements QuizPeerListener {
	
	private static final int SERVER_PORT = 32458;
	private QuizPeer peer;
	private int actualQuestion = -1;
	private String[] questionIds;
	private String[] questions;
	private String[][] answers;
	private String[][] userCodes; 
	
	private HashMap<String, Address> clients = new HashMap<String, Address>();
	private Set<QuizPeerListener> listeners = new HashSet<QuizPeerListener>();

	public QuizServer(String[] questionIds, String[] questions, String[][] answers, String[][] userCodes) {
		this.questionIds = questionIds;
		this.questions = questions;
		this.answers = answers;
		this.userCodes = userCodes;
		peer = new QuizPeer("server", "server", SERVER_PORT);
		peer.registerListener(this);
	}
	
	public boolean askQuestion(int index) {		
		if (index < questionIds.length && index >= 0) {
			actualQuestion = index;
			for (String code : clients.keySet()) {
				peer.sendQuestion(clients.get(code), questionIds[index], questions[index], answers[index]);
			}
			return true;
		} else {
			actualQuestion = -1;
			return false;
		}
	}

	@Override
	public void messageSendingError() {
		System.out.println("Message sending error.");
	}

	@Override
	public void messageSendingSuccess() {
		System.out.println("Message sending success.");
	}

	@Override
	public void answerReceived(Address sender, AnswerMessage answer) {
		System.out.println("Answer received from " + answer.getCode());
		for (QuizPeerListener listener : listeners) {
			listener.answerReceived(sender, answer);
		}
	}

	@Override
	public void questionReceived(Address source, QuestionMessage question) {
	}

	@Override
	public void pingReceived(Address source, PingMessage ping) {
		String code = ping.getCode();
		System.out.println("Ping received from " + code);
		// Checks if code is part of the game
		boolean found = false;
		for (int i = 0; i < userCodes.length; i++) {
			if (code == userCodes[i][0]) {
				found = true;
				break;
			}
		}
		if (!found) {
			return;
		}
		clients.put(code, source);
		if (actualQuestion != -1) {
			QuestionMessage message = new QuestionMessage(questionIds[actualQuestion], questions[actualQuestion], answers[actualQuestion]);
			peer.send(source, message);
		}
	}

	public String[][] getUsers() {
		return userCodes;
	}

	public String getQuestion(int index) {
		return questions[index];
	}

	public String[] getAnswers(int index) {
		return answers[index];
	}

	public void registerListener(QuizPeerListener listener) {
		listeners.add(listener);
	}
}























