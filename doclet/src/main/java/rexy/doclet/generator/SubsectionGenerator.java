package rexy.doclet.generator;

import rexy.doclet.visitor.MultiElementVisitor;
import rexy.doclet.visitor.PackageVisitor;

public class SubsectionGenerator extends CombinedMultiElementGenerator {

    public SubsectionGenerator(String title, String packageName, MultiElementVisitor visitor) {
        super(new ElementGenerator(title, new PackageVisitor(packageName)), visitor);
    }

}