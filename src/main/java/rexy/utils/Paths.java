package rexy.utils;

import java.util.regex.Pattern;

public class Paths {
	
	private static final Pattern SHASH = Pattern.compile("//");
	
	private Paths() {
	}
	
	public static String join(String... partialPaths) {
		return SHASH.matcher(String.join("/", partialPaths)).replaceAll("/");
	}

}