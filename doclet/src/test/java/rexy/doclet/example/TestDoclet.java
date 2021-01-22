package rexy.doclet.example;

import jdk.javadoc.doclet.DocletEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rexy.doclet.RexyDoclet;
import rexy.doclet.Section;
import rexy.doclet.Utils;
import rexy.doclet.generator.VisitingGenerator;
import rexy.doclet.visitor.ElementVisitor;
import rexy.doclet.visitor.SingleElementVisitor;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.DocumentationTool;
import javax.tools.ToolProvider;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static rexy.doclet.Utils.hasInterface;

public class TestDoclet extends RexyDoclet {

    public TestDoclet() {
        super(new VisitingGenerator<>(new ParentVisitor()) {

            private Set<String> found = new HashSet<>();

            @Override
            protected Optional<Section> generateSection(DocletEnvironment environment, Element result) {
                if (result != null) {
                    String docs = generateDocs(environment.getDocTrees(), result);
                    Section section = new Section("Test", docs);
                    processElement(environment, result, "Root");
                    return Optional.of(section);
                }
                return Optional.empty();
            }

            private void processElement(DocletEnvironment environment, Element parent, String fieldName) {
                found.add(fieldName);
                System.out.println(fieldName);

                for (Element element : parent.getEnclosedElements()) {
                    if (element.getKind().isField() && element instanceof VariableElement) {
                        findAnnotation(element,"com.fasterxml.jackson.annotation.JsonProperty").ifPresent(a -> {
                            Element fieldElement = environment.getTypeUtils().asElement(element.asType());
                            if (fieldElement != null) {
                                String fieldType = fieldElement.getSimpleName().toString();
                                String fieldDocs = generateDocs(environment.getDocTrees(), fieldElement);

                                if (hasInterface(((TypeElement) fieldElement), "java.util.List")) {
                                    TypeMirror listOf = ((DeclaredType) element.asType()).getTypeArguments().get(0);
                                    Element fieldKind = environment.getTypeUtils().asElement(listOf);
                                    processElement(environment, fieldKind, findName(fieldKind, a));
                                }
                                else if (hasInterface(((TypeElement) fieldElement), "java.util.Map")) {
                                    TypeMirror mapOf = ((DeclaredType) element.asType()).getTypeArguments().get(1);
                                    Element fieldKind = environment.getTypeUtils().asElement(mapOf);
                                    processElement(environment, fieldKind, findName(fieldKind, a));
                                }
                                else {
                                    processElement(environment, element, findName(fieldElement, a));
                                }
                            }
                        });
                    }
                }
            }

            private String findName(Element element, AnnotationMirror a) {
                return a.getElementValues().entrySet().stream()
                        .filter(e -> e.getKey().getSimpleName().toString().equals("value"))
                        .findAny().map(e -> e.getValue().getValue().toString())
                        .orElse(element.getSimpleName().toString());
            }

            private Optional<? extends AnnotationMirror> findAnnotation(Element element, String annotation) {
                return element.getAnnotationMirrors().stream()
                        .filter(a -> a.getAnnotationType().toString().equals(annotation))
                        .findAny();
            }
        });
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

    public static class ParentVisitor extends ElementVisitor.ConcreteClassVisitor implements SingleElementVisitor {
        private static final Logger logger = LoggerFactory.getLogger(ParentVisitor.class);

        private Element parent;

        @Override
        protected void process(TypeElement element) {
            if (parent == null && element.toString().equals(Parent.class.getName())) {
                logger.info("Found it!");
                parent = element;
            }
        }

        @Override
        public Element getResult() {
            return parent;
        }

    }

}
