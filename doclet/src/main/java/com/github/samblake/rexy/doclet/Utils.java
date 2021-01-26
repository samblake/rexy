package com.github.samblake.rexy.doclet;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;

public class Utils {

    private Utils() {
    }

    public static String addSpaces(String camelCase) {
        return join(splitByCharacterTypeCamelCase(camelCase), " ");
    }

    public static boolean hasInterface(TypeElement element, Class<?> clazz) {
        return hasInterface(element, clazz.getName());
    }

    public static boolean hasInterface(TypeElement element, String className) {
        if (element.toString().equals(className)) {
            return true;
        }

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