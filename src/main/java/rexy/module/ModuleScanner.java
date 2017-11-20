package rexy.module;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.ImplementingClassMatchProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isInterface;

/**
 * Scans certain packages on the classpath to find modules. The {@code rexy} classpath will always be
 * scanned. Additional packages may be supplied.
 */
public class ModuleScanner {
	private static final Logger logger = LogManager.getLogger(ModuleScanner.class);
	
	private final List<String> scanPackages;
	
	/**
	 * Creates a scanner that will scan the {@code rexy} classpath as well as any additional
	 * classpaths supplied.
	 */
	public ModuleScanner(List<String> scanPackages) {
		this.scanPackages = scanPackages;
	}
	
	/**
	 * Scans the classspath for any concrete classes that implement {@link Module modules}.
	 *
	 * @return The modules found with their no-args constructors called.
	 */
	public List<Module> scan() {
		List<Module> modules = new ArrayList<>();
		scan(modules, getClass().getPackage().getName());
		for (String scanPackage : scanPackages) {
			scan(modules, scanPackage);
		}
		return modules;
	}
	
	private void scan(List<Module> modules, String scanPackage) {
		new FastClasspathScanner(scanPackage)
				.matchClassesImplementing(Module.class, new ModuleCreator(modules))
				.scan();
	}
	
	/**
	 * Processes a {@link Module module} class. Checks to see if it's a concrete class and, if it is,
	 * call it's no-args constructor and adds it to the list of modules.
	 */
	private static class ModuleCreator implements ImplementingClassMatchProcessor<Module> {
		private final List<Module> modules;
		
		ModuleCreator(List<Module> modules) {this.modules = modules;}
		
		@Override
		public void processMatch(Class<? extends Module> moduleClass) {
			if (!isAbstract(moduleClass.getModifiers()) && !isInterface(moduleClass.getModifiers())) {
				logger.debug("Found module: " + moduleClass.getCanonicalName());
				try {
					Constructor<? extends Module> constructor = moduleClass.getConstructor();
					constructor.setAccessible(true);
					modules.add(constructor.newInstance());
				}
				catch (ReflectiveOperationException e) {
					logger.error("Could not create " + moduleClass.getCanonicalName(), e);
				}
			}
		}
	}
}
