package rexy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import rexy.config.model.Config;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConfigTest {
	
	@Test
	public void test() throws IOException {
		InputStream inputStream = getClass().getResourceAsStream("/config.json");
		ObjectMapper mapper = new ObjectMapper();
		Config value = mapper.readValue(inputStream, Config.class);
		assertThat(value, notNullValue());
	}
	
}