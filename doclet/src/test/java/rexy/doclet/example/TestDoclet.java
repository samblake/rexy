package rexy.doclet.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rexy.doclet.RexyDoclet;
import rexy.doclet.generator.SubsectionGenerator;
import rexy.doclet.visitor.ElementVisitor;
import rexy.doclet.visitor.MultiElementVisitor;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.DocumentationTool;
import javax.tools.ToolProvider;
import java.nio.file.Path;
import java.util.List;

public class TestDoclet extends RexyDoclet {

    public TestDoclet() {
        super(new SubsectionGenerator("Test", "rexy.doclet.example", new ParentVisitor()));
    }

    public static void main(String[] args) {
        String[] docletArgs = new String[]{
                "-doclet", TestDoclet.class.getName(),
                "-docletpath", "doclet/target/test-classes",
                "-sourcepath", "doclet/src/test/java",
                "rexy.doclet.example"
        };
        DocumentationTool docTool = ToolProvider.getSystemDocumentationTool();
        docTool.run(System.in, System.out, System.err, docletArgs);
    }

    @Override
    protected void copyResources(Path path) {
    }

    public static class ParentVisitor extends ElementVisitor.ConcreteClassVisitor implements MultiElementVisitor {
        private static final Logger logger = LoggerFactory.getLogger(ParentVisitor.class);

        private List<Element> parent;

        @Override
        protected void process(TypeElement element) {
            if (parent == null && element.toString().equals(Parent.class.getName())) {
                logger.info("Found it!");
                parent = List.of(element);
            }
        }

        @Override
        public List<Element> getResult() {
            return parent;
        }

    }

}
