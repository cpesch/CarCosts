package slash.util;

import slash.gui.model.TypedResourceBundle;

import javax.swing.*;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * This class encapsulates a ResourceBundle by returning different types of
 * ressources (Strings, ImageIcons, etc.) in a typesafe way.
 * <p/>
 * SimpleTypedResourceBundle can't inherit from PropertyResourceBundle due
 * to the strange creation policy of PropertyResourceBundle and ResourceBundle.
 *
 * @author Christian Pesch
 */

public class SimpleTypedResourceBundle implements TypedResourceBundle {

    /**
     * Create a TypedResourceBundle.
     */
    public SimpleTypedResourceBundle() {
    }

    public void setClass(Class clazz) {
        this.clazz = clazz;
    }

    public void setResources(ResourceBundle resources) {
        this.resources = resources;
    }

    public void setFile(String file) {
        setResources(ResourceBundle.getBundle(file, Locale.getDefault()));
    }

    /**
     * Create a TypedResourceBundle that does lookups on <code>resources</code>
     * using the class loader of <code>clazz</code>.
     */
    public SimpleTypedResourceBundle(ResourceBundle resources, Class clazz) {
        setResources(resources);
        setClass(clazz);
    }

    /**
     * Create a TypedResourceBundle that does lookups on the
     * ResourceBundle located in the <code>file</code> using the class
     * loader of <code>clazz</code> and the given Locale.
     */
    public SimpleTypedResourceBundle(String file,
                                     Locale locale,
                                     Class clazz)
            throws MissingResourceException {
        this(ResourceBundle.getBundle(file, locale), clazz);
    }

    /**
     * Create a TypedResourceBundle that does lookups on the
     * ResourceBundle located in the <code>file</code> using the class
     * loader of <code>clazz</code> and the default Locale.
     */
    public SimpleTypedResourceBundle(String file,
                                     Class clazz)
            throws MissingResourceException {
        this(file, Locale.getDefault(), clazz);
    }

    /**
     * Create a TypedResourceBundle that does lookups on the
     * ResourceBundle located in the <code>file</code> using the class
     * loader of this SimpleTypedResourceBundle and the given Locale.
     */
    public SimpleTypedResourceBundle(String file,
                                     Locale locale)
            throws MissingResourceException {
        this(file, locale, null);
        this.clazz = getClass();
    }

    /**
     * Create a TypedResourceBundle that does lookups on the
     * ResourceBundle located in the <code>file</code> using the class
     * loader of this SimpleTypedResourceBundle and the default Locale.
     */
    public SimpleTypedResourceBundle(String file)
            throws MissingResourceException {
        this(file, Locale.getDefault());
    }

    /**
     * Lookup resource entry with the specified name and return it.
     * If no entry with the name exists, null is returned
     *
     * @param key the resource entry to lookup
     * @return a string which was looked up
     */
    public String getString(String key) {
        String str;
        try {
            str = resources.getString(key);
        } catch (MissingResourceException mre) {
            str = null;
        }
        // if (str == null) System.out.println("String not found:"+key+" in "+second);
        return str;
    }

    /**
     * Lookup resource entry with the specified name through the class passed
     * to the constructor and return an url for it.
     * If no entry with the name exists, null is returned
     * If the url cannot be found, a console message is printed and null is returned
     *
     * @param key the resource entry to lookup
     * @return an URL which was looked up
     */
    public URL getResource(String key) {
        String name = getString(key);
        if (name != null) {
            URL url = clazz.getResource(name);

            if (url == null) {
                System.out.println("Can't open resource " + key + "=" + name);
            }
            return url;
        }
        return null;
    }

    /**
     * Lookup resource entry with the specified name and return an url for it.
     * If no entry with the name exists, null is returned
     * If the url cannot be found, a console message is printed and null is returned
     *
     * @param key the resource entry to lookup
     * @return an URL which was looked up
     */
    public URL getURL(String key) {
        String name = getString(key);
        URL url = null;

        if (name != null) {
            try {
                url = new URL(name);
            } catch (MalformedURLException ex) {
                System.out.println("Can't open URL " + key + "=" + name);
            }
        }

        return url;
    }

    /**
     * Lookup resource entry with the specified name and return an ImageIcon for
     * it. If no entry with the name exists, null is returned.
     * Calls getResource (key) and returns an ImageIcon for it.
     *
     * @param key the resource entry to lookup
     * @return an ImageIcon which was looked up
     */
    public ImageIcon getIcon(String key) {
        WeakReference<ImageIcon> ref = icons.get(key);
        ImageIcon icon = ref != null ? ref.get() : null;
        if (icon == null) {
            URL url = getResource(key);
            if (url != null) {
                // System.out.println("creating new icon for "+key);
                icon = new ImageIcon(url);
                icons.put(key, new WeakReference<ImageIcon>(icon));
            }
        }
        return icon;
    }

    /**
     * Gives all the keys of this ResourceBundle.  Delegate to given
     * ResourceBundle.
     *
     * @return an Enumeration of keys
     */
    public Enumeration getKeys() {
        return resources.getKeys();
    }

    public String toString() {
        return "SimpleTypedResourceBundle of class " + clazz.getName();
    }

    // --- member variables ------------------------------------

    protected ResourceBundle resources;
    protected Class clazz;
    protected Map<String, WeakReference<ImageIcon>> icons =
            new HashMap<String, WeakReference<ImageIcon>>(1);
}
