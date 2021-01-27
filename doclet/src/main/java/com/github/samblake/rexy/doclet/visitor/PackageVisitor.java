package com.github.samblake.rexy.doclet.visitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;

public class PackageVisitor extends ElementVisitor.PackageVisitor implements SingleElementVisitor {
    private static final Logger logger = LogManager.getLogger(PackageVisitor.class);

    private final String name;
    private Element pkg;

    public PackageVisitor(Class<?> clazz) {
        this(clazz.getPackageName());
    }

    public PackageVisitor(String name) {
        this.name = name;
    }

    @Override
    protected void process(PackageElement element) {
        if (pkg == null && element.toString().equals(name)) {
            pkg = element;
        }
    }

    @Override
    public Element getResult() {
        return pkg;
    }

}