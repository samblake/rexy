package rexy.doclet.generator;

import jdk.javadoc.doclet.DocletEnvironment;
import rexy.doclet.Section;
import rexy.doclet.visitor.MultiElementVisitor;

import java.util.Optional;

public class CombinedMultiElementGenerator extends MultiElementGenerator {

    private final ElementGenerator generator;

    public CombinedMultiElementGenerator(ElementGenerator generator, MultiElementVisitor visitor) {
        super(visitor);
        this.generator = generator;
    }

    @Override
    protected Optional<Section> createTopLevelSection(DocletEnvironment environment) {
        return generator.generate(environment);
    }

}