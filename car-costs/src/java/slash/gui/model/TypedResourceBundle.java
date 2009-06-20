package slash.gui.model;

import javax.swing.*;
import java.net.URL;
import java.util.Enumeration;

/**
 * This interface encapsulates a ResourceBundle by returning different types of
 * ressources (Strings, ImageIcons, etc.) in a typesafe way.
 */

public interface TypedResourceBundle {

    /**
     * Lookup resource entry with the specified name and return it.
     * If no entry with the name exists, null is returned
     *
     * @param key the resource entry to lookup
     * @return a string which was looked up
     */
    public String getString(String key);

    /**
     * Lookup resource entry with the specified name through the class passed
     * to the constructor and return an url for it.
     * If no entry with the name exists, null is returned
     * If the url cannot be found, a console message is printed and null is returned
     *
     * @param key the resource entry to lookup
     * @return an URL which was looked up
     */
    public URL getResource(String key);

    /**
     * Lookup resource entry with the specified name and return an url for it.
     * If no entry with the name exists, null is returned
     * If the url cannot be found, a console message is printed and null is returned
     *
     * @param key the resource entry to lookup
     * @return an URL which was looked up
     */
    public URL getURL(String key);

    /**
     * Lookup resource entry with the specified name and return an ImageIcon for
     * it. If no entry with the name exists, null is returned.
     *
     * @param key the resource entry to lookup
     * @return an ImageIcon which was looked up
     */
    public ImageIcon getIcon(String key);

    /**
     * Gives all the keys of this ResourceBundle.
     *
     * @return an Enumeration of keys
     */
    public Enumeration getKeys();
}
