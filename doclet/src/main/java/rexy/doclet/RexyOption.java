package rexy.doclet;

import jdk.javadoc.doclet.Doclet;

import java.util.List;

abstract class RexyOption implements Doclet.Option {
    private final String name;
    private final String description;

    RexyOption(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public int getArgumentCount() {
        return 1;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Kind getKind() {
        return Kind.STANDARD;
    }

    @Override
    public List<String> getNames() {
        return List.of("--" + name);
    }

    @Override
    public String getParameters() {
        return "<" + name + ">";
    }

}