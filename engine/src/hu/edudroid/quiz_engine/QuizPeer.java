package hu.edudroid.quiz_engine;

import java.util.HashSet;

import it.unipr.ce.dsg.s2p.org.json.JSONException;
import it.unipr.ce.dsg.s2p.org.json.JSONObject;
import it.unipr.ce.dsg.s2p.peer.Peer;
import it.unipr.ce.dsg.s2p.sip.Address;

public class QuizPeer extends Peer{

	private HashSet<QuizPeerListener> listeners = new HashSet<QuizPeerListener>();

	public QuizPeer(String key, String peerName, int peerPort) {
		super(null, key, peerName, peerPort);
	}
	
	public void sendAnswer(Address toAddress, String code, String questionId, String answer) {
		AnswerMessage message = new AnswerMessage(code, questionId, answer);
		super.send(toAddress, message);
	}

	public void sendQuestion(Address toAddress, String questionId, String question, String[] answers) {
		QuestionMessage message = new QuestionMessage(questionId, question, answers);
		super.send(toAddress, message);
	}

	public void sendPing(Address toAddress, String code) {
		PingMessage message = new PingMessage(code);
		super.send(toAddress, message);
	}

	@Override
	protected void onReceivedJSONMsg(JSONObject jsonMsg, Address sender) {
		try {
			String msgType = jsonMsg.getString("type");
			if (msgType.equals(QuestionMessage.QUESTION_MESSAGE_TYPE)) {
				QuestionMessage question = new QuestionMessage(jsonMsg);
				for (QuizPeerListener listener : listeners) {
					listener.questionReceived(sender, question);
				}
			} else if (msgType.equals(AnswerMessage.ANSWER_MESSAGE_TYPE)) {
				AnswerMessage answer = new AnswerMessage(jsonMsg);
				for (QuizPeerListener listener : listeners) {
					listener.answerReceived(sender, answer);
				}
			} else if (msgType.equals(PingMessage.PING_MESSAGE_TYPE)) {
				PingMessage ping = new PingMessage(jsonMsg);
				for (QuizPeerListener listener : listeners) {
					listener.pingReceived(sender, ping);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		super.onReceivedJSONMsg(jsonMsg, sender);
	}

	@Override
	protected void onDeliveryMsgFailure(String sentMessage, Address destination, String messageType) {
		for (QuizPeerListener listener : listeners) {
			listener.messageSendingError(sentMessage, destination, messageType);
		}
	}

	@Override
	protected void onDeliveryMsgSuccess(String sentMessage, Address destination, String messageType) {
		for (QuizPeerListener listener : listeners) {
			listener.messageSendingSuccess(sentMessage, destination, messageType);
		}
	}


	public String getLocalAddressString() {
		String localAddress = peerDescriptor.getAddress();
		if (localAddress.contains("@")) {
			localAddress = localAddress.substring(localAddress.lastIndexOf("@") + 1);
		}
		if (localAddress.contains(":")) {
			localAddress = localAddress.substring(0,localAddress.indexOf(":"));
		}
		return localAddress;
	}

	public void registerListener(QuizPeerListener listener) {
		listeners.add(listener);
	}
	
	public void unregisterListener(QuizPeerListener listener) {
		listeners.remove(listener);
	}
}