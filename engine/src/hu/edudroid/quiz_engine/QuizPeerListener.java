package hu.edudroid.quiz_engine;

import it.unipr.ce.dsg.s2p.sip.Address;

public interface QuizPeerListener {
	void messageSendingError();
	void messageSendingSuccess();
	void answerReceived(Address sender, AnswerMessage answer);
	void questionReceived(Address sender, QuestionMessage question);
	void pingReceived(Address sender, PingMessage ping);
}