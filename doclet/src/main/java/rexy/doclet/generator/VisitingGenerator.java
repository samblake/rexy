package rexy.doclet.generator;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.DocletEnvironment;
import rexy.doclet.Section;
import rexy.doclet.visitor.ResultVisitor;

import javax.lang.model.element.Element;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.join;

public abstract class VisitingGenerator<T, V extends ResultVisitor<T>> implements Generator {

    protected final V visitor;

    public VisitingGenerator(V visitor) {
        this.visitor = visitor;
    }

    @Override
    public Optional<Section> generate(DocletEnvironment environment) {
        for (Element element : environment.getIncludedElements()) {
            visitor.visit(element);
        }
        return generateSection(environment, visitor.getResult());
    }

    protected abstract Optional<Section> generateSection(DocletEnvironment environment, T result);

    protected String generateDocs(DocTrees docTrees, Element element) {
        DocCommentTree docCommentTree = docTrees.getDocCommentTree(element);
        return docCommentTree == null ? null : join(docCommentTree.getFullBody(), "");
    }

}
