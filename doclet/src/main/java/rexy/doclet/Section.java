package rexy.doclet;

import java.util.ArrayList;
import java.util.List;

public class Section {

    private final String title;
    private final String content;
    private final List<Subsection> subsections = new ArrayList<>();

    public Section(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public List<Subsection> getSubsections() {
        return subsections;
    }

    public Section addSubsection(Section section) {
        subsections.add(new Subsection(section.title, section.content));
        return this;
    }

    public String getUrlSegment() {
        return title.toLowerCase().replaceAll(" ", "-");
    }

    private class Subsection extends Section {

        public Subsection(String title, String content) {
            super(title, content);
        }

        @Override
        public String getUrlSegment() {
            return Section.this.getUrlSegment() + "-" + super.getUrlSegment();
        }
    }

}