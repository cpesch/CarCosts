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
public class MaintenanceListRenderer implements ListCellRenderer {
    private static final int MAXIMUM_LABEL_LENGTH = 18;

    /**
     * Initialize.
     */
    public MaintenanceListRenderer() {
        panel = new JPanel();
        panel.setLayout(new GridLayout(1, 5));

        label1 = new JLabel();
        panel.add(label1);

        label2 = new JLabel();
        label2.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(label2);

        label3 = new JLabel();
        panel.add(label3);

        label4 = new JLabel();
        panel.add(label4);

        label5 = new JLabel();
        label5.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(label5);
    }

    /**
     * Render one entry.
     */
    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        Maintenance maintenance = (Maintenance) value;

        label1.setText(RendererHelper.formatDate(maintenance.getDate()));
        label2.setText(maintenance.getMileage() + " " +
                CarCosts.getBundle().getString("maintenancecost-mileage-unit-label") +
                "      "); // spacer

        String str = "  " + maintenance.getTitle();
        label3.setText(str.substring(0, Math.min(str.length(), MAXIMUM_LABEL_LENGTH)));

        str = "   " + maintenance.getNote();
        label4.setText(str.substring(0, Math.min(str.length(), MAXIMUM_LABEL_LENGTH)));

        label5.setText(RendererHelper.formatNumber(maintenance.getCosts()) + " " +
                RendererHelper.formatCurrency(maintenance.getCurrency()));

        panel.setBackground(isSelected ? Color.red : Color.white);
        panel.setForeground(isSelected ? Color.white : Color.black);

        return panel;
    }

    private JPanel panel;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;

}
