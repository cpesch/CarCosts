package slash.gui.toolkit;

import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

/**
 * A KeyStrokeManager handles the mapping of keyboard short cuts, called
 * KeyStrokes in Swing, and the actions, that are fired when pressing a
 * key stroke.
 *
 * @see ActionManager
 */

public class KeyStrokeManager implements KeyListener {

    /**
     * Creates a new KeyStrokeManager.
     */
    public KeyStrokeManager(ActionManager actionMgr) {
        this.actionMgr = actionMgr;

        keymap = addKeymap(DEFAULT_KEYMAP, null);
        keymap.setDefaultAction(new DefaultEditorKit.DefaultKeyTypedAction());
    }

    /* Adds the KeyStrokeManager to receive key events from
    * the given component.
    * @param c the Component
    * @see      java.awt.event.KeyEvent
    * @see      java.awt.event.KeyListener
    */
    public void attachTo(Component c) {
        listeners.add(c);
        c.addKeyListener(this);
    }

    /**
     * Removes the KeyStrokeManager so that it no longer
     * receives key events from the given component.
     *
     * @param c the Component
     * @see java.awt.event.KeyEvent
     * @see java.awt.event.KeyListener
     */
    public void detachFrom(Component c) {
        c.removeKeyListener(this);
        listeners.remove(c);
    }

    /**
     * Detach all Listeners. This is also called by the finalizer to avoid
     * useless Listerers escpecially on Actions
     */
    public void detach() {
        // System.out.println("Detaching "+listeners.size()+" adaptor listeners from KeyStroke Manager");

        for (Iterator iterator = listeners.iterator(); iterator.hasNext();) {
            Component component = (Component) iterator.next();
            component.removeKeyListener(this);
        }

        // remove all listeners
        listeners.clear();
    }

    // --- key listener interface ------------------------------

    /**
     * Invoked when a key has been typed.
     * This event occurs when a key press is followed by a key release.
     */
    public void keyTyped(KeyEvent e) {
        // System.out.println("KeyTyped:" + e);
        processComponentKeyEvent(e);
    }

    /**
     * Invoked when a key has been pressed.
     */
    public void keyPressed(KeyEvent e) {
        // System.out.println("KeyPressed:"+e);
        processComponentKeyEvent(e);
    }

    /**
     * Invoked when a key has been released.
     */
    public void keyReleased(KeyEvent e) {
        // System.out.println("KeyReleased:"+e);
        processComponentKeyEvent(e);
    }

    // --- property change support -----------------------------

    private SwingPropertyChangeSupport changeSupport;

    /**
     * Add a PropertyChangeListener to the listener list.
     * The listener is registered for all properties.
     * <p/>
     * A PropertyChangeEvent will get fired in response to setting
     * a bound property, e.g. setFont, setBackground, or setForeground.
     * Note that if the current component is inheriting its foreground,
     * background, or font from its container, then no event will be
     * fired in response to a change in the inherited property.
     *
     * @param listener The PropertyChangeListener to be added
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        if (changeSupport == null) {
            changeSupport = new SwingPropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove a PropertyChangeListener from the listener list.
     * This removes a PropertyChangeListener that was registered
     * for all properties.
     *
     * @param listener The PropertyChangeListener to be removed
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        if (changeSupport != null) {
            changeSupport.removePropertyChangeListener(listener);
        }
    }

    /**
     * Support for reporting bound property changes.  If oldValue and
     * newValue are not equal and the PropertyChangeEvent listener list
     * isn't empty, then fire a PropertyChange event to each listener.
     * This method has an overloaded method for each primitive type.  For
     * example, here's how to write a bound property set method whose
     * value is an int:
     * <pre>
     * public void setFoo(int newValue) {
     *     int oldValue = foo;
     *     foo = newValue;
     *     firePropertyChange("foo", oldValue, newValue);
     * }
     * </pre>
     * <p/>
     * This method will migrate to java.awt.Component in the next major JDK release
     *
     * @param propertyName The programmatic name of the property that was changed.
     * @param oldValue     The old value of the property.
     * @param newValue     The new value of the property.
     * @see java.beans.PropertyChangeSupport
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (changeSupport != null) {
            changeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    // --- keymap support --------------------------------------

    /**
     * Sets the keymap to use for binding events to
     * actions.  Setting to null effectively disables keyboard input.
     * A PropertyChange event ("keymap") is fired when a new keymap
     * is installed.
     *
     * @param map the keymap
     * @see #getKeymap
     */
    public void setKeymap(Keymap map) {
        Keymap old = keymap;
        keymap = map;
        firePropertyChange("keymap", old, keymap);
    }

    /**
     * Fetches the keymap currently active.
     *
     * @return the keymap
     */
    public Keymap getKeymap() {
        return keymap;
    }

    /**
     * Adds a new keymap into the keymap hierarchy.  Keymap bindings
     * resolve from bottom up so an attribute specified in a child
     * will override an attribute specified in the parent.
     *
     * @param nm     the name of the keymap (must be unique within the collection of
     *               named keymaps).  The name may be null if the keymap is unnamed, but the
     *               caller is responsible for managing the reference returned as an unnamed
     *               keymap can't be fetched by name.
     * @param parent the parent keymap.  This may be null if unspecified
     *               bindings need not be resolved in some other keymap.
     * @return the keymap
     */
    public static Keymap addKeymap(String nm, Keymap parent) {
        Keymap map = new DefaultKeymap(nm, parent);
        if (nm != null) {
            // add a named keymap, a class of bindings
            keymapTable.put(nm, map);
        }
        return map;
    }

    /**
     * Removes a named keymap previously added.  Keymaps
     * with null names may not be removed in this way.
     *
     * @param nm the name of the keymap to remove
     * @return the keymap that was removed
     */
    public static Keymap removeKeymap(String nm) {
        return (Keymap) keymapTable.remove(nm);
    }

    /**
     * Fetches a named keymap previously added to the document.
     * This does not work with null-named keymaps.
     *
     * @param nm the name of the keymap
     * @return the keymap
     */
    public static Keymap getKeymap(String nm) {
        return keymapTable.get(nm);
    }

    /**
     * Loads a keymap with a bunch of
     * bindings.  This can be used to take a static table of
     * definitions and load them into some keymap.  The following
     * example illustrates an example of binding some keys to
     * the cut, copy, and paste actions associated with a
     * JTextComponent.  A code fragment to accomplish
     * this might look as follows:
     * <pre><code>
     * <p/>
     *   static final JTextComponent.KeyBinding[] defaultBindings = {
     *     new JTextComponent.KeyBinding(
     *       KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK),
     *       DefaultEditorKit.copyAction),
     *     new JTextComponent.KeyBinding(
     *       KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK),
     *       DefaultEditorKit.pasteAction),
     *     new JTextComponent.KeyBinding(
     *       KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK),
     *       DefaultEditorKit.cutAction),
     *   };
     * <p/>
     *   keyStrokeMgr.loadKeymap(defaultBindings);
     * </code></pre>
     * The sets of bindings and actions may be empty but must be non-null.
     *
     * @param bindings the bindings
     */
    public void loadKeymap(JTextComponent.KeyBinding[] bindings) {
        for (JTextComponent.KeyBinding binding : bindings) {
            Action a = actionMgr.getAction(binding.actionName, false);
            if (a != null) {
                keymap.addActionForKeyStroke(binding.key, a);
            } else
                System.out.println("Action " + binding.actionName + " for KeyStroke " +
                        binding.key + " not found.");
        }
    }

    /**
     * Maps an event to an action if one is defined in the
     * installed keymap, and perform the action.  If the action is
     * performed, the event is consumed.
     *
     * @return true if an action was performed, false otherwise.
     */
    private boolean mapEventToAction(KeyEvent e) {
        Keymap binding = getKeymap();
        if (binding != null) {
            KeyStroke k = KeyStroke.getKeyStrokeForEvent(e);
            Action a = binding.getAction(k);
            if (a != null) {
                String command = null;
                if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
                    command = String.valueOf(e.getKeyChar());
                }
                ActionEvent ae = new ActionEvent(this,
                        ActionEvent.ACTION_PERFORMED,
                        command, e.getModifiers());
                a.actionPerformed(ae);
                e.consume();
                System.out.println("Action " + a + " fired for " + k);
                return true;
            }
        }
        return false;
    }

    /**
     * Processes any key events that the components itself recognize and fire
     * throught the key listener.  This will be called after the focus manager
     * and any interested listeners have been given a chance to steal away the
     * event.  This method will only be called is the event has not yet been
     * consumed.  This method is called prior to the keyboard UI logic.
     *
     * @param e the event
     */
    private void processComponentKeyEvent(KeyEvent e) {
        int id = e.getID();
        switch (id) {
            case KeyEvent.KEY_TYPED:
                if (!mapEventToAction(e)) {
                    // default behavior is to input translated
                    // characters as content if the character
                    // hasn't been mapped in the keymap.
                    Keymap binding = getKeymap();
                    if (binding != null) {
                        Action a = binding.getDefaultAction();
                        if (a != null) {
                            ActionEvent ae = new ActionEvent(this,
                                    ActionEvent.ACTION_PERFORMED,
                                    String.valueOf(e.getKeyChar()),
                                    e.getModifiers());
                            a.actionPerformed(ae);
                            e.consume();
                        }
                    }
                }
                break;
            case KeyEvent.KEY_PRESSED:
                mapEventToAction(e);
                break;
            case KeyEvent.KEY_RELEASED:
                mapEventToAction(e);
                break;
        }
    }

    // --- inner classes ---------------------------------------

    /**
     * An Implementation of a KeyMap for the KeyStrokeManager.
     */
    public static class DefaultKeymap implements Keymap {

        public DefaultKeymap(String nm, Keymap parent) {
            this.nm = nm;
            this.parent = parent;
            bindings = new Hashtable<KeyStroke, Action>();
        }

        /**
         * Fetch the default action to fire if a
         * key is typed (ie a KEY_TYPED KeyEvent is received)
         * and there is no binding for it.  Typically this
         * would be some action that inserts text so that
         * the keymap doesn't require an action for each
         * possible key.
         */
        public Action getDefaultAction() {
            if (defaultAction != null) {
                return defaultAction;
            }
            return (parent != null) ? parent.getDefaultAction() : null;
        }

        /**
         * Set the default action to fire if a key is typed.
         */
        public void setDefaultAction(Action a) {
            defaultAction = a;
        }

        public String getName() {
            return nm;
        }

        public Action getAction(KeyStroke key) {
            Action a = bindings.get(key);
            if ((a == null) && (parent != null)) {
                a = parent.getAction(key);
            }
            return a;
        }

        public KeyStroke[] getBoundKeyStrokes() {
            KeyStroke[] keys = new KeyStroke[bindings.size()];
            int i = 0;
            for (Enumeration e = bindings.keys(); e.hasMoreElements();) {
                keys[i++] = (KeyStroke) e.nextElement();
            }
            return keys;
        }

        public Action[] getBoundActions() {
            Action[] actions = new Action[bindings.size()];
            int i = 0;
            for (Enumeration e = bindings.elements(); e.hasMoreElements();) {
                actions[i++] = (Action) e.nextElement();
            }
            return actions;
        }

        public KeyStroke[] getKeyStrokesForAction(Action a) {
            return null;
        }

        public boolean isLocallyDefined(KeyStroke key) {
            return bindings.containsKey(key);
        }

        public void addActionForKeyStroke(KeyStroke key, Action a) {
            bindings.put(key, a);
        }

        public void removeKeyStrokeBinding(KeyStroke key) {
            bindings.remove(key);
        }

        public void removeBindings() {
            bindings.clear();
        }

        public Keymap getResolveParent() {
            return parent;
        }

        public void setResolveParent(Keymap parent) {
            this.parent = parent;
        }

        /**
         * String representation of the keymap... potentially
         * a very long string.
         */
        public String toString() {
            return "Keymap[" + nm + "]" + bindings;
        }

        String nm;
        Keymap parent;
        Hashtable<KeyStroke, Action> bindings;
        Action defaultAction;
    }


    /**
     * This is the name of the default keymap that will be shared by all
     * KeyStrokeManager instances unless they have had a different
     * keymap set.
     */
    public static final String DEFAULT_KEYMAP = "default";

    // --- member variables ------------------------------------

    private static Map<String, Keymap> keymapTable = new HashMap<String, Keymap>(17);
    private static Keymap keymap = null;
    private List<Component> listeners = new ArrayList<Component>();
    private ActionManager actionMgr;
}


