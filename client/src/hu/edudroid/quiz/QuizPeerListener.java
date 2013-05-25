package hu.edudroid.quiz;

public interface QuizPeerListener {
	void messageSendingError();
	void messageSendingSuccess();
	void answerReceived(AnswerMessage answer);
	void questionReceived(QuestionMessage question);
}