package com.github.samblake.rexy.doclet;

import com.github.samblake.rexy.doclet.generator.*;
import com.github.samblake.rexy.doclet.visitor.*;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rexy.doclet.generator.*;
import rexy.doclet.visitor.*;

import javax.lang.model.SourceVersion;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class RexyDoclet implements Doclet {
    private static final Logger logger = LoggerFactory.getLogger(RexyDoclet.class);

    private static final String DEFAULT_MARKDOWN_PATH = "../../../../../README.md";

    private static final VisitingGenerator<?,?> CONFIGURATION_GENERATOR = new ConfigGenerator(
            "Configuration", new CombinedElementVisitor(new PackageVisitor("rexy.config"), new ConfigVisitor()));
    private static final VisitingGenerator<?,?> MODULE_GENERATOR = new SubsectionGenerator(
            "Modules", "rexy.module", new ModuleVisitor());
    private static final VisitingGenerator<?,?> MATCHER_GENERATOR = new SubsectionGenerator(
            "Matchers", "rexy.module.jmx.matcher", new RequestMatcherVisitor());

    private String name;
    private String version;
    private String headline;
    private String markdownPath;

    private final Generator[] generators;

    public RexyDoclet() {
        this(CONFIGURATION_GENERATOR, MODULE_GENERATOR, MATCHER_GENERATOR);
    }

    public RexyDoclet(Generator... generators) {
        this.generators = generators;
    }

    @Override
    public void init(Locale locale, Reporter reporter) {
        logger.info("Generating Rexy doc");
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
        logger.info("Generating Rexy doclet");

        Map<String, Object> context = new HashMap<>();
        context.put("name", name);
        context.put("version", version);
        context.put("headline", headline);

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
            copyResources(Paths.get("../../../../../doclet/src/main/resources/template"));
        }
        catch (IOException e) {
            logger.error("Could not generate docs", e);
            return false;
        }

        return true;
    }

    private void writeTemplate(File outputFile, Map<String, Object> context) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
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