package hu.edudroid.quiz;

import java.io.UnsupportedEncodingException;

import android.util.Base64;
import hu.edudroid.quiz_engine.Base64Coder;

public class Base64CoderAndroid implements Base64Coder {

	@Override
	public String encode(String string) {
		try {
			System.out.println("Encode " + string);
			String result = Base64.encodeToString(string.getBytes("UTF-8"), Base64.DEFAULT);
			System.out.println("Encoded " + result);
			return result;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return Base64.encodeToString(string.getBytes(), Base64.DEFAULT);
		} catch (Exception e) {
			e.printStackTrace();
			return string;
		}
	}

	@Override
	public String decode(String base64EncodedString) {
		try {
			System.out.println("Decode " + base64EncodedString);
			if (base64EncodedString.endsWith("\n")) {
				base64EncodedString = base64EncodedString.substring(0, base64EncodedString.length() - "\n".length());
				System.out.println("Removed new line " + base64EncodedString);
			}
			String result = new String(Base64.decode(base64EncodedString, Base64.DEFAULT), "UTF-8");
			System.out.println("Decoded " + result);
			return result;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new String(Base64.decode(base64EncodedString, Base64.DEFAULT));
		} catch (Exception e) {
			e.printStackTrace();
			return base64EncodedString;
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
