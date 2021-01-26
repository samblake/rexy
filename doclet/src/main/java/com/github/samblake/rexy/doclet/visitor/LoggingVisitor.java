package com.github.samblake.rexy.doclet.visitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.samblake.rexy.doclet.RexyDoclet;

import javax.lang.model.element.Element;

public class LoggingVisitor implements ElementVisitor {
    private static final Logger logger = LoggerFactory.getLogger(RexyDoclet.class);

    @Override
    public void visit(Element element) {
        logger.info(element.toString());
    }

}
