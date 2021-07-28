package com.github.samblake.rexy.doclet.generator;

import com.github.samblake.rexy.doclet.Section;
import com.github.samblake.rexy.doclet.visitor.MultiElementVisitor;
import jdk.javadoc.doclet.DocletEnvironment;

import javax.lang.model.element.Element;
import java.util.List;
import java.util.Optional;

import static com.github.samblake.rexy.doclet.Utils.addSpaces;

public abstract class MultiElementGenerator extends VisitingGenerator<List<Element>, MultiElementVisitor> {

    public MultiElementGenerator(MultiElementVisitor visitor) {
        super(visitor);
    }

    @Override
    protected Optional<Section> generateSection(DocletEnvironment environment, List<Element> result) {
        Optional<Section> section = createTopLevelSection(environment);
        section.ifPresent(s -> {
            for (Element element : visitor.getResult()) {
                String name = addSpaces(element.getSimpleName().toString());
                String docs = generateDocs(environment.getDocTrees(), element);
                if (docs != null) {
                    s.addSubsection(new Section(name, docs));
                }
            }
        });
        return section;
    }

    protected abstract Optional<Section> createTopLevelSection(DocletEnvironment environment);

}