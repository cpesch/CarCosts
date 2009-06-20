package slash.util;

import java.io.File;

/**
 * Some useful methods to create and manipulate File objects
 */
public class Files {

    /**
     * Returns the extension of the file, which are the characters
     * behind the last dot in the file name.
     */
    public static String getExtension(File file) {
        String name = file.getName();
        int index = name.lastIndexOf(".");
        if (index == -1)
            return "";
        return name.substring(index + 1, name.length());
    }
}
