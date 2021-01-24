package rexy.doclet.generator;

import jdk.javadoc.doclet.DocletEnvironment;
import rexy.doclet.Section;

import java.util.Optional;

public interface Generator {

    Optional<Section> generate(DocletEnvironment environment);

}
