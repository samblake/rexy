package com.github.samblake.rexy.doclet.generator;

import jdk.javadoc.doclet.DocletEnvironment;
import com.github.samblake.rexy.doclet.Section;
import com.github.samblake.rexy.doclet.visitor.SingleElementVisitor;

import javax.lang.model.element.Element;
import java.util.Optional;

public class ElementGenerator extends VisitingGenerator<Element, SingleElementVisitor> {

    private final String title;

    public ElementGenerator(String title, SingleElementVisitor visitor) {
        super(visitor);
        this.title = title;
    }

    @Override
    protected Optional<Section> generateSection(DocletEnvironment environment, Element element) {
        if (element != null) {
            String docs = generateDocs(environment.getDocTrees(), element);
            return docs == null ? Optional.empty() : Optional.of(new Section(title, docs));
        }
        return Optional.empty();
    }

}