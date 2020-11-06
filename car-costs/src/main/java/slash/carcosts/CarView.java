/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 1998-2001 Christian Pesch. All Rights Reserved.  
*/

package slash.carcosts;

import slash.gui.GridBagHelper;
import slash.gui.adapter.*;
import slash.gui.model.*;
import slash.gui.toolkit.*;
import slash.util.Files;
import slash.util.Util;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.prefs.Preferences;

import static javax.swing.JFileChooser.APPROVE_OPTION;
import static javax.swing.JFileChooser.FILES_ONLY;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static slash.gui.chooser.Constants.createJFileChooser;

/**
 * This is a view of a car.
 *
 * @author Christian Pesch
 */

public class CarView extends ManagedJPanel {
    private static final Preferences preferences = Preferences.userNodeForPackage(CarView.class);
    private static final String LAST_DIRECTORY_PREFERENCE = "preferred-directory";

    public CarView() {
        super();

        // initialization
        TypedResourceBundle bundle = CarCosts.getBundle();
        ActionManager actionMgr = new ActionManager(CarCosts.getActionManager());
        listenerMgr = new ListenerManager(bundle, actionMgr);
        setListenerManager(listenerMgr);
        actionMgr.addActions(getActions());

        // associate actions with models
        actionMgr.associateAction("save-car", saveModel);

        // create menu-, toolbar
        pane = new CarPane();
        menuBar = createMenuBar();
        JToolBar toolBar = createToolBar();

        GridBagLayout gridbag = new GridBagLayout();

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(gridbag);
        GridBagHelper.constrain(actionPanel, listenerMgr.createJButton("edit-fuel-costs"), 0, 0, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST,
                1.0, 0.0, 0, 0, 0, 0);
        GridBagHelper.constrain(actionPanel, listenerMgr.createJButton("edit-maintenance-costs"), 1, 0, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST,
                1.0, 0.0, 0, 0, 0, 0);

        // set layout for this panel
        this.setLayout(gridbag);
        GridBagHelper.constrain(this, toolBar, 0, 1, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST,
                1.0, 0.0, 0, 0, 0, 0);
        GridBagHelper.constrain(this, pane, 0, 2, 1, 1,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                4.0, 4.0, 0, 0, 0, 0);
        GridBagHelper.constrain(this, actionPanel, 0, 3, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER,
                1.0, 0.0, 0, 0, 0, 0);

        // install window menu updater
        new WindowMenuUpdater(CarCosts.getFrameManager());
    }

    /**
     * Gets the actions for this view.
     *
     * @return the actions for the view
     */
    public Action[] getActions() {
        if (actions == null) {
            actions = new Action[]{
                    new EditFuelCostsAction(),
                    new EditMaintenanceCostsAction(),
                    new LoadCarAction(),
                    new SaveAsCarAction(),
                    new SaveCarAction()
            };
        }
        return actions;
    }

    /**
     * Sets the frame of the view.
     *
     * @param frame in which the view is displayed
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

        frame.setDocumentName(CarCosts.getBundle().getString("unnamed-car-title"));
    }

    public void setCar(File newFile, Car newCar) {
        this.file = newFile;
        this.car = newCar;

        pane.setCar(car);

        if (file != null)
            frame.setDocumentName(file.getName());
        else
            frame.setDocumentName(CarCosts.getBundle().getString("unnamed-car-title"));

        currencyModelListener.setDelegate(car.getCurrencyModel());
        saveModelListener.setDelegate(car.getSaveModel());
    }

    public boolean isSaveNeeded() {
        return saveModel.getState();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // file menu
        JMenu menu = listenerMgr.createJMenu("file-menu");
        menu.add(listenerMgr.createJMenuItem("new-car"));
        menu.add(listenerMgr.createJMenuItem("load-car"));
        menu.addSeparator();
        menu.add(listenerMgr.createJMenuItem("save-car"));
        menu.add(listenerMgr.createJMenuItem("save-as-car"));
        menu.addSeparator();
        menu.add(listenerMgr.createJMenuItem("exit"));
        menuBar.add(menu);

        // edit menu
        menu = listenerMgr.createJMenu("edit-menu");
        menu.add(listenerMgr.createJMenuItem("edit-fuel-costs"));
        menu.add(listenerMgr.createJMenuItem("edit-maintenance-costs"));
        menuBar.add(menu);

        // view menu
        /* ## do later
        menu = listenerMgr.createJMenu("view-menu");
        BooleanModelModerator currencyModerator = new BooleanModelModerator();
        Currency[] currencies = Currency.getCurrencies();
        for(int i=0; i < currencies.length; i++) {
          BooleanModel bm = new DefaultBooleanModel();
          currencyModerator.addModel(bm);
          menu.add(listenerMgr.createJCheckBoxMenuItem("select-currency-"+currencies[i].getName(), bm));
          currencyModels.put(currencies[i].getName(), bm);
        }
        menuBar.add(menu);
        */

        // window menu
        windowMenu = listenerMgr.createJMenu("window-menu");
        menuBar.add(windowMenu);

        menu = listenerMgr.createJMenu("help-menu");
        menu.add(listenerMgr.createJMenuItem("about"));
        menuBar.add(menu);

        return menuBar;
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();

        // don't allow toolbar to float around
        toolBar.setFloatable(false);

        toolBar.add(listenerMgr.createJButton("new-car"));
        toolBar.add(listenerMgr.createJButton("load-car"));
        toolBar.add(listenerMgr.createJButton("save-car"));

        return toolBar;
    }

    /**
     * Close the frame.
     */
    private void closeWindow() {
        CarCosts.getActionManager().getAction("exit").actionPerformed(null);
    }

    // --- Inner classes for actions --------------------------------

    public class CarPane extends JPanel {

        public CarPane() {
            GridBagLayout gridbag = new GridBagLayout();

            setLayout(gridbag);

            name = new JTextField();
            sign = new JTextField();

            from = new JTextField();
            from.setEditable(false);
            to = new JTextField();
            to.setEditable(false);

            currencySelector = new CurrencySelector();
            currencySelector.setCurrencies(Currency.getCurrencies());
            unit1 = new JLabel();
            unit2 = new JLabel();
            unit3 = new JLabel();
            unit4 = new JLabel();
            fractureUnit1 = new JLabel();
            fractureUnit2 = new JLabel();
            fractureUnit3 = new JLabel();

            mileage = new JTextField();
            mileage.setEditable(false);
            mileage.setHorizontalAlignment(JTextField.RIGHT);

            fuelQuantity = new JTextField();
            fuelQuantity.setEditable(false);
            fuelQuantity.setHorizontalAlignment(JTextField.RIGHT);

            fuelCosts = new JTextField();
            fuelCosts.setEditable(false);
            fuelCosts.setHorizontalAlignment(JTextField.RIGHT);

            maintenanceCosts = new JTextField();
            maintenanceCosts.setEditable(false);
            maintenanceCosts.setHorizontalAlignment(JTextField.RIGHT);

            totalCosts = new JTextField();
            totalCosts.setEditable(false);
            totalCosts.setHorizontalAlignment(JTextField.RIGHT);

            avFuelCosts = new JTextField();
            avFuelCosts.setEditable(false);
            avFuelCosts.setHorizontalAlignment(JTextField.RIGHT);

            avMaintenanceCosts = new JTextField();
            avMaintenanceCosts.setEditable(false);
            avMaintenanceCosts.setHorizontalAlignment(JTextField.RIGHT);

            avTotalCosts = new JTextField();
            avTotalCosts.setEditable(false);
            avTotalCosts.setHorizontalAlignment(JTextField.RIGHT);

            averageMileage = new JTextField();
            averageMileage.setEditable(false);
            averageMileage.setHorizontalAlignment(JTextField.RIGHT);

            averageQuantity = new JTextField();
            averageQuantity.setEditable(false);
            averageQuantity.setHorizontalAlignment(JTextField.RIGHT);

            averageFuelQuantityCosts = new JTextField();
            averageFuelQuantityCosts.setEditable(false);
            averageFuelQuantityCosts.setHorizontalAlignment(JTextField.RIGHT);

            GridBagHelper.constrain(this, new JLabel(CarCosts.getBundle().getString("car-car-label")),
                    0, 0, 1, 1, 8, 2, 2, 2);
            GridBagHelper.constrain(this, name, 1, 0, 7, 1,
                    GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH,
                    1.0, 0.0, 6, 2, 2, 2);
            GridBagHelper.constrain(this, new JLabel(CarCosts.getBundle().getString("car-sign-label")),
                    0, 1, 1, 1, 2, 2, 8, 2);
            GridBagHelper.constrain(this, sign, 1, 1, 7, 1,
                    GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH,
                    1.0, 0.0, 0, 2, 8, 2);

            GridBagHelper.constrain(this, new JLabel(CarCosts.getBundle().getString("car-range-label")),
                    0, 2, 1, 1, 2, 2, 2, 2);

            JPanel rangePanel = new JPanel();
            rangePanel.setLayout(gridbag);
            GridBagHelper.constrain(rangePanel, new JLabel(CarCosts.getBundle().getString("car-range-from-label")),
                    1, 2, 1, 1, 2, 2, 2, 2);
            GridBagHelper.constrain(rangePanel, from, 2, 2, 1, 1,
                    GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHEAST,
                    1.0, 0.0, 0, 2, 2, 2);
            GridBagHelper.constrain(rangePanel, new JLabel(CarCosts.getBundle().getString("car-range-to-label")),
                    4, 2, 1, 1, 2, 2, 2, 2);
            GridBagHelper.constrain(rangePanel, to, 5, 2, 2, 1,
                    GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHEAST,
                    1.0, 0.0, 0, 2, 2, 2);
/* do later if it all
      GridBagHelper.constrain(rangePanel, listenerMgr.createJButton("evaluate-time-range"),
			      7, 2, 1, 1,
			      GridBagConstraints.NONE, GridBagConstraints.CENTER,
			      0.2, 0.2, 0, 2, 0, 2);
*/
            GridBagHelper.constrain(this, rangePanel,
                    1, 2, 7, 1,
                    GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER,
                    1.0, 0.0, 0, 0, 0, 0);

            GridBagHelper.constrain(this, new JLabel(CarCosts.getBundle().getString("car-currency-label")),
                    0, 3, 1, 1, 7, 2, 2, 2);
            GridBagHelper.constrain(this, currencySelector,
                    1, 3, 3, 1, 5, 0, 6, 0);

            GridBagHelper.constrain(this, new JLabel(CarCosts.getBundle().getString("car-mileage-label")),
                    0, 4, 1, 1, 8, 2, 2, 2);
            GridBagHelper.constrain(this, mileage, 1, 4, 3, 1,
                    GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHEAST,
                    1.0, 0.0, 6, 2, 2, 2);
            GridBagHelper.constrain(this, new JLabel(CarCosts.getBundle().getString("car-mileage-unit")),
                    4, 4, 2, 1,
                    8, 2, 2, 2);
            GridBagHelper.constrain(this, new JLabel(CarCosts.getBundle().getString("car-average-mileage-label")),
                    5, 4, 1, 1,
                    8, 12, 2, 2);
            GridBagHelper.constrain(this, averageMileage, 6, 4, 1, 1,
                    GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH,
                    1.0, 0.0, 6, 2, 2, 2);
            GridBagHelper.constrain(this, new JLabel(CarCosts.getBundle().getString("car-mileage-unit")),
                    7, 4, 1, 1,
                    8, 2, 2, 2);


            GridBagHelper.constrain(this, new JLabel(CarCosts.getBundle().getString("car-quantity-label")),
                    0, 5, 1, 1, 2, 2, 2, 2);
            GridBagHelper.constrain(this, fuelQuantity, 1, 5, 3, 1,
                    GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH,
                    1.0, 0.0, 0, 2, 2, 2);
            GridBagHelper.constrain(this, new JLabel(CarCosts.getBundle().getString("car-quantity-unit")),
                    4, 5, 1, 1,
                    2, 2, 2, 2);
            GridBagHelper.constrain(this, new JLabel(CarCosts.getBundle().getString("car-average-quantity-label")),
                    5, 5, 1, 1,
                    2, 12, 2, 2);
            GridBagHelper.constrain(this, averageQuantity, 6, 5, 1, 1,
                    GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH,
                    1.0, 0.0, 0, 2, 2, 2);
            GridBagHelper.constrain(this, new JLabel(CarCosts.getBundle().getString("car-quantity-unit")),
                    7, 5, 1, 1,
                    2, 2, 2, 2);


            GridBagHelper.constrain(this, new JLabel(CarCosts.getBundle().getString("car-average-fuel-quantity-cost-label")),
                    5, 6, 1, 1,
                    2, 12, 2, 2);
            GridBagHelper.constrain(this, averageFuelQuantityCosts, 6, 6, 1, 1,
                    GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH,
                    1.0, 0.0, 0, 2, 2, 2);
            GridBagHelper.constrain(this, unit4,
                    7, 6, 1, 1,
                    2, 2, 2, 2);


            GridBagHelper.constrain(this, new JLabel(CarCosts.getBundle().getString("car-fuelcosts-label")),
                    0, 7, 1, 1,
                    2, 2, 2, 2);
            GridBagHelper.constrain(this, fuelCosts, 1, 7, 3, 1,
                    GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH,
                    1.0, 0.0, 0, 2, 2, 2);
            GridBagHelper.constrain(this, unit1,
                    4, 7, 1, 1,
                    2, 2, 2, 2);
            GridBagHelper.constrain(this, new JLabel(CarCosts.getBundle().getString("car-average-cost-label")),
                    5, 7, 1, 1,
                    2, 12, 2, 2);
            GridBagHelper.constrain(this, avFuelCosts, 6, 7, 1, 1,
                    GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH,
                    1.0, 0.0, 0, 2, 2, 2);
            GridBagHelper.constrain(this, fractureUnit1,
                    7, 7, 1, 1,
                    2, 2, 2, 2);


            GridBagHelper.constrain(this, new JLabel(CarCosts.getBundle().getString("car-maintenancecosts-label")),
                    0, 8, 1, 1,
                    2, 2, 2, 2);
            GridBagHelper.constrain(this, maintenanceCosts, 1, 8, 3, 1,
                    GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH,
                    1.0, 0.0, 0, 2, 2, 2);
            GridBagHelper.constrain(this, unit2,
                    4, 8, 1, 1,
                    2, 2, 2, 2);
            GridBagHelper.constrain(this, new JLabel(CarCosts.getBundle().getString("car-average-cost-label")),
                    5, 8, 1, 1,
                    2, 12, 2, 2);
            GridBagHelper.constrain(this, avMaintenanceCosts, 6, 8, 1, 1,
                    GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH,
                    1.0, 0.0, 0, 2, 2, 2);
            GridBagHelper.constrain(this, fractureUnit2,
                    7, 8, 1, 1,
                    2, 2, 2, 2);


            GridBagHelper.constrain(this, new JLabel(CarCosts.getBundle().getString("car-costs-label")),
                    0, 9, 1, 1,
                    2, 2, 12, 2);
            GridBagHelper.constrain(this, totalCosts, 1, 9, 3, 1,
                    GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH,
                    1.0, 0.0, 0, 2, 12, 2);
            GridBagHelper.constrain(this, unit3,
                    4, 9, 1, 1,
                    2, 2, 12, 2);
            GridBagHelper.constrain(this, new JLabel(CarCosts.getBundle().getString("car-average-cost-label")),
                    5, 9, 1, 1,
                    2, 12, 12, 2);
            GridBagHelper.constrain(this, avTotalCosts, 6, 9, 1, 1,
                    GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH,
                    1.0, 0.0, 0, 2, 12, 2);
            GridBagHelper.constrain(this, fractureUnit3,
                    7, 9, 1, 1,
                    2, 2, 12, 2);

            name.requestFocus();
        }

        public void setCar(Car car) {
            StringModel imName = car.getNameModel();
            StringModelToBoundedDocumentAdapter adapName = new
                    StringModelToBoundedDocumentAdapter(imName, 50);
            imName.addChangeListener(new AdapterDelegateMediator(adapName));
            name.setDocument(adapName);

            StringModel imSign = car.getSignModel();
            StringModelToBoundedDocumentAdapter adapSign = new
                    StringModelToBoundedDocumentAdapter(imSign, 50);
            imSign.addChangeListener(new AdapterDelegateMediator(adapSign));
            sign.setDocument(adapSign);

            CalendarModel imFrom = car.getFromDateModel();
            CalendarModelToBoundedDocumentAdapter adapFrom = new
                    CalendarModelToBoundedDocumentAdapter(imFrom, "dd.MM.yyyy");
            imFrom.addChangeListener(new AdapterDelegateMediator(adapFrom));
            from.setDocument(adapFrom);

            CalendarModel imTo = car.getToDateModel();
            CalendarModelToBoundedDocumentAdapter adapTo = new
                    CalendarModelToBoundedDocumentAdapter(imTo, "dd.MM.yyyy");
            imTo.addChangeListener(new AdapterDelegateMediator(adapTo));
            to.setDocument(adapTo);

            currencySelector.setModel(car.getCurrencyModel());
            updateCurrencies();

            IntegerModel imMileage = car.getMileageModel();
            IntegerModelToBoundedDocumentAdapter adapMileage = new
                    IntegerModelToBoundedDocumentAdapter(imMileage, 10);
            imMileage.addChangeListener(new AdapterDelegateMediator(adapMileage));
            mileage.setDocument(adapMileage);

            DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.GERMAN);
            decimalFormat.applyPattern("###,##0.00");

            DoubleModel imFuelQuantity = car.getFuelQuantityModel();
            DoubleModelToBoundedDocumentAdapter adapFuelQuantity = new
                    DoubleModelToBoundedDocumentAdapter(imFuelQuantity, decimalFormat);
            fuelQuantity.setDocument(adapFuelQuantity);
            imFuelQuantity.addChangeListener(new AdapterDelegateMediator(adapFuelQuantity));

            DoubleModel imFuelCosts = car.getTotalFuelCostsModel();
            DoubleModelToBoundedDocumentAdapter adapFuelCosts = new
                    DoubleModelToBoundedDocumentAdapter(imFuelCosts, decimalFormat);
            fuelCosts.setDocument(adapFuelCosts);
            imFuelCosts.addChangeListener(new AdapterDelegateMediator(adapFuelCosts));

            DoubleModel imMaintenanceCosts = car.getTotalMaintenanceCostsModel();
            DoubleModelToBoundedDocumentAdapter adapMaintenanceCosts = new
                    DoubleModelToBoundedDocumentAdapter(imMaintenanceCosts, decimalFormat);
            maintenanceCosts.setDocument(adapMaintenanceCosts);
            imMaintenanceCosts.addChangeListener(new AdapterDelegateMediator(adapMaintenanceCosts));

            DoubleModel imTotalCosts = car.getTotalCostsModel();
            DoubleModelToBoundedDocumentAdapter adapTotalCosts = new
                    DoubleModelToBoundedDocumentAdapter(imTotalCosts, decimalFormat);
            totalCosts.setDocument(adapTotalCosts);
            imTotalCosts.addChangeListener(new AdapterDelegateMediator(adapTotalCosts));

            DoubleModel imAvFuelCosts = car.getAverageFuelCostsModel();
            DoubleModelToBoundedDocumentAdapter adapAvFuelCosts = new
                    DoubleModelToBoundedDocumentAdapter(imAvFuelCosts, decimalFormat);
            avFuelCosts.setDocument(adapAvFuelCosts);
            imAvFuelCosts.addChangeListener(new AdapterDelegateMediator(adapAvFuelCosts));

            DoubleModel imAvMaintenanceCosts = car.getAverageMaintenanceCostsModel();
            DoubleModelToBoundedDocumentAdapter adapAvMaintenanceCosts = new
                    DoubleModelToBoundedDocumentAdapter(imAvMaintenanceCosts, decimalFormat);
            avMaintenanceCosts.setDocument(adapAvMaintenanceCosts);
            imAvMaintenanceCosts.addChangeListener(new AdapterDelegateMediator(adapAvMaintenanceCosts));

            DoubleModel imAvTotalCosts = car.getAverageTotalCostsModel();
            DoubleModelToBoundedDocumentAdapter adapAvTotalCosts = new
                    DoubleModelToBoundedDocumentAdapter(imAvTotalCosts, decimalFormat);
            avTotalCosts.setDocument(adapAvTotalCosts);
            imAvTotalCosts.addChangeListener(new AdapterDelegateMediator(adapAvTotalCosts));

            DoubleModel imAverageMileage = car.getAverageMileageModel();
            DoubleModelToBoundedDocumentAdapter adapAverageMileage = new
                    DoubleModelToBoundedDocumentAdapter(imAverageMileage, decimalFormat);
            averageMileage.setDocument(adapAverageMileage);
            imAverageMileage.addChangeListener(new AdapterDelegateMediator(adapAverageMileage));

            DoubleModel imAverageQuantity = car.getAverageQuantityModel();
            DoubleModelToBoundedDocumentAdapter adapAverageQuantity = new
                    DoubleModelToBoundedDocumentAdapter(imAverageQuantity, decimalFormat);
            averageQuantity.setDocument(adapAverageQuantity);
            imAverageQuantity.addChangeListener(new AdapterDelegateMediator(adapAverageQuantity));

            DoubleModel imAverageFuelQuantityCosts = car.getAverageFuelQuantityCostsModel();
            DoubleModelToBoundedDocumentAdapter adapAverageFuelQuantityCosts = new
                    DoubleModelToBoundedDocumentAdapter(imAverageFuelQuantityCosts, decimalFormat);
            averageFuelQuantityCosts.setDocument(adapAverageFuelQuantityCosts);
            imAverageFuelQuantityCosts.addChangeListener(new AdapterDelegateMediator(adapAverageFuelQuantityCosts));
        }

        public void updateCurrencies() {
            unit1.setText(car.getCurrency().getName());
            unit2.setText(car.getCurrency().getName());
            unit3.setText(car.getCurrency().getName());
            unit4.setText(car.getCurrency().getName());
            fractureUnit1.setText(car.getCurrency().getFracture());
            fractureUnit2.setText(car.getCurrency().getFracture());
            fractureUnit3.setText(car.getCurrency().getFracture());
        }

        // --- inner classes ---------------------------------------

        private JTextField name;
        private JTextField sign;
        private JTextField from;
        private JTextField to;

        private CurrencySelector currencySelector;
        private JLabel unit1, unit2, unit3, unit4;
        private JLabel fractureUnit1, fractureUnit2, fractureUnit3;

        private JTextField mileage;
        private JTextField fuelQuantity;
        private JTextField fuelCosts;
        private JTextField maintenanceCosts;
        private JTextField totalCosts;
        private JTextField avFuelCosts;
        private JTextField avMaintenanceCosts;
        private JTextField avTotalCosts;
        private JTextField averageMileage;
        private JTextField averageQuantity;
        private JTextField averageFuelQuantityCosts;
    }

    /**
     * Listen to frame events and change the window menu of the explorer
     */
    public class WindowMenuUpdater implements FrameListener {

        /**
         * Number of entries to skip.
         */
        private static final int START_OFFSET = 0;

        public WindowMenuUpdater(FrameManager frameManager) {
            frameManager.addFrameListener(this);
        }

        /**
         * Handle the frame events.
         */
        public synchronized void frameOpened(FrameEvent e) {
            Frame frame = e.getSource();
            windowMenu.add(listenerMgr.createJMenuItem("activate-window",
                    frame.getTitle()));
        }

        /**
         * Handle the frame events.
         */
        public synchronized void frameClosed(FrameEvent e) {
            // remove the entry (+START_OFFSET to compensate first entries until separator)
            windowMenu.remove(e.getIndex() + START_OFFSET);
        }

        /**
         * Handle the frame events.
         */
        public synchronized void frameChangedTitle(FrameEvent e) {
            Frame frame = e.getSource();

            // update entry (+START_OFFSET to compensate first entries until separator)
            JMenuItem item = (JMenuItem) windowMenu.getMenuComponent(e.getIndex() + START_OFFSET);
            item.setText(frame.getTitle());
        }
    }

    public class CarFilter extends javax.swing.filechooser.FileFilter {
        public boolean accept(File file) {
            String extension = Files.getExtension(file);
            return file.isDirectory() ||
                    extension.equalsIgnoreCase(CarCosts.getBundle().getString("car-filter-extension"));
        }

        public String getDescription() {
            return CarCosts.getBundle().getString("car-filter");
        }
    }

    private static File findExistingPath(File path) {
        while (path != null && !path.exists()) {
            path = path.getParentFile();
        }
        return path != null && path.exists() ? path : null;
    }

    public File getLastDirectoryPreference() {
        File path = new File(preferences.get(LAST_DIRECTORY_PREFERENCE, ""));
        return findExistingPath(path);
    }

    public void setLastDirectoryPreference(File path) {
        preferences.put(LAST_DIRECTORY_PREFERENCE, path.getPath());
    }

    // --- Inner classes for actions --------------------------------

    /**
     * An action, which loads a car.
     */
    public class LoadCarAction extends AbstractAction {

        /**
         * Construct a new action.
         */
        public LoadCarAction() {
            super("load-car");
        }

        /**
         * Process the event.
         *
         * @param e the action event
         */
        public void actionPerformed(ActionEvent e) {
            JFileChooser c = createJFileChooser();
            CarFilter carFilter = new CarFilter();
            c.addChoosableFileFilter(carFilter);
            c.setFileFilter(carFilter);
            c.setFileSelectionMode(FILES_ONLY);
            c.setDialogTitle(CarCosts.getBundle().getString("load-car-title"));
            if (file == null)
                c.setCurrentDirectory(getLastDirectoryPreference());
            else
                c.setCurrentDirectory(file);

            if (c.showOpenDialog(null) == APPROVE_OPTION) {
                File file = c.getSelectedFile();
                setLastDirectoryPreference(file);

                try {
                    Car car = new Car(file);
                    setCar(file, car);
                } catch (IOException ie) {
                    ie.printStackTrace();

                    JOptionPane.showMessageDialog(null, Util.formatString(CarCosts.getBundle().getString("load-car-failed"),
                            new Object[]{file.getName(), ie}),
                            CarCosts.getBundle().getString("carcosts-title"),
                            ERROR_MESSAGE);
                }
            }
        }
    }

    private File getFile() {
        JFileChooser c = createJFileChooser();
        CarFilter carFilter = new CarFilter();
        c.addChoosableFileFilter(carFilter);
        c.setFileFilter(carFilter);
        c.setFileSelectionMode(FILES_ONLY);
        c.setDialogTitle(CarCosts.getBundle().getString("save-car-title"));
        if (file == null)
            c.setCurrentDirectory(getLastDirectoryPreference());
        else
            c.setCurrentDirectory(file);

        if (c.showSaveDialog(null) == APPROVE_OPTION) {
            File file = c.getSelectedFile();
            setLastDirectoryPreference(file);

            String extension = Files.getExtension(file);
            if (!extension.equalsIgnoreCase(CarCosts.getBundle().getString("car-filter-extension"))) {
                file = new File(file.getParent(), file.getName() + "." + CarCosts.getBundle().getString("car-filter-extension"));
            }

            return file;
        }

        return null;
    }

    public void save() {
        if (file == null)
            saveAs();
        else
            save(file);
    }

    private void saveAs() {
        File file = getFile();

        // file was choosen
        if (file != null) {

            // if choosen file exists, query user before overwriting
            if (file.exists()) {
                int option = JOptionPane.showConfirmDialog(frame,
                        Util.formatString(CarCosts.getBundle().getString("save-car-overwrite"),
                                new Object[]{file.getName()}),
                        CarCosts.getBundle().getString("carcosts-title"),
                        JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.NO_OPTION)
                    return;
            }

            save(file);
        }
    }

    private void save(File file) {
        if (file == null)
            return;

        try {
            car.writeCsv(file);
            car.writeAmigaCarCosts(file);
            setCar(file, car);
        } catch (IOException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(null, Util.formatString(CarCosts.getBundle().getString("save-car-failed"),
                    new Object[]{file.getName(), e}),
                    CarCosts.getBundle().getString("carcosts-title"),
                    ERROR_MESSAGE);
        }
    }

    // --- inner classes ---------------------------------------

    class SaveChangeForwarder implements ChangeListener {
        BooleanModel delegate;

        public void setDelegate(BooleanModel newDelegate) {
            if (delegate != null)
                delegate.removeChangeListener(this);

            this.delegate = newDelegate;

            if (delegate != null)
                delegate.addChangeListener(this);
        }

        public void stateChanged(ChangeEvent e) {
            saveModel.setState(delegate.getState());
        }
    }

    class CurrencyChangeForwarder implements ChangeListener {
        CurrencyModel delegate;

        public void setDelegate(CurrencyModel newDelegate) {
            if (delegate != null)
                delegate.removeChangeListener(this);

            this.delegate = newDelegate;

            if (delegate != null)
                delegate.addChangeListener(this);
        }

        public void stateChanged(ChangeEvent e) {
            pane.updateCurrencies();
        }
    }

    /**
     * An action, which saves a car.
     */
    public class SaveAsCarAction extends AbstractAction {

        /**
         * Construct a new action.
         */
        public SaveAsCarAction() {
            super("save-as-car");
        }

        /**
         * Process the event.
         *
         * @param e the action event
         */
        public void actionPerformed(ActionEvent e) {
            saveAs();
        }
    }


    /**
     * An action, which saves a car.
     */
    public class SaveCarAction extends AbstractAction {

        /**
         * Construct a new action.
         */
        public SaveCarAction() {
            super("save-car");
        }

        /**
         * Process the event.
         *
         * @param e the action event
         */
        public void actionPerformed(ActionEvent e) {
            save();
        }
    }

    /**
     * An action, which edits the fillngs.
     */
    public class EditFuelCostsAction extends AbstractAction {

        /**
         * Construct a new action.
         */
        public EditFuelCostsAction() {
            super("edit-fuel-costs");
        }

        /**
         * Process the event.
         *
         * @param e the action event
         */
        public void actionPerformed(ActionEvent e) {
            RegistredJFrame frame = new RegistredJFrame(CarCosts.getFrameManager(),
                    CarCosts.getBundle().getString("fuelcost-title"),
                    CarCosts.getNextFrameCount());
            if (car == null) {
                setCar(null, new Car());
            }

            FuelCostView view = new FuelCostView(car);
            view.setFrame(frame);

            frame.setIconImage(CarCosts.getBundle().getIcon("fuelcost-image").getImage());
            frame.getContentPane().add("Center", view);
            frame.setSize(600, 450);
            frame.setVisible(true);
        }
    }

    /**
     * An action, which edits the maintenances.
     */
    public class EditMaintenanceCostsAction extends AbstractAction {

        /**
         * Construct a new action.
         */
        public EditMaintenanceCostsAction() {
            super("edit-maintenance-costs");
        }

        /**
         * Process the event.
         *
         * @param e the action event
         */
        public void actionPerformed(ActionEvent e) {
            RegistredJFrame frame = new RegistredJFrame(CarCosts.getFrameManager(),
                    CarCosts.getBundle().getString("maintenancecost-title"),
                    CarCosts.getNextFrameCount());
            if (car == null) {
                setCar(null, new Car());
            }

            MaintenanceCostView view = new MaintenanceCostView(car);
            view.setFrame(frame);

            frame.setIconImage(CarCosts.getBundle().getIcon("maintenancecost-image").getImage());
            frame.getContentPane().add("Center", view);
            frame.setSize(640, 450);
            frame.setVisible(true);
        }
    }

    // --- member variables ------------------------------------

    private RegistredJFrame frame;
    private JMenuBar menuBar;
    private JMenu windowMenu;
    private CarPane pane;

    private ListenerManager listenerMgr;
    private Action[] actions = null;

    private BooleanModel saveModel = new DefaultBooleanModel();
    private SaveChangeForwarder saveModelListener = new SaveChangeForwarder();
    private CurrencyChangeForwarder currencyModelListener = new CurrencyChangeForwarder();

    private File file;
    private Car car;
}
