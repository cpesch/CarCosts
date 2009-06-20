package slash.util;

import java.text.MessageFormat;

public class Util {

    /**
     * convenience method to create a formatted string
     */
    public static String formatString(String pattern, Object[] o) {
        return MessageFormat.format(pattern, o);
    }
}
