package hu.edudroid.quiz_engine;

public interface Base64Coder {
	public String encode(String string);
	public String decode(String base64EncodedString);
	public String[] encode(String[] strings);
	public String[] decode(String[] base64EncodedStrings);
}
