package rexy.doclet.generator;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import jdk.javadoc.doclet.DocletEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rexy.doclet.Section;

import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

public class ReadmeGenerator implements Generator {
    private static final Logger logger = LoggerFactory.getLogger(ReadmeGenerator.class);

    private final String title;
    private final String path;

    public ReadmeGenerator(String title, String path) {
        this.title = title;
        this.path = path;
    }

    @Override
    public Optional<Section> generate(DocletEnvironment environment) {
        try {
            return Optional.of(new Section(title, renderReadme()));
        }
        catch (IOException e) {
            logger.warn("Could not generate readme");
            return Optional.empty();
        }
    }

    private String renderReadme() throws IOException {
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        try (FileReader reader = new FileReader(path)) {
            Node document = parser.parseReader(reader);
            return renderer.render(document);
        }
    }

}