package com.github.samblake.rexy.doclet.generator;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.LiteralTree;
import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.DocletEnvironment;
import com.github.samblake.rexy.doclet.Section;
import com.github.samblake.rexy.doclet.visitor.ResultVisitor;

import javax.lang.model.element.Element;
import java.util.Optional;

import static java.lang.System.lineSeparator;
import static java.util.Arrays.copyOfRange;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

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
        return docCommentTree == null ? "" : docCommentTree.getFullBody().stream()
            .map(this::processDoc)
            .collect(joining());
    }

    private String processDoc(DocTree c) {
        switch (c.getKind()) {
            case CODE:
                return processCode((LiteralTree) c);
            case LINK:
                return processLink((LiteralTree) c);
            default:
                return c.toString();
        }
    }

    private String processLink(LiteralTree link) {
        return link.getBody().toString();
    }

    private String processCode(LiteralTree code) {
        String trimmed = trimCode(code.getBody().toString());
        String[] lines = trimmed.split("\\r?\\n");
        int indent = findIndent(lines[0]);
        String collect = stream(lines).map(s -> s.substring(indent)).collect(joining(lineSeparator()));
        return "<code>" + collect + "</code>";
    }

    private int findIndent(String line) {
        char[] chars = line.toCharArray();
        int length = chars.length - 1;
        int indent = 0;
        while (indent < length && chars[indent] <= 32) {
            indent++;
        }
        return indent;
    }

    private String trimCode(String value) {
        char[] chars = value.toCharArray();
        int length = chars.length - 1;

        int start = 0;
        while (start < length && chars[start] == 10 || chars[start] == 13) {
            start++;
        }

        int end = length;
        while (end > start && chars[end] <= 32) {
            end--;
        }

        return start > 0 || end < length ?new String(copyOfRange(chars, start, end + 1)) : value;
    }

}