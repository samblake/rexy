package rexy.utils.loader;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import rexy.module.jmx.mock.MockEndpoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@SuppressFBWarnings(
	value = {
		"NP_LOAD_OF_KNOWN_NULL_VALUE",
		"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE",
		"RCN_REDUNDANT_NULLCHECK_OF_NULL_VALUE"
	},
	justification = "Seems to be a false positive - https://github.com/spotbugs/spotbugs/issues/1338"
)
public class ResourceLoader implements Loader {
	
	@Override
	public String load(String path) throws IOException {
		try (InputStream inputStream = MockEndpoint.class.getResourceAsStream('/' + path)) {
			if (inputStream != null) {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, UTF_8))) {
					return reader.lines().collect(Collectors.joining("\n"));
				}
			}
		}
		return null;
	}
	
}