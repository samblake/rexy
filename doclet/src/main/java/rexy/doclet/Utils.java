package rexy.doclet;

import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;

public class Utils {

    private Utils() {
    }

    public static String addSpaces(String camelCase) {
        return join(splitByCharacterTypeCamelCase(camelCase), " ");
    }

}