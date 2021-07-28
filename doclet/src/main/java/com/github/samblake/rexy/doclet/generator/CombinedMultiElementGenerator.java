package com.github.samblake.rexy.doclet.generator;

import com.github.samblake.rexy.doclet.Section;
import com.github.samblake.rexy.doclet.visitor.MultiElementVisitor;
import jdk.javadoc.doclet.DocletEnvironment;

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