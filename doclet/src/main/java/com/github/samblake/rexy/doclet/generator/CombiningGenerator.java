package com.github.samblake.rexy.doclet.generator;

import com.github.samblake.rexy.doclet.Section;
import jdk.javadoc.doclet.DocletEnvironment;

import java.util.Optional;

public class CombiningGenerator implements Generator {

    private final Generator parent;
    private final Generator[] children;

    public CombiningGenerator(Generator parent, Generator... additionalChildren) {
        this.parent = parent;
        this.children = additionalChildren;
    }

    @Override
    public Optional<Section> generate(DocletEnvironment environment) {
        Optional<Section> section = parent.generate(environment);
        if (section.isPresent()) {
            for (Generator child : children) {
                child.generate(environment).ifPresent(s -> section.get().addSubsection(s));
            }
        }
        return section;
    }

}