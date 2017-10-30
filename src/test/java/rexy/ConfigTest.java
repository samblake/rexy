package rexy;

import org.codehaus.jackson.map.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import rexy.config.Config;

import java.io.IOException;
import java.io.InputStream;

public class ConfigTest {

	@Test
	public void test() throws IOException {
		InputStream inputStream = this.getClass().getResourceAsStream("/config.json");
		ObjectMapper mapper = new ObjectMapper();
		Config value = mapper.readValue(inputStream, Config.class);
		assertNotNull(value);
	}
}
