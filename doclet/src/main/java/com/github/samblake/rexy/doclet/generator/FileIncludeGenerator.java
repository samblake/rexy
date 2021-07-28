package com.github.samblake.rexy.doclet.generator;

import com.github.samblake.rexy.doclet.Section;
import jdk.javadoc.doclet.DocletEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static com.github.samblake.rexy.doclet.Utils.removeFileExtension;
import static com.github.samblake.rexy.doclet.Utils.replaceConcat;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readAllBytes;

public class FileIncludeGenerator implements Generator {
    private static final Logger logger = LogManager.getLogger(FileIncludeGenerator.class);

    private final String title;
    private final Path path;
    private final boolean outputFilename;

    public FileIncludeGenerator(String title, String path, boolean outputFilename) {
        this.title = title;
        this.path = Paths.get(path);
        this.outputFilename = outputFilename;
    }

    public FileIncludeGenerator(String path) {
        this(path, true);
    }

    public FileIncludeGenerator(String path, boolean outputFilename) {
        this.path = Paths.get(path);



        this.title = replaceConcat(removeFileExtension(getFilename(this.path)));
        this.outputFilename = outputFilename;
    }

    private static String getFilename(Path path) {
        Path fileName = path.getFileName();
        return fileName == null ? path.toString() : fileName.toString();
    }

    @Override
    public Optional<Section> generate(DocletEnvironment environment) {
        try {
            return Optional.of(renderFile());
        }
        catch (IOException e) {
            logger.warn("Could not generate file: " + path);
            return Optional.empty();
        }
    }

    private Section renderFile() throws IOException {
        StringBuilder content = new StringBuilder();
        if (outputFilename) {
            content.append(getFilename(this.path));
        }

        content.append("<pre><code>")
            .append(readFile())
            .append("</code></pre>");

        return new Section(title, content.toString());
    }

    private String readFile() throws IOException {
        return new String(readAllBytes(path), UTF_8);
    }

}