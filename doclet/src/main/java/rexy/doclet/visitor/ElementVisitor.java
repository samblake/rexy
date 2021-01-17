package rexy.doclet.visitor;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import static javax.lang.model.element.ElementKind.PACKAGE;
import static javax.lang.model.element.Modifier.ABSTRACT;

public interface ElementVisitor {

    void visit(Element element);

    abstract class FilteringVisitor<T extends Element> implements ElementVisitor {

        @Override
        public void visit(Element element) {
            if (filter(element)) {
                process((T)element);
            }
        }

        protected abstract boolean filter(Element element);

        protected abstract void process(T element);

    }

    abstract class TypeElementVisitor extends FilteringVisitor<TypeElement> {

        @Override
        protected boolean filter(Element element) {
            return element instanceof TypeElement;
        }

        protected boolean hasInterface(TypeElement element, String className) {
            for (TypeMirror inter : element.getInterfaces()) {
                if (inter instanceof DeclaredType) {
                    if (((DeclaredType)inter).asElement().toString().equals(className)) {
                        return true;
                    }
                }
            }

            TypeMirror superclass = element.getSuperclass();
            if (superclass instanceof DeclaredType) {
                Element superElement = ((DeclaredType) superclass).asElement();
                if (superElement instanceof TypeElement) {
                    return hasInterface((TypeElement)superElement, className);
                }
            }

            return false;
        }

    }

    abstract class ConcreteClassVisitor extends TypeElementVisitor {

        @Override
        protected boolean filter(Element element) {
            return element.getKind().isClass()
                    && !element.getModifiers().contains(ABSTRACT)
                    && super.filter(element);
        }

    }

    abstract class PackageVisitor extends FilteringVisitor<PackageElement> {

        @Override
        protected boolean filter(Element element) {
            return element.getKind().equals(PACKAGE) && element instanceof PackageElement;
        }

    }

}