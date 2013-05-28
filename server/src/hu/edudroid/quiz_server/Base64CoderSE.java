package hu.edudroid.quiz_server;

import java.io.UnsupportedEncodingException;

import org.zoolu.tools.Base64;

import hu.edudroid.quiz_engine.Base64Coder;

public class Base64CoderSE implements Base64Coder {
	
	@Override
	public String encode(String string) {
		try {
			System.out.println("Encode " + string);
			String result = Base64.encode(string.getBytes("UTF-8"));
			System.out.println("Encoded " + result);
			return result;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return Base64.encode(string.getBytes());
		}
	}

	@Override
	public String decode(String base64EncodedString) {
		try {
			return new String(Base64.decode(base64EncodedString), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new String(Base64.decode(base64EncodedString));
		}
	}

	@Override
	public String[] encode(String[] strings) {
		String[] encodedStrings = new String[strings.length];
		for (int i = 0; i < strings.length; i++) {
			encodedStrings[i] = encode(strings[i]);
		}
		return encodedStrings;
	}

	@Override
	public String[] decode(String[] base64EncodedStrings) {
		String[] strings = new String[base64EncodedStrings.length];
		for (int i = 0; i < base64EncodedStrings.length; i++) {
			strings[i] = decode(base64EncodedStrings[i]);
		}
		return strings;
	}

}
