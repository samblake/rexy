package rexy.module;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.ImplementingClassMatchProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import static java.lang.reflect.Modifier.*;

/**
 * Scans certain packages on the classpath to find modules. The {@code rexy} classpath will always be
 * scanned. Additional packages may be supplied.
 */
public class ModuleScanner {
	private static final Logger logger = LogManager.getLogger(ModuleScanner.class);
	
	private final List<String> scanPackages;
	
	/**
	 * Creates a scanner that will scan the {@code rexy} package as well as any additional
	 * packages supplied.
	 */
	public ModuleScanner(List<String> scanPackages) {
		this.scanPackages = scanPackages;
	}
	
	/**
	 * Scans the classspath for any concrete classes that implement {@link RexyModule modules}.
	 *
	 * @return The modules found with their no-args constructors called.
	 */
	public List<RexyModule> scan() {
		List<RexyModule> modules = new ArrayList<>();
		scan(modules, getClass().getPackage().getName());
		for (String scanPackage : scanPackages) {
			scan(modules, scanPackage);
		}
		return modules;
	}
	
	private void scan(List<RexyModule> modules, String scanPackage) {
		new FastClasspathScanner(scanPackage)
				.matchClassesImplementing(RexyModule.class, new ModuleCreator(modules))
				.scan();
	}
	
	/**
	 * Processes a {@link RexyModule module} class. Checks to see if it's a concrete class and, if it is,
	 * call it's no-args constructor and adds it to the list of modules.
	 */
	private static final class ModuleCreator implements ImplementingClassMatchProcessor<RexyModule> {
		private final List<RexyModule> modules;
		
		private ModuleCreator(List<RexyModule> modules) {
			this.modules = modules;
		}
		
		@Override
		public void processMatch(Class<? extends RexyModule> moduleClass) {
			if (shouldInitialiseModule(moduleClass)) {
				logger.debug("Found module: " + moduleClass.getCanonicalName());
				try {
					Constructor<? extends RexyModule> constructor = moduleClass.getConstructor();
					constructor.setAccessible(true);
					modules.add(constructor.newInstance());
				}
				catch (ReflectiveOperationException e) {
					logger.error("Could not create " + moduleClass.getCanonicalName(), e);
				}
			}
		}
		
		private boolean shouldInitialiseModule(Class<? extends RexyModule> moduleClass) {
			return !isAbstract(moduleClass.getModifiers())
					&& !isInterface(moduleClass.getModifiers())
					&& !isPrivate(moduleClass.getModifiers());
		}
	}
	
}
