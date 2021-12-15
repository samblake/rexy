package com.github.samblake.rexy.doclet.generator;

import com.github.samblake.rexy.doclet.Section;
import jdk.javadoc.doclet.DocletEnvironment;

import java.util.Optional;

public interface Generator {

    Optional<Section> generate(DocletEnvironment environment);

}
