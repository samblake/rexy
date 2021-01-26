package com.github.samblake.rexy.doclet.generator;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import jdk.javadoc.doclet.DocletEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.samblake.rexy.doclet.Section;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
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
            return Optional.of(renderReadme(title, true));
        }
        catch (IOException e) {
            logger.warn("Could not generate readme");
            return Optional.empty();
        }
    }

    private Section renderReadme(String title, boolean removeFirstSection) throws IOException {
        Section section = null;
        StringBuilder content = new StringBuilder();

        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        try (FileReader reader = new FileReader(path)) {
            Node document = parser.parseReader(reader);
            Iterator<Node> it = document.getChildIterator();
            while (it.hasNext()) {
                Node node = it.next();
                if (node.isOrDescendantOfType(Heading.class)) {
                    if (section == null) {
                        section = new Section(title, content.toString());
                    }
                    else {
                        section.addSubsection(new Section(title, content.toString()));
                    }

                    title = ((Heading) node).getText().toString();
                    content = new StringBuilder();
                }
                else {
                    content.append(renderer.render(node));
                }
            }

            if (section == null) {
                section = new Section(title, content.toString());
            }
            else {
                section.addSubsection(new Section(title, content.toString()));
            }

            if (removeFirstSection && !section.getSubsections().isEmpty()) {
                section.getSubsections().remove(0);
            }

            return section;
        }
    }

}