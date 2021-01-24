package rexy.doclet.visitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rexy.doclet.RexyDoclet;

import javax.lang.model.element.TypeElement;

public class ConfigVisitor extends ElementVisitor.ConcreteClassVisitor implements SingleElementVisitor {
    private static final Logger logger = LoggerFactory.getLogger(RexyDoclet.class);

    private TypeElement config;

    @Override
    protected void process(TypeElement element) {
        if (config == null && element.toString().equals("rexy.config.model.Config")) {
            logger.info("Found config: " + element);
            config = element;
        }
    }

    @Override
    public TypeElement getResult() {
        return config;
    }

}