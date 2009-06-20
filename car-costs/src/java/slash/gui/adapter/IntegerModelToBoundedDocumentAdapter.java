package slash.gui.adapter;

import slash.gui.model.BoundedDocument;
import slash.gui.model.IntegerModel;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import java.util.logging.Logger;

/**
 * A bounded plain document that limits the size of the document and
 * adapts an IntegerModel to a BoundedDocument or visa versa.
 *
 * @see BoundedDocument
 * @see BidirectionalAdapter
 */

public class IntegerModelToBoundedDocumentAdapter extends BoundedDocument
        implements BidirectionalAdapter {

    private static Logger log = Logger.getLogger(IntegerModelToBoundedDocumentAdapter.class.getName());

    /**
     * Constructs a bounded Integer text document.
     */
    public IntegerModelToBoundedDocumentAdapter(IntegerModel model,
                                                int maximumSize) {
        super(maximumSize);
        this.delegate = model;

        updateAdapterFromDelegate();
    }

    /**
     * Update the delegate after changes of the adapter.
     * This is the normal use, when the adapter is for
     * editing the delegate's content.
     */
    public void updateDelegateFromAdapter() {
        try {
            // read textfield
            String content = getText(0, getLength());

            // convert string into Integer, set delegate
            if (content.length() > 0)
                delegate.setValue(Integer.parseInt(content));
            else
                delegate.setValue(0);
        } catch (Exception e) {
            log.severe("error updating delegate: " + e.getMessage());
        }
    }

    /**
     * Update the adapter after changes of the delegate.
     * This is for inverse use, when the adapter displays
     * the delegate's content.
     */
    public void updateAdapterFromDelegate() {
        try {
            // remove old content
            super.remove(0, getLength());

            // convert int to string, insert into text field
            if (delegate.getValue() != 0) {
                super.insertString(0, Integer.toString(delegate.getValue()), null);
            }
        } catch (BadLocationException e) {
            log.severe("error updating adapter: " + e.getMessage());
        }
    }

    /**
     * Removes a portion of the content of the document.  This
     * will cause notification to be sent to the observers of
     * the document (unless an exception is thrown).
     *
     * @param offs the offset from the begining
     * @param len  the number of characters to remove
     * @throws BadLocationException some portion of the removal range
     *                              was not a valid part of the document.  The location in the exception
     *                              is the first bad position encountered.
     * @see DocumentEvent
     * @see DocumentListener
     */
    public void remove(int offs, int len) throws BadLocationException {
        super.remove(offs, len);
        updateDelegateFromAdapter();
    }


    /**
     * Updates document structure as a result of text insertion. If the maximum
     * size of the document is reached, an BadLocationException is thrown.
     *
     * @param a the set of attributes
     * @throws BadLocationException the given insert position is not a valid
     *                              position within the document
     */
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        // check of numbers
        if (validValues.indexOf(str) == -1)
            throw new BadLocationException("No number at offset: ", offs);

        // do the insert
        super.insertString(offs, str, a);
        updateDelegateFromAdapter();
    }

    // --- member variables ------------------------------------

    private IntegerModel delegate;
    private static final String validValues = "0123456789";
}
