/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 1998-2006 Christian Pesch. All Rights Reserved.
*/

package slash.carcosts;

import slash.gui.GridBagHelper;
import slash.gui.adapter.CalendarModelToBoundedDocumentAdapter;
import slash.gui.adapter.DoubleModelToBoundedDocumentAdapter;
import slash.gui.adapter.IntegerModelToBoundedDocumentAdapter;
import slash.gui.adapter.StringModelToBoundedDocumentAdapter;
import slash.gui.model.*;
import slash.gui.toolkit.ActionManager;
import slash.gui.toolkit.ListenerManager;
import slash.gui.toolkit.ManagedJPanel;
import slash.gui.toolkit.RegistredJFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * This is a view of a maintenance.
 *
 * @author Christian Pesch
 */

public class MaintenanceView extends ManagedJPanel {

    public MaintenanceView(Car car, Maintenance maintenance) {
        super();

        this.car = car;
        this.maintenance = maintenance;
        this.original = new Maintenance(maintenance);

        // initialization
        TypedResourceBundle bundle = CarCosts.getBundle();
        ActionManager actionMgr = new ActionManager(CarCosts.getActionManager());
        listenerMgr = new ListenerManager(bundle, actionMgr);
        setListenerManager(listenerMgr);
        actionMgr.addActions(getActions());

        // create menu-, toolbar
        menuBar = createMenuBar();
        JToolBar toolBar = createToolBar();

        GridBagLayout gridbag = new GridBagLayout();

        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(gridbag);

        CalendarModel imDate = maintenance.getDateModel();
        CalendarModelToBoundedDocumentAdapter adapDate = new
                CalendarModelToBoundedDocumentAdapter(imDate, "dd.MM.yyyy");
        JTextField date = new JTextField();
        date.setDocument(adapDate);

        IntegerModel imMileage = maintenance.getMileageModel();
        IntegerModelToBoundedDocumentAdapter adapMileage = new
                IntegerModelToBoundedDocumentAdapter(imMileage, 10);
        JTextField mileage = new JTextField();
        mileage.setDocument(adapMileage);

        StringModel dbTitle = maintenance.getTitleModel();
        StringModelToBoundedDocumentAdapter adapTitle = new
                StringModelToBoundedDocumentAdapter(dbTitle, 30);
        JTextField title = new JTextField(30);
        title.setDocument(adapTitle);

        StringModel dbNote = maintenance.getNoteModel();
        StringModelToBoundedDocumentAdapter adapNote = new
                StringModelToBoundedDocumentAdapter(dbNote, 30);
        JTextField note = new JTextField(30);
        note.setDocument(adapNote);

        DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.GERMAN);
        decimalFormat.applyPattern("###,##0.00");

        DoubleModel dbCosts = maintenance.getCostsModel();
        DoubleModelToBoundedDocumentAdapter adapCosts = new
                DoubleModelToBoundedDocumentAdapter(dbCosts, decimalFormat);
        JTextField costs = new JTextField(10);
        costs.setDocument(adapCosts);

        CurrencySelector currencySelector = new CurrencySelector();
        currencySelector.setCurrencies(Currency.getCurrencies());
        currencySelector.setModel(maintenance.getCurrencyModel());

        GridBagHelper.constrain(dataPanel, new JLabel(CarCosts.getBundle().getString("maintenance-date-label")), 0, 0, 1, 1,
                2, 2, 2, 2);
        GridBagHelper.constrain(dataPanel, date, 1, 0, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH,
                1.0, 0.0, 2, 2, 2, 2);

        GridBagHelper.constrain(dataPanel, new JLabel(CarCosts.getBundle().getString("maintenance-mileage-label")),
                0, 1, 1, 1,
                2, 2, 2, 2);
        GridBagHelper.constrain(dataPanel, mileage, 1, 1, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH,
                1.0, 0.0, 2, 2, 2, 2);
        GridBagHelper.constrain(dataPanel, new JLabel(CarCosts.getBundle().getString("maintenance-mileage-unit-label")),
                2, 1, 1, 1,
                2, 2, 2, 2);

        GridBagHelper.constrain(dataPanel, new JLabel(CarCosts.getBundle().getString("maintenance-title-label")),
                0, 2, 1, 1,
                2, 2, 2, 2);
        GridBagHelper.constrain(dataPanel, title, 1, 2, 2, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH,
                1.0, 0.0, 2, 2, 2, 2);

        GridBagHelper.constrain(dataPanel, new JLabel(CarCosts.getBundle().getString("maintenance-note-label")),
                0, 3, 1, 1,
                2, 2, 2, 2);
        GridBagHelper.constrain(dataPanel, note, 1, 3, 2, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH,
                1.0, 0.0, 2, 2, 2, 2);

        GridBagHelper.constrain(dataPanel, new JLabel(CarCosts.getBundle().getString("maintenance-cost-label")),
                0, 4, 1, 1,
                2, 2, 2, 2);
        GridBagHelper.constrain(dataPanel, costs, 1, 4, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH,
                1.0, 0.0, 2, 2, 2, 2);
        GridBagHelper.constrain(dataPanel, currencySelector, 2, 4, 1, 1,
                0, 0, 0, 2);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(gridbag);
        GridBagHelper.constrain(actionPanel, listenerMgr.createJButton("edit-maintenance-okay"), 0, 0, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST,
                1.0, 0.0, 0, 0, 0, 0);
        GridBagHelper.constrain(actionPanel, listenerMgr.createJButton("edit-maintenance-cancel"), 1, 0, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST,
                1.0, 0.0, 0, 0, 0, 0);

        // set layout for this panel
        this.setLayout(gridbag);
        GridBagHelper.constrain(this, toolBar, 0, 1, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST,
                1.0, 0.0, 0, 0, 0, 0);
        GridBagHelper.constrain(this, dataPanel, 0, 2, 1, 1,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                1.0, 1.0, 0, 0, 0, 0);
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
                    new EditMaintenanceOkayAction(),
                    new EditMaintenanceCancelAction(),
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

        JMenu menu = listenerMgr.createJMenu("edit-menu");
        menu.add(listenerMgr.createJMenuItem("edit-maintenance-okay"));
        menu.add(listenerMgr.createJMenuItem("edit-maintenance-cancel"));
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

        // toolBar.add(listenerMgr.createJButton("edit-maintenance-okay"));
        // toolBar.add(listenerMgr.createJButton("edit-maintenance-cancel"));

        return toolBar;
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

    // --- Inner classes for actions --------------------------------

    /**
     * An action, which deletes the maintenance.
     */
    public class EditMaintenanceOkayAction extends AbstractAction {

        /**
         * Construct a new action.
         */
        public EditMaintenanceOkayAction() {
            super("edit-maintenance-okay");
        }

        /**
         * Process the event.
         *
         * @param ae the action event
         */
        public void actionPerformed(ActionEvent ae) {
            MaintenanceCosts costs = car.getMaintenanceCosts();
            if (!costs.existsMaintenance(maintenance))
                costs.addMaintenance(maintenance);
            else
                costs.changeMaintenance(maintenance);

            closeWindow();
        }
    }

    /**
     * An action, which deletes the maintenance.
     */
    public class EditMaintenanceCancelAction extends AbstractAction {

        /**
         * Construct a new action.
         */
        public EditMaintenanceCancelAction() {
            super("edit-maintenance-cancel");
        }

        /**
         * Process the event.
         *
         * @param ae the action event
         */
        public void actionPerformed(ActionEvent ae) {
            maintenance.setMaintenance(original);

            closeWindow();
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

    private ListenerManager listenerMgr;
    private Action[] actions = null;

    private Car car;
    private Maintenance maintenance;
    private Maintenance original;
}


