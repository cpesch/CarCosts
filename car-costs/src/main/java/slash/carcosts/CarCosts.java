/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 1998-2001 Christian Pesch. All Rights Reserved.  
*/

package slash.carcosts;

import slash.gui.model.TypedResourceBundle;
import slash.gui.toolkit.ActionManager;
import slash.gui.toolkit.FrameManager;
import slash.gui.toolkit.HistoryFrameManager;
import slash.gui.toolkit.RegistredJFrame;
import slash.util.SimpleTypedResourceBundle;
import slash.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * The car cost calculation program.
 *
 * @author Christian Pesch
 */

public class CarCosts {
    public static Logger log = Logger.getLogger(CarCosts.class.getName());

    /**
     * Construct a new main object.
     */
    public CarCosts() {
        log.info("Creating new carcosts instance");

        // init the log facility early
        initializeLogging();

        setLookAndFeel();

        ActionManager actionMgr = getActionManager();
        actionMgr.addActions(getActions());
        actionMgr.setDefaultAction(new DefaultAction());
    }

    /**
     * Initialize with the given file names. If no
     * files were given, start with a new empty
     * car.
     */
    protected void initialize(String[] fileNames) {
        if (fileNames.length == 0)
            createCarView().setCar(null, new Car());
        else {

            for (int i = 0, c = fileNames.length; i < c; i++) {
                openCarView(new File(fileNames[i]));
            }
        }
    }

    /**
     * Gets the actions for this view.
     *
     * @return the actions for the view
     */
    public Action[] getActions() {
        if (actions == null) {
            actions = new Action[]{
                    new NewCarAction(),
                    new AboutAction(),
                    new ActivateWindowAction(),
                    new ExitAction()
            };
        }
        return actions;
    }

    public static FrameManager getFrameManager() {
        if (frameMgr == null)
            frameMgr = new HistoryFrameManager();

        return frameMgr;
    }

    public static ActionManager getActionManager() {
        if (actionMgr == null)
            actionMgr = new ActionManager();
        return actionMgr;
    }

    /**
     * Get the ressource bundle.
     */
    public static TypedResourceBundle getBundle() {
        if (bundle == null) {
            try {
                bundle = new SimpleTypedResourceBundle(ResourceBundle.getBundle("slash/carcosts/carcosts",
                        Locale.getDefault()),
                        Class.forName("slash.carcosts.CarCosts"));
            } catch (Exception e) {
                e.printStackTrace();
                log.severe("Can't get bundle: " + e.getMessage());
                System.exit(20);
            }
        }
        return bundle;
    }

    /**
     * Initialize the logging.
     */
    void initializeLogging() {
        log.fine("Initializing carcosts logging");

        try {
            LogManager manager = LogManager.getLogManager();
            manager.addLogger(log);
        } catch (Exception e) {
            // its okay here to print a stack trace
            e.printStackTrace();
            exit(10);
        }
    }

    void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // intentionally do nothing
        }
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
    }

    /**
     * Get the number of frames.
     */
    public static int getNextFrameCount() {
        return frameCount++;
    }

    /**
     * Start the Program.
     */
    public static void main(String[] args) {
        CarCosts carCosts = new CarCosts();
        carCosts.initialize(args);
    }

    /**
     * Exit the Program.
     */
    public void exit(int level) {
        System.exit(level);
    }

    public CarView createCarView() {
        RegistredJFrame frame = new RegistredJFrame(getFrameManager(),
                CarCosts.getBundle().getString("car-title"),
                getNextFrameCount());
        view = new CarView();
        view.setFrame(frame);

        frame.setIconImage(CarCosts.getBundle().getIcon("car-image").getImage());
        frame.getContentPane().add("Center", view);
        frame.setSize(600, 400);
        frame.setVisible(true);

        return view;
    }

    public void openCarView(File file) {
        try {
            Car car = new Car(file);
            createCarView().setCar(file, car);
        } catch (IOException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(null, Util.formatString(CarCosts.getBundle().getString("load-car-failed"),
                    new Object[]{file.getName(), e}),
                    CarCosts.getBundle().getString("carcosts-title"),
                    JOptionPane.ERROR_MESSAGE);

            createCarView();
        }
    }

    // --- Inner classes for actions --------------------------------

    /**
     * An action, which creates a new car.
     */
    public class NewCarAction extends AbstractAction {

        /**
         * Construct a new action.
         */
        public NewCarAction() {
            super("new-car");
        }

        /**
         * Process the event.
         *
         * @param e the action event
         */
        public void actionPerformed(ActionEvent e) {
            createCarView().setCar(null, new Car());
        }
    }

    /**
     * An action, which exits the application.
     */
    public class ExitAction extends AbstractAction {

        public ExitAction() {
            super("exit");
        }

        /**
         * Process the event.
         *
         * @param e the action event
         */
        public void actionPerformed(ActionEvent e) {
            if (view.isSaveNeeded()) {
                int option = JOptionPane.showConfirmDialog(null,
                        CarCosts.getBundle().getString("exit-query-for-save"),
                        CarCosts.getBundle().getString("carcosts-title"),
                        JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    view.save();
                }
            }

            exit(0);
        }
    }

    /**
     * An action, which shows an about message.
     */
    public class AboutAction extends AbstractAction {

        /**
         * Construct a new action.
         */
        public AboutAction() {
            super("about");
        }

        /**
         * Process the event.
         *
         * @param e the action event
         */
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null, CarCosts.getBundle().getString("about-text"),
                    CarCosts.getBundle().getString("carcosts-title"),
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * The default action.
     */
    public static class DefaultAction extends AbstractAction {

        public DefaultAction() {
            super("default-action");
        }

        /**
         * Process the event.
         *
         * @param e the action event
         */
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null, Util.formatString(CarCosts.getBundle().getString("not-implemented"),
                    new Object[]{e.getActionCommand()}),
                    CarCosts.getBundle().getString("carcosts-title"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * An action, which activates a window.
     */
    public class ActivateWindowAction extends AbstractAction {

        public ActivateWindowAction() {
            super("activate-window");
        }

        /**
         * Process the event.
         *
         * @param e the action event
         */
        public void actionPerformed(ActionEvent e) {
            String title = e.getActionCommand();
            Frame frame = getFrameManager().getFrameByTitle(title);

            if ((frame != null) && (frame instanceof RegistredJFrame)) {
                // activate window
                frame.toFront();
                frame.requestFocus();
            }
        }
    }

    // --- member variables ------------------------------------

    private static TypedResourceBundle bundle = null;
    private static int frameCount = 1;
    private static ActionManager actionMgr = null;
    private static FrameManager frameMgr = null;

    private Action[] actions = null;

    private CarView view;
}
