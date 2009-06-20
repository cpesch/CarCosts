package slash.gui.model;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * A bounded plain document that limits the size of the document.
 *
 * @see PlainDocument
 */

public class BoundedDocument extends PlainDocument {

    /**
     * Constructs a bounded plain text document.
     */
    public BoundedDocument(int maximumSize) {
        super();
        this.maximumSize = maximumSize;
    }

    /**
     * Get the maximum size of the BoundedDocument.
     *
     * @return the maximum size
     */
    public int getMaximumSize() {
        return maximumSize;
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
        if (getLength() < maximumSize) {
            super.insertString(offs, str, a);
        } else {
            throw new BadLocationException("Bounded size reached at offset: ", offs);
        }
    }

    // --- member variables ------------------------------------

    private int maximumSize;
}
