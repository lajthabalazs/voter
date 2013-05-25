package hu.edudroid.quiz;

import it.unipr.ce.dsg.s2p.org.json.JSONException;
import it.unipr.ce.dsg.s2p.org.json.JSONObject;
import it.unipr.ce.dsg.s2p.peer.Peer;
import it.unipr.ce.dsg.s2p.sip.Address;

public class QuizPeer extends Peer{

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

	public void sendPing(Address toAddress) {
		PingMessage message = new PingMessage();
		super.send(toAddress, message);
	}

	@Override
	protected void onReceivedJSONMsg(JSONObject jsonMsg, Address sender) {
		try {
			String msgType = jsonMsg.getString("type");
			if (msgType.equals(QuestionMessage.QUESTION_MESSAGE_TYPE)) {
				// Simple chat message
				// TODO parse message
				// TODO Message received
			} else {
				// TODO Message error
			}
		} catch (JSONException e) {
			// TODO Message error
			e.printStackTrace();
		}
		super.onReceivedJSONMsg(jsonMsg, sender);
	}

	@Override
	protected void onDeliveryMsgFailure(String arg0, Address arg1, String arg2) {
		// TODO Message sending error
	}

	@Override
	protected void onDeliveryMsgSuccess(String arg0, Address arg1, String arg2) {
		// TODO Message sending success
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

	public void registerListener(QuizService messageService) {
		// TODO Auto-generated method stub
		
	}
}