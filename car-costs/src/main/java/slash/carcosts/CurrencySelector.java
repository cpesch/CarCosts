/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 1998-2006 Christian Pesch. All Rights Reserved.
*/

package slash.carcosts;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * This class allows selecting a Currency.
 *
 * @author Christian Pesch
 */

public class CurrencySelector extends JComboBox {

    /**
     * Construct a new selector.
     */
    public CurrencySelector() {
        setRenderer(new DefaultRenderer());
    }

    /**
     * Construct a new selector with the given currencies.
     */
    public void setCurrencies(List<Currency> currencies) {
        this.setModel(new CurrencyComboBoxModel(currencies));
    }

    public void setModel(CurrencyModel model) {
        this.model = model;
        setSelected(model.getValue());
    }

    /**
     * Return the selected Currency.
     */
    public Currency getSelected() {
        return (Currency) getSelectedItem();
    }

    /**
     * Preselected the given Currency.
     */
    public void setSelected(Currency selected) {
        setSelectedItem(selected);
    }

    /**
     * Detach me.
     */
    public void detach() {
        setRenderer(new DefaultListCellRenderer());
        removeAll();
    }

    private class CurrencyComboBoxModel
            extends AbstractListModel
            implements ComboBoxModel {

        CurrencyComboBoxModel(List<Currency> currencies) {
            this.currencies = currencies;
        }

        public Object getElementAt(int i) {
            return currencies.get(i);
        }

        public int getSize() {
            return currencies.size();
        }

        public Object getSelectedItem() {
            if (model != null)
                return model.getValue();
            else
                return null;
        }

        public void setSelectedItem(Object item) {
            if (model != null)
                model.setValue((Currency) item);
        }

        private List<Currency> currencies;
    }

    private class DefaultRenderer extends DefaultListCellRenderer {

        /**
         * Return a component that has been configured to display the
         * specified value.
         */
        public Component getListCellRendererComponent(JList list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {

            setComponentOrientation(list.getComponentOrientation());

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            // text
            Currency currency = (Currency) value;
            setText(currency != null ? currency.getName() : "?");
            // icon
            setIcon(null);

            this.setEnabled(list.isEnabled());

            setFont(list.getFont());
            setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

            return this;
        }
    }

    // --- member variables ------------------------------------

    private CurrencyModel model;
}
