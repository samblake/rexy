package rexy;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rexy.config.ConfigParser;
import rexy.config.model.Api;
import rexy.config.model.Config;
import rexy.exception.RexyException;
import rexy.http.RexyServer;
import rexy.module.Module;
import rexy.module.ModuleInitialisationException;
import rexy.module.ModuleScanner;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import static rexy.utils.JsonNodes.booleanValue;

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
			Config config = new ConfigParser(configPath).parse();
			List<Module> allModules = new ModuleScanner(config.getScanPackages()).scan();
			List<Module> enabledModules = initModules(config, allModules);
			
			new RexyServer(config, enabledModules).start();
			logger.info("Rexy started on port " + config.getPort());
		}
		catch (IOException | RexyException e) {
			logger.error("Rexy server failed to start", e);
		}
	}
	
	private List<Module> initModules(Config config, List<Module> modules) throws ModuleInitialisationException {
		List<Module> enabledModules = new LinkedList<>();
		Set<Entry<String, JsonNode>> moduleConfigs = config.getModules().entrySet();
		for (Entry<String, JsonNode> moduleConfig : moduleConfigs) {
			enabledModules.addAll(initModule(modules, moduleConfig, config.getApis()));
		}
		return enabledModules;
	}
	
	private List<Module> initModule(List<Module> modules, Entry<String, JsonNode> moduleConfig, List<Api> apis)
			throws ModuleInitialisationException {
		
		List<Module> enabledModules = new LinkedList<>();
		
		String moduleName = moduleConfig.getKey();
		Module module = modules.stream().filter(m -> m.getName().equals(moduleName)).findFirst()
				.orElseThrow(() -> new ModuleInitialisationException("Could not find module " + moduleName));
		
		if (booleanValue(moduleConfig.getValue(), ("enabled"))) {
			module.init(moduleConfig.getValue());
			
			for (Api api : apis) {
				module.initEndpoint(api);
			}
			
			enabledModules.add(module);
			logger.info("Started module: " + moduleName);
		}
		else {
			logger.info("Module disabled: " + moduleName);
		}
		
		return enabledModules;
	}
	
}