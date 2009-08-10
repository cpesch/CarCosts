/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 1998-2006 Christian Pesch. All Rights Reserved.
*/

package slash.carcosts;

import javax.swing.*;
import java.awt.*;

/**
 * A specialised renderer for the list.
 *
 * @author Christian Pesch
 */
public class FuelListRenderer implements ListCellRenderer {

    /**
     * Initialize.
     */
    public FuelListRenderer(Car car) {
        this.car = car;

        panel = new JPanel();
        panel.setLayout(new GridLayout(1, 4));

        label1 = new JLabel();
        panel.add(label1);

        label2 = new JLabel();
        label2.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(label2);

        label3 = new JLabel();
        label3.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(label3);

        label4 = new JLabel();
        label4.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(label4);

        label5 = new JLabel();
        label5.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(label5);

        label6 = new JLabel();
        label6.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(label6);

        label7 = new JLabel();
        label7.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(label7);
    }

    /**
     * Return the filling before the given filling even it is at the same day.
     */
    private Filling getPreviousFilling(Filling filling) {
        Filling previous = null;
        for (Filling element : car.getFuelCosts().getFillings()) {
            if ((element.getDate().before(filling.getDate()) ||
                    !element.equals(filling) && element.getDate().equals(filling.getDate()))
                    && element.getMileage() <= filling.getMileage()) {
                previous = element;
            }
        }
        return previous;
    }

    /**
     * Render one entry.
     */
    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        Filling filling = (Filling) value;

        label1.setText(RendererHelper.formatDate(filling.getDate()));
        label2.setText(filling.getMileage() + " " +
                CarCosts.getBundle().getString("fuelcost-mileage-unit-label"));
        Filling previous = getPreviousFilling(filling);
        if (previous != null) {
            label3.setText(RendererHelper.formatNumber(filling.getQuantity()) + " " +
                    CarCosts.getBundle().getString("fuelcost-quantity-unit-label"));
            label4.setText(RendererHelper.formatNumber(filling.getCosts()) + " " +
                    RendererHelper.formatCurrency(filling.getCurrency()));
            int difference = filling.getMileage() - previous.getMileage();
            label5.setText(difference + " " +
                    CarCosts.getBundle().getString("fuelcost-mileage-unit-label"));
            double averageQuantity = filling.getQuantity() / difference * 100.0;
            label6.setText(RendererHelper.formatNumber(averageQuantity) + " " +
                    CarCosts.getBundle().getString("fuelcost-quantity-unit-label"));
            label7.setText(RendererHelper.formatPricePerLiter(car.getCurrency().fromEuro(filling.getAverageCosts())) + " " +
                    RendererHelper.formatCurrency(car.getCurrency()));
        } else {
            label3.setText("-");
            label4.setText("-");
            label5.setText("-");
            label6.setText("-");
            label7.setText("-");
        }

        panel.setBackground(isSelected ? Color.red : Color.white);
        panel.setForeground(isSelected ? Color.white : Color.black);

        return panel;
    }

    private Car car;
    private JPanel panel;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    private JLabel label6;
    private JLabel label7;
}
