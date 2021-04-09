package com.github.samblake.rexy.doclet;

import com.github.samblake.rexy.doclet.generator.*;
import com.github.samblake.rexy.doclet.visitor.*;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.lang.model.SourceVersion;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.github.samblake.rexy.doclet.Constants.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class RexyDoclet implements Doclet {
    private static final Logger logger = LogManager.getLogger(RexyDoclet.class);

    private static final String BASE = "../../../../../";

    private static final String DEFAULT_MARKDOWN_PATH = BASE + "README.md";

    private static final Generator MODULE_GENERATOR = new SubsectionGenerator(
            "Modules", MODULE_PACKAGE, new ModuleVisitor());
    private static final Generator MATCHER_GENERATOR = new SubsectionGenerator(
            "Matchers", MATCHER_PACKAGE, new RequestMatcherVisitor());

    private static final Generator JAVA_CONFIG_GENERATOR = new ConfigGenerator(
            "Configuration", new CombinedElementVisitor(new PackageVisitor(CONFIG_PACKAGE), new ConfigVisitor()));
    private static final Generator CONFIG_GENERATOR = new CombiningGenerator(JAVA_CONFIG_GENERATOR,
            new FileIncludeGenerator(BASE + "example-config.json"),
            new FileIncludeGenerator(BASE + "example-import.json"),
            new FileIncludeGenerator(BASE + "example-body.json"));

    private String name;
    private String version;
    private String headline;
    private String url;
    private String markdownPath;

    private final Generator[] generators;

    public RexyDoclet() {
        this(CONFIG_GENERATOR, MODULE_GENERATOR, MATCHER_GENERATOR);
    }

    public RexyDoclet(Generator... generators) {
        this.generators = generators;
    }

    @Override
    public void init(Locale locale, Reporter reporter) {
        logger.info("Generating Rexy docs");
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public Set<? extends RexyOption> getSupportedOptions() {
        return Set.of(
            new RexyOption("name", "application name") {
                @Override
                public boolean process(String option, List<String> arguments) {
                    if (arguments.isEmpty()) {
                        // TODO reporter
                        return false;
                    }

                    name = arguments.get(0);
                    return true;
                }
            },
            new RexyOption("ver", "application version") {
                @Override
                public boolean process(String option, List<String> arguments) {
                    if (arguments.isEmpty()) {
                        // TODO reporter
                        return false;
                    }

                    version = arguments.get(0);
                    return true;
                }
            },
            new RexyOption("headline", "headline description") {
                @Override
                public boolean process(String option, List<String> arguments) {
                    headline = arguments == null ? null : arguments.get(0);
                    return true;
                }
            },
            new RexyOption("url", "project url") {
                @Override
                public boolean process(String option, List<String> arguments) {
                    url = arguments == null ? null : arguments.get(0);
                    return true;
                }
            },
            new RexyOption("markdown", "markdown location") {
                @Override
                public boolean process(String option, List<String> arguments) {
                    markdownPath = arguments == null ? null : arguments.get(0);
                    return true;
                }
            }
        );
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean run(DocletEnvironment environment) {
        Map<String, Object> context = new HashMap<>();
        context.put("name", name);
        context.put("version", version);
        context.put("headline", headline);
        context.put("url", url);

        List<Generator> generators = new ArrayList<>();
        String path = markdownPath == null ? DEFAULT_MARKDOWN_PATH : markdownPath;
        generators.add(new ReadmeGenerator("Rexy", path));
        generators.addAll(asList(this.generators));

        List<Section> sections = generators.stream()
                .map(g -> g.generate(environment))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());

        context.put("sections", sections);

        try {
            File file = new File("index.html");
            writeTemplate(file, context);
            copyResources(Paths.get(BASE + "doclet/src/main/resources/template"));
        }
        catch (IOException e) {
            logger.error("Could not generate docs", e);
            return false;
        }

        return true;
    }

    private void writeTemplate(File outputFile, Map<String, Object> context) throws IOException {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(outputFile), UTF_8)) {
            PebbleEngine engine = new PebbleEngine.Builder().build();
            PebbleTemplate compiledTemplate = engine.getTemplate("template/index.peb");
            compiledTemplate.evaluate(writer, context);
        }
    }

    protected void copyResources(Path path) throws IOException {
        Files.walk(path).forEach(source -> {
            if (!source.toString().endsWith("/template") && !source.toString().endsWith(".peb")) {
                String destination = source.toString().substring(source.toString().indexOf("/template") + 10);
                try {
                    Files.copy(source, Paths.get(destination));
                }
                catch (Exception e) {
                    logger.error("Unable to copy template resources: " + e.getMessage());
                }
            }
        });
    }

}