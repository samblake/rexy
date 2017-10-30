package rexy;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import rexy.config.Config;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

public class ConfigTest {

	@Test
	public void test() throws IOException {
		InputStream inputStream = this.getClass().getResourceAsStream("/config.json");
		ObjectMapper mapper = new ObjectMapper();
		Config value = mapper.readValue(inputStream, Config.class);
		assertNotNull(value);
	}
}
