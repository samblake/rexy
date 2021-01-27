package com.github.samblake.rexy.doclet.visitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.github.samblake.rexy.doclet.RexyDoclet;

import javax.lang.model.element.Element;

public class LoggingVisitor implements ElementVisitor {
    private static final Logger logger = LogManager.getLogger(RexyDoclet.class);

    @Override
    public void visit(Element element) {
        logger.info(element.toString());
    }

}
