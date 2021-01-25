package com.github.samblake.rexy.doclet.visitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.samblake.rexy.doclet.RexyDoclet;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;

public class RequestMatcherVisitor extends ElementVisitor.ConcreteClassVisitor implements MultiElementVisitor {
    private static final Logger logger = LoggerFactory.getLogger(RexyDoclet.class);

    private final List<Element> requestMatchers = new ArrayList<>();

    @Override
    protected void process(TypeElement element) {
        if (hasInterface(element, "rexy.module.jmx.matcher.RequestMatcher")) {
            logger.info("Found matcher: " + element);
            requestMatchers.add(element);
        }
    }

    @Override
    public List<Element> getResult() {
        return requestMatchers;
    }

}