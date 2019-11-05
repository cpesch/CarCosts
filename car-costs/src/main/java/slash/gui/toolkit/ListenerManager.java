package slash.gui.toolkit;

import slash.gui.model.BooleanModel;
import slash.gui.model.TriStateModel;
import slash.gui.model.TypedResourceBundle;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

/**
 * This class acts as a factory an manager for Listeners (used in
 * ManagedJPanel).  Mainly the listener connection between AbstractButton and
 * BooleanModel, TriStateModel and Actions through Adaptors ist handled by this
 * class.  Detachment of all listeners is guaranteed through this classe's
 * finalizer.
 */

public class ListenerManager {
    /**
     * suffix for an Image
     */
    public static final String imageSuffix = "Image";
    /**
     * suffix for a Label
     */
    public static final String labelSuffix = "Label";
    /**
     * suffix for a ToolTip
     */
    public static final String toolTipSuffix = "ToolTip";
    /**
     * suffix for a Mnemonice
     */
    public static final String mnemonicSuffix = "Mnemonic";

    /**
     * infix for a Button
     */
    public static final String buttonInfix = "Button";
    /**
     * infix for a MenuItem
     */
    public static final String menuItemInfix = "MenuItem";

    /**
     * Create a new ListenerManager with a TypedResourceBundle
     * and an ActionManager.
     */
    public ListenerManager(TypedResourceBundle bundle,
                           ActionManager actionMgr) {
        this.bundle = bundle;
        this.actionMgr = actionMgr;
        listeners = new ArrayList<Detachable>();
        actions = new HashMap<AbstractButton, Action>();
    }

//   protected void finalize() {
//     System.out.println("Finalizing Detachable Manager "+this);
//   }

    /**
     * Detach all Listeners. This is also called by the finalizer to avoid
     * useless Listerers escpecially on Actions
     */
    public void detach() {
        // System.out.println("Detaching "+listeners.size()+" adaptor listeners from Detachable Manager");

        for (Detachable detachable : listeners) {
            detachable.detach();
        }

        // remove all listeners
        listeners.clear();

        // System.out.println("Detaching "+actions.size()+" action listeners from Detachable Manager");

        Set<AbstractButton> buttons = actions.keySet();
        for (AbstractButton button : buttons) {
            Action a = actions.get(button);
            button.removeActionListener(a);
        }

        // remove all listeners
        actions.clear();

        actionMgr = null;
    }

    /**
     * Try to perform bundle.getString(key+infix+suffix)
     * if this returns null return bundle.getString(key+suffix)
     */
    private String getAlternativeString(String key, String infix, String suffix) {
        String s = bundle.getString(key + infix + suffix);
        if (s != null)
            return s;
        else
            return bundle.getString(key + suffix);
    }

    /**
     * Try to perform bundle.getIcon(key+infix+suffix)
     * if this returns null return bundle.getIcon(key+suffix)
     */
    private ImageIcon getAlternativeIcon(String key, String infix, String suffix) {
        ImageIcon icon = bundle.getIcon(key + infix + suffix);
        if (icon != null)
            return icon;
        else
            return bundle.getIcon(key + suffix);
    }


    /**
     * initializes an AbstractButton with the label from the properties
     * The Action gets attached as ActionListener to the Button
     * The Button gets attached as PropertyChangeListener to the Action
     */
    protected void initializeAbstractButton(AbstractButton but,
                                            String cmd,
                                            String infix) {
        but.setActionCommand(cmd);

        // set label
        String label = getAlternativeString(cmd, infix, labelSuffix);
        if (label != null) {
            but.setText(label);
        }

        // set ToolTip
        String tip = getAlternativeString(cmd, infix, toolTipSuffix);
        if (tip != null) {
            but.setToolTipText(tip);
        }

        // set mnemonic
        String mnemonic = bundle.getString(cmd + mnemonicSuffix);
        if (mnemonic != null) {
            but.setMnemonic(mnemonic.charAt(0));
        }

        // load image
        ImageIcon icon = getAlternativeIcon(cmd, infix, imageSuffix);
        if (icon != null) {
            but.setHorizontalTextPosition(JButton.RIGHT);
            but.setIcon(icon);
        }

        // initialize the action
        initializeAction(but, cmd);
    }

    /**
     * initializes an AbstractButton with the label from the properties
     * The Button gets attached as PropertyChangeListener to the Action
     */
    protected void initializeAction(AbstractButton but,
                                    String cmd) {
        // get associated Action
        Action a = actionMgr.getAction(cmd);
        if (a != null) {
            but.addActionListener(a);

            // add to hashtable of listeners
            actions.put(but, a);

            // create a PropertyChangeListerner that modifies the Button
            Detachable pcl = new PropertyChangeDetachableAdaptor(but, a);

            // add to list of listeners
            listeners.add(pcl);
        } else {
            but.setEnabled(false);
        }
    }

    /**
     * initializes an AbstractButton
     * The Button gets attached as ChangeListener to the BooleanModel
     */
    protected void initializeAbstractButton(AbstractButton but,
                                            String cmd,
                                            String infix,
                                            BooleanModel booleanModel) {
        initializeAbstractButton(but, cmd, infix);

        // create a ChangeListerner that modifies the Button
        Detachable bcl = new BooleanModelChangeDetachableAdaptor(but, booleanModel);

        // add to list of listeners
        listeners.add(bcl);
    }

    /**
     * initializes an AbstractButton
     * The Button gets attached as ChangeListener to the TriStateModel
     */
    protected void initializeAbstractButton(AbstractButton but,
                                            String cmd,
                                            String infix,
                                            TriStateModel triStateModel) {
        initializeAbstractButton(but, cmd, infix);

        // create a ChangeListerner that modifies the Button
        Detachable bcl = new TriStateModelChangeDetachableAdaptor(but, triStateModel);

        // add to list of listeners
        listeners.add(bcl);
    }


    /**
     * Creates a JMenuItem and initializes it.
     */
    public JMenuItem createJMenuItem(String cmd) {
        // create a JMenuItem
        JMenuItem mi = new JMenuItem();
        initializeAbstractButton(mi, cmd, menuItemInfix);
        return mi;
    }

    /**
     * Creates a JMenuItem and initializes it. This variant uses
     * a given title.
     */
    public JMenuItem createJMenuItem(String cmd, String title) {
        // create a JMenuItem
        JMenuItem mi = new JMenuItem(title);
        initializeAction(mi, cmd);
        return mi;
    }

    /**
     * Creates a JMenuItem and initializes it. This variant uses
     * a given title and icon.
     */
    public JMenuItem createJMenuItem(String cmd, String title, Icon icon) {
        JMenuItem mi = createJMenuItem(cmd, title);
        mi.setIcon(icon);
        return mi;
    }

    /**
     * Creates a JMenuItem and initializes it.
     */
    public JMenuItem createJMenuItem(String cmd, TriStateModel tristateModel) {
        // create a JMenuItem
        JMenuItem mi = new JMenuItem();
        initializeAbstractButton(mi, cmd, menuItemInfix, tristateModel);
        return mi;
    }


    /**
     * Creates a JButton and initializes it
     */
    public JButton createJButton(String cmd) {
        // create a JButton
        JButton but = new NoFocusJButton();
        initializeAbstractButton(but, cmd, buttonInfix);
        return but;
    }

    /**
     * Creates a JButton and initializes it
     */
    public JButton createJButton(String cmd, BooleanModel booleanModel) {
        // create a JButton
        JButton but = new NoFocusJButton();
        initializeAbstractButton(but, cmd, buttonInfix, booleanModel);
        return but;
    }

    /**
     * Creates a JButton and initializes it
     */
    public JButton createJButton(String cmd, TriStateModel tristateModel) {
        // create a JButton
        JButton but = new NoFocusJButton();
        initializeAbstractButton(but, cmd, buttonInfix, tristateModel);
        return but;
    }


    /**
     * Creates a JToggleButton and initializes it
     */
    public JToggleButton createJToggleButton(String cmd, BooleanModel booleanModel) {
        // create a JToggleButton
        JToggleButton but = new NoFocusJToggleButton();
        initializeAbstractButton(but, cmd, buttonInfix, booleanModel);
        return but;
    }

    /**
     * Creates a JToggleButton and initializes it
     */
    public JToggleButton createJToggleButton(String cmd, TriStateModel triStateModel) {
        // create a JToggleButton
        JToggleButton but = new NoFocusJToggleButton();
        initializeAbstractButton(but, cmd, buttonInfix, triStateModel);
        return but;
    }


    /**
     * Creates a JCheckBoxMenuItem and initializes it
     */
    public JCheckBoxMenuItem createJCheckBoxMenuItem(String cmd, BooleanModel booleanModel) {
        JCheckBoxMenuItem but = new JCheckBoxMenuItem();
        initializeAbstractButton(but, cmd, menuItemInfix, booleanModel);
        return but;
    }

    /**
     * Creates a JCheckBoxMenuItem and initializes it
     */
    public JCheckBoxMenuItem createJCheckBoxMenuItem(String cmd, TriStateModel triStateModel) {
        JCheckBoxMenuItem but = new JCheckBoxMenuItem();
        initializeAbstractButton(but, cmd, menuItemInfix, triStateModel);
        return but;
    }


    /**
     * Creates a JMenu and sets its label
     */
    public JMenu createJMenu(String name) {
        JMenu menu = new JMenu();

        String label = bundle.getString(name + labelSuffix);
        if (label != null) {
            menu.setText(label);
        }

        String mnemonic = bundle.getString(name + mnemonicSuffix);
        if (mnemonic != null) {
            menu.setMnemonic(mnemonic.charAt(0));
        }

        return menu;
    }


    /**
     * Moderate between a button an an action in order to dis-/enable the button
     * if the action's dis/enabled-state changes.
     * This class attaches as PropertyChangeListener to the Action and forwards
     * subsequent notifications.
     */
    public static class PropertyChangeDetachableAdaptor implements PropertyChangeListener,
            Detachable {

        AbstractButton button;
        Action action;

        public PropertyChangeDetachableAdaptor(AbstractButton button,
                                               Action action) {
            this.button = button;
            this.action = action;
            action.addPropertyChangeListener(this);

            // explicidly set value
            button.setEnabled(action.isEnabled());
        }

        public void detach() {
            // System.out.println("removing pcl " + this + " from " + action);
            action.removePropertyChangeListener(this);
            action = null;
            button = null;
        }

        public void propertyChange(PropertyChangeEvent evt) {
            button.setEnabled(action.isEnabled());
        }
    }

    /**
     * Moderate between a button and its BooleanModel in order to
     * select/unselect the button if the BooleanModel's state changes.  This
     * class attaches as ChangeListener to the BooleanModel and forwards
     * subsequent notifications.
     */
    public static class BooleanModelChangeDetachableAdaptor implements ChangeListener,
            Detachable {

        AbstractButton button;
        BooleanModel booleanModel;

        public BooleanModelChangeDetachableAdaptor(AbstractButton button,
                                                   BooleanModel booleanModel) {
            this.button = button;
            this.booleanModel = booleanModel;
            booleanModel.addChangeListener(this);

            // explicidly set value
            button.setSelected(booleanModel.getState());
        }

        public void detach() {
            booleanModel.removeChangeListener(this);
        }

        public void stateChanged(ChangeEvent evt) {
            button.setSelected(booleanModel.getState());
        }
    }


    /**
     * Moderate between a button and its TriStateModel in order to select/unselect, enable/disable
     * the button if the TriStateModel's state changes.
     * This class attaches as ChangeListener to the TriStateModel and forwards
     * subsequent notifications.
     */
    public static class TriStateModelChangeDetachableAdaptor
            implements ChangeListener, Detachable {

        AbstractButton button;
        TriStateModel triStateModel;

        public TriStateModelChangeDetachableAdaptor(AbstractButton button,
                                                    TriStateModel triStateModel) {
            this.button = button;
            this.triStateModel = triStateModel;
            triStateModel.addChangeListener(this);

            syncState();
        }

        public void detach() {
            triStateModel.removeChangeListener(this);
        }

        public void syncState() {
            switch (triStateModel.getState()) {
                case TriStateModel.YES:
                    button.setSelected(true);
                    break;
                case TriStateModel.NO:
                    button.setSelected(false);
                    break;
                case TriStateModel.DON_T_CARE:
                    // ## gray out button
                    break;
            }
        }

        public void stateChanged(ChangeEvent evt) {
            syncState();
        }
    }


    private static Dimension hackDimension = new Dimension(23, 23);

    /**
     * A JButton which always denies receiving the focus and
     * always shows a smart optic by hacking a nice size.
     */
    public static class NoFocusJButton extends JButton {

        /**
         * Construct a new JButton, but let this button always deny
         * getting the focus. For a smart windows look and feel,
         * reduce minimum, preferred and maximum size.
         */
        public NoFocusJButton() {
            super();
            setRequestFocusEnabled(false);

            setMinimumSize(hackDimension);
            setPreferredSize(hackDimension);
            setMaximumSize(hackDimension);
        }

        /**
         * Identifies whether or not this component can receive the focus.
         * A disabled button, for example, would return false.
         *
         * @return true if this component can receive the focus
         */
        public boolean isFocusTraversable() {
            return false;
        }
    }

    /**
     * A JToggleButton which always denies receiving the focus and
     * always shows a smart optic by hacking a nice size.
     */
    public static class NoFocusJToggleButton extends JToggleButton {

        /**
         * Construct a new JToggleButton, but let this button
         * always deny getting the focus.  For a smart windows
         * look and feel, reduce minimum, preferred and maximum size.
         * For other l&fs, reduce maximum size.
         */
        public NoFocusJToggleButton() {
            super();
            setRequestFocusEnabled(false);

            setMinimumSize(hackDimension);
            setPreferredSize(hackDimension);
            setMaximumSize(hackDimension);
        }

        /**
         * Identifies whether or not this component can receive the focus.
         * A disabled button, for example, would return false.
         *
         * @return true if this component can receive the focus
         */
        public boolean isFocusTraversable() {
            return false;
        }
    }

    // --- member variables ------------------------------------

    private TypedResourceBundle bundle;
    private ActionManager actionMgr;
    private List<Detachable> listeners;
    private Map<AbstractButton, Action> actions;
}

