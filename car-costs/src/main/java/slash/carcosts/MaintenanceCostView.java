/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 1998-2006 Christian Pesch. All Rights Reserved.
*/

package slash.carcosts;

import slash.gui.GridBagHelper;
import slash.gui.model.SortingListModel;
import slash.gui.model.TypedResourceBundle;
import slash.gui.toolkit.ActionManager;
import slash.gui.toolkit.ListenerManager;
import slash.gui.toolkit.ManagedJPanel;
import slash.gui.toolkit.RegistredJFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This is a view of the maintenance costs.
 *
 * @author Christian Pesch
 */

public class MaintenanceCostView extends ManagedJPanel {

    public MaintenanceCostView(Car car) {
        super();

        this.car = car;

        // initialization
        TypedResourceBundle bundle = CarCosts.getBundle();
        ActionManager actionMgr = new ActionManager(CarCosts.getActionManager());
        listenerMgr = new ListenerManager(bundle, actionMgr);
        setListenerManager(listenerMgr);
        actionMgr.addActions(getActions());

        // create menu-, toolbar
        menuBar = createMenuBar();
        JToolBar toolBar = createToolBar();

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new GridLayout(1, 5));
        headerPanel.add(new JLabel("Datum"));
        headerPanel.add(new JLabel("Kilometer"));
        headerPanel.add(new JLabel("Titel"));
        headerPanel.add(new JLabel("Notiz"));
        headerPanel.add(new JLabel("Kosten", SwingConstants.CENTER));

        GridBagLayout gridbag = new GridBagLayout();

        JScrollPane scroller = new
                JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // increase speed of scrolling to a factor of 8
        scroller.getVerticalScrollBar().setUnitIncrement(80);
        scroller.getHorizontalScrollBar().setUnitIncrement(80);

        JViewport port = scroller.getViewport();

        maintenanceList = new JList();
        maintenanceList.setModel(new SortingListModel(car.getMaintenanceCosts(), new MaintenanceComparator()));
        maintenanceList.setCellRenderer(new MaintenanceListRenderer());
        port.add(maintenanceList);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(gridbag);
        GridBagHelper.constrain(actionPanel, listenerMgr.createJButton("new-maintenance"), 0, 0, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST,
                1.0, 0.0, 0, 0, 0, 0);
        GridBagHelper.constrain(actionPanel, listenerMgr.createJButton("edit-maintenance"), 1, 0, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST,
                1.0, 0.0, 0, 0, 0, 0);
        GridBagHelper.constrain(actionPanel, listenerMgr.createJButton("delete-maintenance"), 2, 0, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST,
                1.0, 0.0, 0, 0, 0, 0);
        GridBagHelper.constrain(actionPanel, listenerMgr.createJButton("close-window"), 0, 1, 3, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST,
                1.0, 0.0, 0, 0, 0, 0);

        // set layout for this panel
        this.setLayout(gridbag);
        if (toolBar != null)
            GridBagHelper.constrain(this, toolBar, 0, 0, 1, 1,
                    GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST,
                    1.0, 0.0, 0, 0, 0, 0);
        GridBagHelper.constrain(this, headerPanel, 0, 1, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER,
                1.0, 0.0, 0, 0, 0, 0);
        GridBagHelper.constrain(this, scroller, 0, 2, 1, 1,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                4.0, 4.0, 0, 0, 0, 0);
        GridBagHelper.constrain(this, actionPanel, 0, 3, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER,
                1.0, 0.0, 0, 0, 0, 0);

    }

    /**
     * Gets the actions for this view.
     *
     * @return the actions for the view
     */
    public Action[] getActions() {
        if (actions == null) {
            actions = new Action[]{
                    new NewMaintenanceAction(),
                    new EditMaintenanceAction(),
                    new DeleteMaintenanceAction(),
                    new CloseWindowAction()
            };
        }
        return actions;
    }

    /**
     * Sets the frame of the view.
     *
     * @param frame in that the view is displayed
     */
    public void setFrame(RegistredJFrame frame) {
        this.frame = frame;
        frame.setJMenuBar(menuBar);

        // by default, let the program handle the close operation
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // set our close operation
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeWindow();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    /**
     * Create the menu bar for this view
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // file menu for tree
        JMenu menu = listenerMgr.createJMenu("file-menu");
        menu.add(listenerMgr.createJMenuItem("close-window"));
        menuBar.add(menu);

        menu = listenerMgr.createJMenu("edit-menu");
        menu.add(listenerMgr.createJMenuItem("new-maintenance"));
        menu.add(listenerMgr.createJMenuItem("edit-maintenance"));
        menu.add(listenerMgr.createJMenuItem("delete-maintenance"));
        menuBar.add(menu);

        return menuBar;
    }

    /**
     * Create the toolbar for this view.
     */
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();

        // don't allow toolbar to float around
        toolBar.setFloatable(false);

        toolBar.add(listenerMgr.createJButton("new-maintenance"));
        toolBar.add(listenerMgr.createJButton("edit-maintenance"));
        toolBar.add(listenerMgr.createJButton("delete-maintenance"));

        return null; // ## deactivated for now
    }

    /**
     * Close the frame.
     */
    private void closeWindow() {
        // when there's only one frame, call exit action
        if (CarCosts.getFrameManager().getFrameCount() == 1) {
            CarCosts.getActionManager().getAction("exit").actionPerformed(null);
        } else {
            frame.dispose();
        }
    }

    private void createMaintenanceFrame(Maintenance maintenance) {
        RegistredJFrame frame = new RegistredJFrame(CarCosts.getFrameManager(),
                CarCosts.getBundle().getString("maintenance-title"),
                CarCosts.getNextFrameCount());

        MaintenanceView view = new MaintenanceView(car, maintenance);
        view.setFrame(frame);

        frame.setIconImage(CarCosts.getBundle().getIcon("maintenance-image").getImage());
        frame.getContentPane().add("Center", view);
        frame.setSize(315, 210);
        frame.setVisible(true);
    }

    // --- Inner classes ------------------------------------------

    // --- Inner classes for actions --------------------------------

    /**
     * An action, which deletes the maintenance.
     */
    public class NewMaintenanceAction extends AbstractAction {

        /**
         * Construct a new action.
         */
        public NewMaintenanceAction() {
            super("new-maintenance");
        }

        /**
         * Process the event.
         *
         * @param ae the action event
         */
        public void actionPerformed(ActionEvent ae) {
            createMaintenanceFrame(new Maintenance(car.getCurrency()));
        }
    }

    /**
     * An action, which edits the maintenances.
     */
    public class EditMaintenanceAction extends AbstractAction {

        /**
         * Construct a new action.
         */
        public EditMaintenanceAction() {
            super("edit-maintenance");
        }

        /**
         * Process the event.
         *
         * @param ae the action event
         */
        public void actionPerformed(ActionEvent ae) {
            Object[] selectedValues = maintenanceList.getSelectedValues();
            for (Object selectedValue : selectedValues) {
                Maintenance maintenance = (Maintenance) selectedValue;
                createMaintenanceFrame(maintenance);
            }
        }
    }

    /**
     * An action, which delete the maintenances.
     */
    public class DeleteMaintenanceAction extends AbstractAction {

        /**
         * Construct a new action.
         */
        public DeleteMaintenanceAction() {
            super("delete-maintenance");
        }

        /**
         * Process the event.
         *
         * @param ae the action event
         */
        public void actionPerformed(ActionEvent ae) {
            Object[] selectedValues = maintenanceList.getSelectedValues();
            for (Object selectedValue : selectedValues) {
                Maintenance maintenance = (Maintenance) selectedValue;
                car.getMaintenanceCosts().removeMaintenance(maintenance);
            }
        }
    }

    /**
     * An action, which closes a window.
     */
    public class CloseWindowAction extends AbstractAction {

        public CloseWindowAction() {
            super("close-window");
        }

        /**
         * Process the event.
         *
         * @param e the action event
         */
        public void actionPerformed(ActionEvent e) {
            closeWindow();
        }
    }

    // --- member variables ------------------------------------

    private RegistredJFrame frame;
    private JMenuBar menuBar;
    private JList maintenanceList;

    private ListenerManager listenerMgr;
    private Action[] actions = null;

    private Car car;
}
