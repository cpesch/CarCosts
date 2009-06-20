package slash.gui.toolkit;

import slash.gui.model.TypedResourceBundle;

import javax.swing.*;

/**
 * A ManagedJPanel is basically a JPanel that has a ListenerManager
 * and an ActionManager associated with it.
 *
 * @see ActionManager
 * @see ListenerManager
 * @see KeyStrokeManager
 */

public class ManagedJPanel extends JPanel {

    /**
     * The ListenerManager of the panel.
     */
    private ListenerManager listenerMgr;

    /**
     * The ActionManager of the panel.
     */
    private ActionManager actionMgr;

    /**
     * The KeyStrokeManager of the panel.
     */
    private KeyStrokeManager keyStrokeMgr;

    /**
     * Create the panel without managers.
     */
    public ManagedJPanel() {
        super();
        this.actionMgr = null;
        this.listenerMgr = null;
        this.keyStrokeMgr = null;
    }

    /**
     * Create the panel the given listener manager.
     */
    public ManagedJPanel(ListenerManager listenerManager) {
        this();
        this.listenerMgr = listenerManager;
    }

    /**
     * Create the panel with the given managers.
     */
    public ManagedJPanel(TypedResourceBundle bundle,
                         ActionManager actionManager) {
        this(new ListenerManager(bundle, actionManager));
        this.actionMgr = actionManager;
        this.keyStrokeMgr = new KeyStrokeManager(actionMgr);
    }


    /**
     * Sets the listener manager for this panel.
     *
     * @param listenerManager the ListenerManager
     */
    public void setListenerManager(ListenerManager listenerManager) {
        this.listenerMgr = listenerManager;
    }

    /**
     * Gets the listener manager for this panel.
     *
     * @return the ListenerManager
     */
    public ListenerManager getListenerManager() {
        return listenerMgr;
    }


    /**
     * Sets the action manager for this panel.
     *
     * @param actionManager the ActionManager
     */
    public void setActionManager(ActionManager actionManager) {
        this.actionMgr = actionManager;
    }

    /**
     * Gets the action manager for this panel.
     *
     * @return the ActionManager
     */
    public ActionManager getActionManager() {
        return actionMgr;
    }

    /**
     * Sets the KeyStrokeManager for this panel.
     *
     * @param keyStrokeManager the KeyStrokeManager
     */
    public void setKeyStrokeManager(KeyStrokeManager keyStrokeManager) {
        this.keyStrokeMgr = keyStrokeManager;
    }

    /**
     * Gets the KeyStrokeManager for this panel.
     *
     * @return the KeyStrokeManager
     */
    public KeyStrokeManager getKeyStrokeManager() {
        return keyStrokeMgr;
    }
}
