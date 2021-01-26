package com.github.samblake.rexy.doclet.generator;

import com.github.samblake.rexy.doclet.visitor.MultiElementVisitor;
import com.github.samblake.rexy.doclet.visitor.PackageVisitor;

public class SubsectionGenerator extends CombinedMultiElementGenerator {

    public SubsectionGenerator(String title, String packageName, MultiElementVisitor visitor) {
        super(new ElementGenerator(title, new PackageVisitor(packageName)), visitor);
    }

}