package slash.gui.adapter;

import slash.gui.model.BoundedDocument;
import slash.gui.model.StringModel;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import java.util.logging.Logger;

/**
 * A bounded plain document that limits the size of the document and
 * adapts an StringModel to a BoundedDocument or visa versa.
 *
 * @see BoundedDocument
 * @see BidirectionalAdapter
 */

public class StringModelToBoundedDocumentAdapter extends BoundedDocument
        implements BidirectionalAdapter {

    private static Logger log = Logger.getLogger(StringModelToBoundedDocumentAdapter.class.getName());

    /**
     * Constructs a bounded String text document.
     */
    public StringModelToBoundedDocumentAdapter(StringModel model,
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
            // read textfield, set delegate
            delegate.setValue(getText(0, getLength()));
        } catch (BadLocationException e) {
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
            super.insertString(0, delegate.getValue(), null);
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
        // do the insert
        super.insertString(offs, str, a);
        updateDelegateFromAdapter();
    }

    // --- member variables ------------------------------------

    private StringModel delegate;
}
