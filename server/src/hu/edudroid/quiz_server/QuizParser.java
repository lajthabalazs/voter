package hu.edudroid.quiz_server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class QuizParser {
	private ArrayList<String> questionIds = new ArrayList<String>();
	private ArrayList<String> questions = new ArrayList<String>();
	private ArrayList<ArrayList<String>> answers = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<String>> codes = new ArrayList<ArrayList<String>>();
	
	public QuizParser(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		
		ArrayList<String> questionAnswers = null;
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			if (line.startsWith("Q")) {
				String[] parts = line.split(" ");
				questionIds.add(parts[1]);
				questions.add(line.substring(1 + 1 + parts[1].length() + 1));
				questionAnswers = new ArrayList<String>();
				answers.add(questionAnswers);
			} else if (line.startsWith("A")) {
				if (questionAnswers != null) {
					questionAnswers.add(line.substring(1 + 1));
				}
			} else if (line.startsWith("C")) {
				String[] parts = line.split(" ");
				String code = parts[1];
				String teamName = line.substring(1 + 1 + parts[1].length() + 1);
				ArrayList<String> items = new ArrayList<String>();
				items.add(code);
				items.add(teamName);
				codes.add(items);
			}
		}
		reader.close();
	}
	
	public String[] getQuestionIds() {
		return questionIds.toArray(new String[questionIds.size()]);
	}

	public String[] getQuestions() {
		return questions.toArray(new String[questionIds.size()]);
	}

	public String[][] getAnswers() {
		String[][] answers = new String[this.answers.size()][];
		for (int i = 0; i < answers.length; i++) {
			answers[i] = this.answers.get(i).toArray(new String[this.answers.get(i).size()]);
		}
		return answers;
	}

	/**
	 * Each item contains the client code and the client's display name
	 * @return
	 */
	public String[][] getCodes() {
		String[][] codes = new String[this.codes.size()][];
		for (int i = 0; i < codes.length; i++) {
			codes[i] = this.codes.get(i).toArray(new String[this.codes.get(i).size()]);
		}
		return codes;
	}
}
