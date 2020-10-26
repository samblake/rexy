package rexy.utils.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileLoader implements Loader {
	
	@Override
	public String load(String path) throws IOException {
		File file = new File(path);
		if (file.exists()) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), UTF_8))) {
				return reader.lines().collect(Collectors.joining("\n"));
			}
		}
		return null;
	}
	
}