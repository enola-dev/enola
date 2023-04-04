package dev.enola.core.docgen;

public class StringUtil {

    public static String capitalize(String string) {
        if (string == null) {
            return null;
        }
        if (string.isEmpty()) {
            return string;
        }
        if (string.length() == 1) {
            return string.toUpperCase();
        }

        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
