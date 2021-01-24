package rexy.doclet.generator;

import jdk.javadoc.doclet.DocletEnvironment;
import org.apache.commons.lang3.tuple.Pair;
import rexy.doclet.Section;
import rexy.doclet.visitor.CombinedElementVisitor;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static j2html.TagCreator.*;
import static rexy.doclet.Utils.hasInterface;

public class ConfigGenerator extends VisitingGenerator<Pair<Element, Element>, CombinedElementVisitor> {

    private final String title;

    public ConfigGenerator(String title, CombinedElementVisitor visitor) {
        super(visitor);
        this.title = title;
    }

    @Override
    protected Optional<Section> generateSection(DocletEnvironment environment, Pair<Element, Element> result) {
        if (result.getLeft() != null) {
            String docs = generateDocs(environment.getDocTrees(), result.getLeft());
            if (docs != null) {
                Section section = new Section(title, docs);
                if (result.getRight() != null) {
                    List<Table> tables = processElement(environment, result.getRight(), "Root");

                    tables.stream()
                            .filter(t -> t.rows.size() > 0)
                            .map(t -> new Section(t.name, t.description + render(t)))
                            .forEach(section::addSubsection);
                }
                return Optional.of(section);
            }
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
                findAnnotation(element,"com.fasterxml.jackson.annotation.JsonProperty").ifPresent(a -> {
                    Element fieldElement = asElement(environment, element);
                    if (fieldElement != null) {
                        String fieldName = findName(fieldElement, a);
                        String fieldDocs = generateDocs(environment.getDocTrees(), element);

                        if (hasInterface(((TypeElement) fieldElement), "java.util.List")) {
                            Element fieldKind = getTypeParameter(environment, element, 0);
                            String fieldType = "List of " + getTypeName(fieldKind);
                            table.addRow(fieldName, fieldType, fieldDocs);
                            tables.addAll(processElement(environment, fieldKind, findName(fieldKind, a)));
                        }
                        else if (hasInterface(((TypeElement) fieldElement), "java.util.Map")) {
                            Element fieldKind = getTypeParameter(environment, element, 1);
                            String fieldType = "Map of " + getTypeName(fieldKind);
                            table.addRow(fieldName, fieldType, fieldDocs);
                            tables.addAll(processElement(environment, fieldKind, findName(fieldKind, a)));
                        }
                        else {
                            String fieldType = getTypeName(fieldElement);
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

    private String getTypeName(Element fieldElement) {
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

    private static class Table {
        private final String name;
        private final String description;
        private final List<Row> rows = new ArrayList<>();

        public Table(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public void addRow(String name, String type, String description) {
            rows.add(new Row(name, type, description));
        }

        private static class Row {
            private final String name;
            private final String type;
            private final String description;

            public Row(String name, String type, String description) {
                this.name = name;
                this.type = type;
                this.description = description;
            }
        }
    }

}