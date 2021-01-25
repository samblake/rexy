package com.github.samblake.rexy.doclet.visitor;

import org.apache.commons.lang3.tuple.Pair;

import javax.lang.model.element.Element;

public class CombinedElementVisitor implements PairElementVisitor {

    private final SingleElementVisitor left;
    private final SingleElementVisitor right;

    public CombinedElementVisitor(SingleElementVisitor left, SingleElementVisitor right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void visit(Element element) {
        left.visit(element);
        right.visit(element);
    }

    @Override
    public Pair<Element, Element> getResult() {
        return Pair.of(left.getResult(), right.getResult());
    }

}
