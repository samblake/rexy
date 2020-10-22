package rexy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.config.ConfigParser;
import rexy.config.RexyConfig;
import rexy.config.model.Api;
import rexy.exception.RexyException;
import rexy.http.RexyServer;
import rexy.module.ModuleInitialisationException;
import rexy.module.ModuleScanner;
import rexy.module.RexyModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import static rexy.utils.Json.booleanValue;

/**
 * A lightweight REST mock/proxy framework.
 *
 * <p>This is the main class. Run it from the command line with the path to the config file as the argument.
 * The path can either be an absolute file or relative to the classpath.</p>
 */
public class Rexy {
	private static final Logger logger = LogManager.getLogger(Rexy.class);
	
	private static final String DEFAULT_PATH = "rexy.json";
	
	private final String configPath;
	
	public Rexy(String configPath) {
		this.configPath = configPath;
	}
	
	/**
	 * Runs the Rexy server.
	 *
	 * @param args There should be a single argument, the path to the config file
	 */
	public static void main(String[] args) {
		String path = (args.length == 0) ? DEFAULT_PATH : args[0];
		new Rexy(path).start();
	}
	
	/**
	 * Reads the config file and runs the server.
	 */
	public void start() {
		logger.info("Starting Rexy");
		try {
			RexyConfig config = new ConfigParser(configPath).parse();
			List<RexyModule> allModules = new ModuleScanner(config.getScanPackages()).scan();
			List<RexyModule> enabledModules = initModules(config, allModules);
			
			new RexyServer(config, enabledModules).start();
			logger.info("Rexy started on port " + config.getPort());
		}
		catch (IOException | RexyException e) {
			logger.error("Rexy server failed to start", e);
		}
	}
	
	private List<RexyModule> initModules(RexyConfig config, List<RexyModule> modules) throws ModuleInitialisationException {
		RexyDetails rexyDetails = new RexyDetails(config.getPort(), config.getBasePath());
		
		List<RexyModule> enabledModules = new ArrayList<>();
		for (Entry<String, JsonNode> moduleConfig : config.getModules().entrySet()) {
			enabledModules.addAll(initModule(rexyDetails, modules, moduleConfig, config.getApis()));
		}
		
		return enabledModules;
	}
	
	private List<RexyModule> initModule(RexyDetails rexyDetails, List<RexyModule> modules,
			Entry<String, JsonNode> moduleConfig, List<Api> apis) throws ModuleInitialisationException {
		
		List<RexyModule> enabledModules = new ArrayList<>();
		
		String moduleName = moduleConfig.getKey();
		RexyModule module = modules.stream().filter(m -> m.getName().equals(moduleName)).findFirst()
				.orElseThrow(() -> new ModuleInitialisationException("Could not find module " + moduleName));
		
		if (booleanValue(moduleConfig.getValue(), "enabled")) {
			module.init(rexyDetails, moduleConfig.getValue());
			
			for (Api api : apis) {
				module.initEndpoint(api);
			}
			
			enabledModules.add(module);
			logger.info("Started module: " + moduleName);
		}
		else {
			logger.info("RexyModule disabled: " + moduleName);
		}
		
		return enabledModules;
	}
	
	public static class RexyDetails {
		private final int port;
		private final String baseUrl;
		
		public RexyDetails(int port, String baseUrl) {
			this.port = port;
			this.baseUrl = baseUrl;
		}
		
		public int getPort() {
			return port;
		}
		
		public String getBaseUrl() {
			return baseUrl;
		}
		
	}
	
}