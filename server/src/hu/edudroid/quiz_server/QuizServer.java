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
	
	private QuizPeer peer;
	private HashMap<String, Address> clients = new HashMap<String, Address>();
	private Set<QuizPeerListener> listeners = new HashSet<QuizPeerListener>();
	
	private QuizGame game;
	
	public QuizServer(QuizGame game) {
		this.game = game;
		peer = new QuizPeer("server", "server", QuizPeer.SERVER_PORT, new Base64CoderSE());
		peer.registerListener(this);
	}
	
	public void registerListener(QuizPeerListener listener) {
		listeners.add(listener);
	}
	
	@Override public void messageSendingError(String sentMessage, Address destination, String messageType) {}
	@Override public void messageSendingSuccess(String sentMessage, Address destination, String messageType) {}

	@Override
	public void answerReceived(Address sender, AnswerMessage answer) {
		System.out.println("Answer received from " + answer.getCode());
		for (QuizPeerListener listener : listeners) {
			listener.answerReceived(sender, answer);
		}
	}

	@Override
	public void questionReceived(Address source, QuestionMessage question) {}

	@Override
	public void pingReceived(Address source, PingMessage ping) {
		String code = ping.getCode();
		System.out.println("Ping received from " + code);
		// Checks if code is part of the game
		if (!game.hasPlayer(code)) {
			System.out.println("Invalid code");
			return;
		}
		System.out.println("Valid code, " + code + " @ " + source.toString());
		clients.put(code, source);
		QuizQuestion question = game.getActualQuestion();
		if (question != null) {
			System.out.println("Send question");
			peer.sendQuestion(source, question.getQuestionId(), question.getQuestionText(), question.getAnswerStrings());
		} else {
			System.out.println("No question yet.");
		}
	}
}