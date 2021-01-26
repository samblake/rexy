package com.github.samblake.rexy.doclet.visitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.samblake.rexy.doclet.RexyDoclet;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;

import static com.github.samblake.rexy.doclet.Constants.MODULE_INTERFACE;

public class ModuleVisitor extends ElementVisitor.ConcreteClassVisitor implements MultiElementVisitor {
    private static final Logger logger = LoggerFactory.getLogger(RexyDoclet.class);

    private final List<Element> modules = new ArrayList<>();

    @Override
    protected void process(TypeElement element) {
        if (hasInterface(element, MODULE_INTERFACE)) {
            logger.info("Found module: " + element);
            modules.add(element);
        }
    }

    @Override
    public List<Element> getResult() {
        return modules;
    }

}