package rexy.doclet.example;

import j2html.tags.DomContent;
import j2html.tags.Text;
import jdk.javadoc.doclet.DocletEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rexy.doclet.RexyDoclet;
import rexy.doclet.Section;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static j2html.TagCreator.*;
import static rexy.doclet.Utils.hasInterface;
import static rexy.doclet.generator.ConfigGenerator.JSON_PROPERTY;
import static rexy.doclet.generator.ConfigGenerator.JSON_PROPERTY_ORDER;

public class TestDoclet extends RexyDoclet {

    public TestDoclet() {
        super(gen);
    }

    private static final VisitingGenerator<?,?> gen = new VisitingGenerator<Element, ParentVisitor>(new ParentVisitor()) {

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

        private List<Table> processElement(DocletEnvironment environment, Element parent, String name) {
            List<Table> tables = new ArrayList<>();

            String docs = generateDocs(environment.getDocTrees(), parent);
            Table table = new Table(name, docs);
            tables.add(table);

            for (Element element : parent.getEnclosedElements()) {
                if (element.getKind().isField() && element instanceof VariableElement) {
                    findAnnotation(element, JSON_PROPERTY).ifPresent(a -> {
                        Element fieldElement = asElement(environment, element);
                        if (fieldElement != null) {
                            String fieldName = findName(fieldElement, a);
                            String fieldDocs = generateDocs(environment.getDocTrees(), element);

                            if (hasInterface(((TypeElement) fieldElement), List.class)) {
                                Element fieldKind = getTypeParameter(environment, element, 0);
                                DomContent[] fieldType = new DomContent[]{text("List of "), getType(fieldKind)};
                                table.addRow(fieldName, fieldType, fieldDocs);
                                tables.addAll(processElement(environment, fieldKind, getTypeName(fieldKind)));
                            } else if (hasInterface(((TypeElement) fieldElement), Map.class)) {
                                Element fieldKind = getTypeParameter(environment, element, 1);
                                DomContent[] fieldType = new DomContent[]{text("Map of "), getType(fieldKind)};
                                table.addRow(fieldName, fieldType, fieldDocs);
                                tables.addAll(processElement(environment, fieldKind, getTypeName(fieldKind)));
                            } else {
                                DomContent fieldType = getType(fieldElement);
                                table.addRow(fieldName, fieldType, fieldDocs);
                                tables.addAll(processElement(environment, element, findName(fieldElement, a)));
                            }
                        }
                    });
                }
            }

            return tables;
        }

        private Element asElement(DocletEnvironment environment, Element element) {
            return environment.getTypeUtils().asElement(element.asType());
        }

        private Element getTypeParameter(DocletEnvironment environment, Element element, int i) {
            TypeMirror listOf = ((DeclaredType) element.asType()).getTypeArguments().get(i);
            return environment.getTypeUtils().asElement(listOf);
        }

        private DomContent getType(Element fieldElement) {
            String name = getTypeName(fieldElement);

            if (findAnnotation(fieldElement, JSON_PROPERTY_ORDER).isPresent()) {
                String href = "#" + "test".toLowerCase() + "-" + fieldElement.getSimpleName().toString().toLowerCase();
                return a().withHref(href).withText(name);
            }

            return new Text(name);
        }

        public String getTypeName(Element fieldElement) {
            String fieldType = fieldElement.getSimpleName().toString();
            return fieldType.equals("JsonNode") ? "Custom JSON" : fieldType;
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

        public String render(Table table) {
            return table(
                tr(
                    th("Name"),
                    th("Type"),
                    th("Description")
                )
            )
            .with(table.rows.stream().map(r -> tr(
                td(r.name),
                td(r.type),
                td(r.description == null ? "" : r.description)
            )))
            .render();
        }
    };

    private static class Table {
        private final String name;
        private final String description;
        private final List<Table.Row> rows = new ArrayList<>();

        public Table(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public void addRow(String name, String type, String description) {
            rows.add(new Table.Row(name, type, description));
        }

        public void addRow(String name, DomContent type, String description) {
            rows.add(new Table.Row(name, type, description));
        }

        public void addRow(String name, DomContent[] type, String description) {
            rows.add(new Table.Row(name, type, description));
        }

        private static class Row {
            private final String name;
            private final DomContent[] type;
            private final String description;

            public Row(String name, String type, String description) {
                this(name, new Text(type), description);
            }

            public Row(String name, DomContent type, String description) {
                this(name, new DomContent[] { type }, description);
            }

            public Row(String name, DomContent[] type, String description) {
                this.name = name;
                this.type = type;
                this.description = description;
            }
        }
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
