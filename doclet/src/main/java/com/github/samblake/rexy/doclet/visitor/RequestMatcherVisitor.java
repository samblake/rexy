package com.github.samblake.rexy.doclet.visitor;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;

import static com.github.samblake.rexy.doclet.Constants.INTERNAL_ANNOTATION;
import static com.github.samblake.rexy.doclet.Constants.MATCHER_INTERFACE;
import static com.github.samblake.rexy.doclet.Utils.hasAnnotation;

public class RequestMatcherVisitor extends ElementVisitor.ConcreteClassVisitor implements MultiElementVisitor {

    private final List<Element> requestMatchers = new ArrayList<>();

    @Override
    protected void process(TypeElement element) {
        if (hasInterface(element, MATCHER_INTERFACE) && !hasAnnotation(element, INTERNAL_ANNOTATION)) {
            requestMatchers.add(element);
        }
    }

    @Override
    public List<Element> getResult() {
        return requestMatchers;
    }
    
}