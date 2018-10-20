package rexy.utils;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

public class Paths {
	
	private Paths() {
	}
	
	public static String join(String... partialPaths) {
		return stream(partialPaths)
				.collect(joining("/"))
				.replaceAll("//", "/");
	}

}