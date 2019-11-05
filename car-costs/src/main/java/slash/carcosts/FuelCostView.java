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
import slash.util.Util;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

/**
 * This is a view of the fuel costs.
 *
 * @author Christian Pesch
 */

public class FuelCostView extends ManagedJPanel {

    public FuelCostView(Car car) {
        super();

        // initialization
        TypedResourceBundle bundle = CarCosts.getBundle();

        ActionManager actionMgr = new ActionManager(CarCosts.getActionManager());
        listenerMgr = new ListenerManager(bundle, actionMgr);
        setListenerManager(listenerMgr);
        actionMgr.addActions(getActions());

        // create menu-, toolbar
        menuBar = createMenuBar();
        JToolBar toolBar = createToolBar();

        unit1 = new JLabel();
        unit1.setHorizontalAlignment(SwingConstants.RIGHT);

        setCar(car);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new GridLayout(1, 7));
        headerPanel.add(new JLabel(CarCosts.getBundle().getString("fuelcost-date-label")));
        headerPanel.add(new JLabel(CarCosts.getBundle().getString("fuelcost-mileage-label"),
                SwingConstants.RIGHT));
        headerPanel.add(new JLabel(CarCosts.getBundle().getString("fuelcost-quantity-label"),
                SwingConstants.RIGHT));
        headerPanel.add(new JLabel(CarCosts.getBundle().getString("fuelcost-cost-label"),
                SwingConstants.CENTER));
        headerPanel.add(new JLabel(CarCosts.getBundle().getString("fuelcost-difference-label"),
                SwingConstants.RIGHT));
        headerPanel.add(new JLabel(CarCosts.getBundle().getString("fuelcost-average-quantity-label"),
                SwingConstants.RIGHT));
        headerPanel.add(unit1);

        GridBagLayout gridbag = new GridBagLayout();

        JScrollPane scroller = new
                JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // increase speed of scrolling to a factor of 8
        scroller.getVerticalScrollBar().setUnitIncrement(80);
        scroller.getHorizontalScrollBar().setUnitIncrement(80);

        JViewport port = scroller.getViewport();

        fillingList = new JList();
        fillingList.setModel(new SortingListModel(car.getFuelCosts(), new FillingComparator()));
        fillingList.setCellRenderer(new FuelListRenderer(car));
        port.add(fillingList);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(gridbag);
        GridBagHelper.constrain(actionPanel, listenerMgr.createJButton("new-filling"), 0, 0, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST,
                1.0, 0.0, 0, 0, 0, 0);
        GridBagHelper.constrain(actionPanel, listenerMgr.createJButton("edit-filling"), 1, 0, 1, 1,
                GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST,
                1.0, 0.0, 0, 0, 0, 0);
        GridBagHelper.constrain(actionPanel, listenerMgr.createJButton("delete-filling"), 2, 0, 1, 1,
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

    public void setCar(Car newCar) {
        this.car = newCar;
        currencyModelListener.setDelegate(car.getCurrencyModel());
        updateCurrencies();
    }

    /**
     * Gets the actions for this view.
     *
     * @return the actions for the view
     */
    public Action[] getActions() {
        if (actions == null) {
            actions = new Action[]{
                    new NewFillingAction(),
                    new EditFillingAction(),
                    new DeleteFillingAction(),
                    new CloseWindowAction()
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
        menu.add(listenerMgr.createJMenuItem("new-filling"));
        menu.add(listenerMgr.createJMenuItem("edit-filling"));
        menu.add(listenerMgr.createJMenuItem("delete-filling"));
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

        toolBar.add(listenerMgr.createJButton("new-filling"));
        toolBar.add(listenerMgr.createJButton("edit-filling"));
        toolBar.add(listenerMgr.createJButton("delete-filling"));

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

    private void createFillingFrame(Filling filling) {
        RegistredJFrame frame = new RegistredJFrame(CarCosts.getFrameManager(),
                CarCosts.getBundle().getString("filling-title"),
                CarCosts.getNextFrameCount());

        FillingView view = new FillingView(car, filling);
        view.setFrame(frame);

        frame.setIconImage(CarCosts.getBundle().getIcon("filling-image").getImage());
        frame.getContentPane().add("Center", view);
        frame.setSize(270, 190);
        frame.setVisible(true);
    }

    // --- Inner classes --------------------------------------------

    public void updateCurrencies() {
        unit1.setText(Util.formatString(CarCosts.getBundle().getString("fuelcost-average-cost-label"),
                new Object[]{RendererHelper.formatCurrency(car.getCurrency())}));
        car.getFuelCosts().updateCurrencies();
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
            updateCurrencies();
        }
    }

    // --- Inner classes for actions --------------------------------

    /**
     * An action, which deletes the filling.
     */
    public class NewFillingAction extends AbstractAction {

        /**
         * Construct a new action.
         */
        public NewFillingAction() {
            super("new-filling");
        }

        /**
         * Process the event.
         *
         * @param e the action event
         */
        public void actionPerformed(ActionEvent e) {
            createFillingFrame(new Filling(car.getCurrency()));
        }
    }

    /**
     * An action, which edits the fillings.
     */
    public class EditFillingAction extends AbstractAction {

        /**
         * Construct a new action.
         */
        public EditFillingAction() {
            super("edit-filling");
        }

        /**
         * Process the event.
         *
         * @param e the action event
         */
        public void actionPerformed(ActionEvent e) {
            Object[] selectedValues = fillingList.getSelectedValues();
            for (Object selectedValue : selectedValues) {
                Filling filling = (Filling) selectedValue;
                createFillingFrame(filling);
            }
        }
    }

    /**
     * An action, which delete the fillings.
     */
    public class DeleteFillingAction extends AbstractAction {

        /**
         * Construct a new action.
         */
        public DeleteFillingAction() {
            super("delete-filling");
        }

        /**
         * Process the event.
         *
         * @param e the action event
         */
        public void actionPerformed(ActionEvent e) {
            Object[] selectedValues = fillingList.getSelectedValues();
            for (Object selectedValue : selectedValues) {
                Filling filling = (Filling) selectedValue;
                car.getFuelCosts().removeFilling(filling);
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
    private JList fillingList;
    private JLabel unit1;

    private ListenerManager listenerMgr;
    private Action[] actions = null;

    private Car car;

    private CurrencyChangeForwarder currencyModelListener = new CurrencyChangeForwarder();
}
