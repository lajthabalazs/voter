package hu.edudroid.quiz_server;

import hu.edudroid.quiz_engine.AnswerMessage;
import hu.edudroid.quiz_engine.PingMessage;
import hu.edudroid.quiz_engine.QuestionMessage;
import hu.edudroid.quiz_engine.QuizPeerListener;
import hu.edudroid.quiz_engine.TimeoutMessage;
import hu.edudroid.quiz_server.QuizQuestion.Type;
import it.unipr.ce.dsg.s2p.sip.Address;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

public class QuizGame implements QuizPeerListener{
	private ArrayList<QuizRound> rounds = new ArrayList<QuizRound>();
	private int actualRound = -1;
	private int actualQuestion = -1;
	private HashMap<String, QuizPlayer> players = new HashMap<String,QuizPlayer>();
	private HashSet<QuizGameListener> listeners = new HashSet<QuizGameListener>();
	
	public QuizGame(String fileName) throws IOException {
		System.out.println("Parsing");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
		QuizRound actualRound = null;
		QuizQuestion actualQuestion = null;
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			if (line.startsWith("R")) {
				System.out.println("Round");
				actualRound = new QuizRound();
				rounds.add(actualRound);
				actualQuestion = null;
			} else if (line.startsWith("Q")) {
				if (actualRound != null) {
					String[] parts = line.split(" ");
					String questionId = parts[1];
					Type type = Type.parse(parts[2]);
					String text = line.substring(1 + 1 + parts[1].length() + 1 + parts[2].length() + 1);
					actualQuestion = new QuizQuestion(actualRound, questionId, text, type);
					actualRound.addQuestion(actualQuestion);
					System.out.println("   Question " + actualQuestion.toString());
				} else {
					System.err.println("Question found outside of round : " + line);
				}
			} else if (line.startsWith("A")) {
				if (actualQuestion != null) {
					QuizAnswer answer = new QuizAnswer();
					String[] parts = line.split(" ");
					answer.setPointValue(Integer.parseInt(parts[1]));
					answer.setText(line.substring(1 + 1 + parts[1].length() + 1));
					actualQuestion.addAnswer(answer);
					System.out.println("      Answer " + answer.toString());
				} else {
					System.err.println("Answer found outside of question : " + line);
				}
			} else if (line.startsWith("C")) {
				actualRound = null;
				actualQuestion = null;
				String[] parts = line.split(" ");
				String code = parts[1];
				String teamName = line.substring(1 + 1 + parts[1].length() + 1);
				QuizPlayer player = new QuizPlayer(code, teamName, 5, 5);
				players.put(code, player);
				System.out.println("User " + code + " -> " + teamName);
			} else {
				System.out.println("UNPARSED " + line);
			}
		}
		reader.close();
	}
	
	public boolean playRound(int round) {
		if (round < 0) {
			return false;
		} else if (rounds.size() > round) {
			actualRound = round;
			actualQuestion = 0;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean playQuestion(int question) {
		if (actualRound == -1) {
			System.out.println("Actual round = -1");
			return false;
		} else if (question < 0) {
			System.out.println("Question < 0");
			return false;
		} else if (rounds.get(actualRound).getQuestionCount() > question) {
			actualQuestion = question;
			return true;
		} else {
			System.out.println("Question out of bounds");
			return false;
		}
	}
	
	/**
	 * Gets the specific user's score for the given round.
	 * @param round The index of the round
	 * @param playerCode The code of the player
	 * @return
	 */
	public int getScore(int roundIndex, String playerCode) {
		QuizRound round = rounds.get(roundIndex);
		ArrayList<String> roundsQuestions = round.getQuestionIds();
		ArrayList<UserAnswer> playerAnswers = new ArrayList<UserAnswer>();
		QuizPlayer actualPlayer = players.get(playerCode);
		ArrayList<ArrayList<UserAnswer>> allAnswers = new ArrayList<ArrayList<UserAnswer>>();
		for (String questionId : roundsQuestions) {
			playerAnswers.add(actualPlayer.getAnswer(questionId));
			ArrayList<UserAnswer> allAnswersForQuestion = new ArrayList<UserAnswer>();
			for (QuizPlayer player : players.values()) {
				allAnswersForQuestion.add(player.getAnswer(questionId));
			}
			allAnswers.add(allAnswersForQuestion);
		}		
		return round.getScore(playerAnswers, allAnswers);
	}

	/**
	 * Gets the specific user's scores for each question of the given round.
	 * @param round The index of the round
	 * @param playerCode The code of the player
	 * @return Time series for each question in the round. Each item is an accumulated value: [0] score for the first answer [1] score for the first and second
	 * answer. And so on.
	 */
	public int[] getScores(int roundIndex, String playerCode) {
		QuizRound round = rounds.get(roundIndex);
		ArrayList<String> roundsQuestions = round.getQuestionIds();
		ArrayList<UserAnswer> playerAnswers = new ArrayList<UserAnswer>();
		QuizPlayer actualPlayer = players.get(playerCode);
		ArrayList<ArrayList<UserAnswer>> allAnswers = new ArrayList<ArrayList<UserAnswer>>();
		for (String questionId : roundsQuestions) {
			playerAnswers.add(actualPlayer.getAnswer(questionId));
			ArrayList<UserAnswer> allAnswersForQuestion = new ArrayList<UserAnswer>();
			for (QuizPlayer player : players.values()) {
				allAnswersForQuestion.add(player.getAnswer(questionId));
			}
			allAnswers.add(allAnswersForQuestion);
		}		
		return round.getScores(playerAnswers, allAnswers);
	}

	public boolean hasPlayer(String code) {
		return players.containsKey(code);
	}

	public QuizQuestion getActualQuestion() {
		if ((actualRound != -1) && (actualQuestion != -1)) {
			return rounds.get(actualRound).getQuestion(actualQuestion);
		} else {
			return null;
		}
	}

	public int getActualRoundIndex() {
		return actualRound;
	}

	public int getActualQuestionIndex() {
		return actualQuestion;
	}

	public boolean hasNextRound() {
		return actualRound + 1 < rounds.size();
	}
	
	public boolean hasNextQuestion() {
		if (actualRound == -1) {
			return false;
		} else {
			return actualQuestion + 1 < rounds.get(actualRound).getQuestionCount();
		}
	}

	public boolean hasActiveQuestion() {
		return actualQuestion != -1;
	}

	public List<QuizPlayer> getPlayers() {
		TreeSet<String> keys = new TreeSet<String>(players.keySet());
		ArrayList<QuizPlayer> sortedPlayers = new ArrayList<QuizPlayer>();
		for (String key : keys) {
			sortedPlayers.add(players.get(key));
		}
		return sortedPlayers;
	}
	
	public void registerListener(QuizGameListener listener) {
		listeners.add(listener);
	}
	
	public void unregisterListener(QuizGameListener listener) {
		listeners.remove(listener);
	}
	
	public boolean setAnswer(String questionId, String code, int answer, boolean askForDouble, boolean askForDoubleOrNothing) {
		QuizPlayer player = players.get(code);
		if (player != null) {
			if (player.getAnswer(questionId) == null) {
				player.addAnswer(questionId, answer, askForDouble, askForDoubleOrNothing);
				for (QuizGameListener listener : listeners) {
					listener.modelChanged();
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public void answerReceived(Address sender, AnswerMessage answer) {
		setAnswer(answer.getQuestionId(), answer.getCode(), Integer.parseInt(answer.getAnswerId()), answer.getAskForDouble(), answer.getAskForDoubleOrNothing());
	}

	@Override public void messageSendingError(String sentMessage, Address destination,String messageType) {}
	@Override public void messageSendingSuccess(String sentMessage, Address destination,String messageType) {}
	@Override public void questionReceived(Address sender, QuestionMessage question) {}
	@Override public void pingReceived(Address sender, PingMessage ping) {}
	@Override public void timeoutReceived(Address sender, TimeoutMessage timeout) {}

	public QuizPlayer getPlayer(String code) {
		return players.get(code);
	}
}