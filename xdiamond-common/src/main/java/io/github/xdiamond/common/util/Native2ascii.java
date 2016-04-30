package io.github.xdiamond.common.util;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * form http://stackoverflow.com/questions/10008989/library-for-converting-
 * native2ascii-and-vice-versa
 * 
 * @author hengyunabc
 * 
 */
public class Native2ascii {
	static public String encode(String utf8) {
		final CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();
		final StringBuilder result = new StringBuilder();
		for (final Character character : utf8.toCharArray()) {
			if (asciiEncoder.canEncode(character)) {
				result.append(character);
			} else {
				result.append("\\u");
				result.append(Integer.toHexString(0x10000 | character).substring(1).toUpperCase());
			}
		}
		return result.toString();
	}
}
