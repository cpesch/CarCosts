package slash.gui.toolkit;

import slash.gui.model.BooleanModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.*;

/**
 * An ActionManager maintains a set of Actions used by an application. The
 * idea is that each part of an application, that defines actions will register
 * these with the ActionManager. All later lookups for an Action will be made
 * through the ActionManager and only via the Actions name (command).
 * <p/>
 * This implementation can cascade ActionManagers to have a hierarchy of
 * ActionManagers. This could be helpful when creating Actions explicitly for
 * a certain frame in order to simplify enabling/disabling of actions.
 * <p/>
 * Actions can be associated with a <code>BooleanModel</code>, which
 * determines whether the actions are enabled/disabled.
 *
 * @author Christian Pesch
 */

public class ActionManager {

    /**
     * Create a new ActionManager.
     */
    public ActionManager() {
        commands = new HashMap<String, Action>();
        listeners = new ArrayList<BooleanModelActionAdaptor>();
        cascade = null;
    }

    /**
     * Create a new ActionManager and let it delegate all
     * unknown Actions to the given cascade.
     */
    public ActionManager(ActionManager actionMgr) {
        this();
        cascade = actionMgr;
    }

//   protected void finalize() {
//     System.out.println("Finalizing Action Manager "+this);
//   }


    /**
     * If the action manager is cascaded, then remove the cascade,
     * the listeners and the hash table.
     */
    public void detach() {
        // System.out.println("Detaching "+listeners.size()+" listeners from Action Manager");

        for (Iterator<BooleanModelActionAdaptor> iterator = listeners.iterator(); iterator.hasNext();) {
            Detachable detachable = (Detachable) iterator.next();
            detachable.detach();
        }

        // remove all listeners
        listeners.clear();

        // remove commands from hash table
        commands.clear();

        // null out references
        cascade = null;
        defaultAction = null;
    }

    /**
     * Find an action for a given command. Ask cascaded action
     * managers, if necessary. Return the default action if there
     * is one defined.
     *
     * @param command the name of the Action to search for
     * @return an Action, or null if the command doesn't exist
     */
    public Action getAction(String command) {
        return getAction(command, true);
    }

    /**
     * Find an action for a given command. Ask cascaded action
     * managers, if necessary.
     *
     * @param command       the name of the Action to search for
     * @param returnDefault toggle whether to return the default
     *                      action, when nothing is found
     * @return an Action, or null if the command doesn't exist
     */
    public Action getAction(String command, boolean returnDefault) {
        Action action = (Action) commands.get(command);

        // didn't find action, so ask cascaded ActionManager
        if ((action == null) && (cascade != null)) {
            action = cascade.getAction(command);
        }

        // still no action found, return default action
        if ((action == null) && (returnDefault)) {
            action = defaultAction;
        }
        return action;
    }

    /**
     * Add an Action to the list of Actions maintained by the
     * ActionManager. The Command-String ist the name of the Action.
     *
     * @param action the Action
     */
    public void addAction(Action action) {
        commands.put(action.getValue(Action.NAME).toString(), action);
        // System.out.println("Adding action "+action.getValue(Action.NAME)+
        // " from "+action);
    }

    /**
     * Add an array of Actions to the list of Actions maintained by the
     * ActionManager.  The Command-String is the name of the Action.
     *
     * @param actions Array of actions
     */
    public void addActions(Action[] actions) {
        for (int i = 0; i < actions.length; i++) {
            addAction(actions[i]);
        }
    }

    /**
     * Associate an action with a BooleanModel, which toggles enabled/disabled
     * of the action.  The Command-String ist the name of the Action.
     *
     * @param command Name of the action
     * @param model   Model to associate with
     */
    public void associateAction(String command, BooleanModel model) {
        Action action = getAction(command);
        if (action != defaultAction) {

            // System.out.println("Associating action "+action+" with command "+command);

            // create a Detachable that enables/disables an action
            // with the given boolean model
            BooleanModelActionAdaptor bmaa =
                    new BooleanModelActionAdaptor(action, model);

            // add to list of listeners
            listeners.add(bmaa);
        } else {
            System.out.println("Can't associate model with command " + command);
        }
    }

    /**
     * Sets a default action, which is called, when there is no action found.
     * This is useful for the prototype, when functionality is missing.
     *
     * @param action Default action
     */
    public void setDefaultAction(Action action) {
        defaultAction = action;
    }


    /**
     * Moderate between an action and its BooleanModel in order to
     * enable/disable the action if the BooleanModel's state changes.  This
     * class attaches as ChangeListener to the BooleanModel and forwards
     * subsequent notifications.
     */
    public static class BooleanModelActionAdaptor
            implements ChangeListener, Detachable {

        Action action;
        BooleanModel booleanModel;

        public BooleanModelActionAdaptor(Action action,
                                         BooleanModel booleanModel) {
            this.action = action;
            this.booleanModel = booleanModel;
            booleanModel.addChangeListener(this);

            // explicitly set value
            action.setEnabled(booleanModel.getState());
        }

        public void detach() {
            booleanModel.removeChangeListener(this);
        }

        public void stateChanged(ChangeEvent evt) {
            action.setEnabled(booleanModel.getState());
        }
    }

    // --- member variables ------------------------------------

    private Map<String, Action> commands;
    private ActionManager cascade;
    private List<BooleanModelActionAdaptor> listeners;

    // must be static as there is only one action manager
    private static Action defaultAction = null;
}
