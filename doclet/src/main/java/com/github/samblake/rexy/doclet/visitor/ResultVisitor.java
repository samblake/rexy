package com.github.samblake.rexy.doclet.visitor;

public interface ResultVisitor<T> extends ElementVisitor {

    T getResult();

}