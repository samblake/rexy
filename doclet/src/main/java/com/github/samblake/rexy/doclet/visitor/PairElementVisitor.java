package com.github.samblake.rexy.doclet.visitor;

import org.apache.commons.lang3.tuple.Pair;

import javax.lang.model.element.Element;

public interface PairElementVisitor extends ResultVisitor<Pair<Element, Element>> {
}