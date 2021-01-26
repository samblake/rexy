package com.github.samblake.rexy.doclet.visitor;

import javax.lang.model.element.TypeElement;

import static com.github.samblake.rexy.doclet.Constants.CONFIG_INTERFACE;

public class ConfigVisitor extends ElementVisitor.ConcreteClassVisitor implements SingleElementVisitor {

    private TypeElement config;

    @Override
    protected void process(TypeElement element) {
        if (config == null && element.toString().equals(CONFIG_INTERFACE)) {
            config = element;
        }
    }

    @Override
    public TypeElement getResult() {
        return config;
    }

}