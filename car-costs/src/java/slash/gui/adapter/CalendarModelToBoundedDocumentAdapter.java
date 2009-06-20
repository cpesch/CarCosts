package slash.gui.adapter;

import slash.gui.model.CalendarModel;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

/**
 * A bounded plain document that adapts a CalendarModel to a BoundedDocument or
 * visa versa. The date and time from the CalendarModel are show in the given
 * format, which is something like DD.MM.YYYY HH:MM.  The possible format
 * symbols are in java.text.SimpleDateFormat.
 *
 * @see slash.gui.model.BoundedDocument
 * @see slash.gui.adapter.BidirectionalAdapter
 */

public class CalendarModelToBoundedDocumentAdapter extends PlainDocument
        implements BidirectionalAdapter {

    private static Logger log = Logger.getLogger(CalendarModelToBoundedDocumentAdapter.class.getName());

    /**
     * Constructs a bounded text document that starts with a date.
     *
     * @param calendar the Calendar to show and edit
     * @param format   the Format of the date string
     */
    public CalendarModelToBoundedDocumentAdapter(CalendarModel calendar,
                                                 String format) {
        this.delegate = calendar;
        this.dateFormat = format;

        updateAdapterFromDelegate();
    }

    // --- helper methods ----------------------------------------

    /**
     * Tries to parse the given String and convert it to a date.
     */
    private Date parseDate(String dateString) throws ParseException {
        // check with the SimpleDateFormat
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        format.setLenient(true);

        return format.parse(dateString);
    }

    /**
     * Test if the given alphanumerical date string is a valid date string.
     * If so, update the delegate model with the new date.
     */
    private boolean isValidDateString(String dateString) {
        // check with the SimpleDateFormat
        Date date = null;
        try {
            date = parseDate(dateString);
        } catch (ParseException e) {
            return false;
        }

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        // prevent crazy years
        if (calendar.get(Calendar.YEAR) > 9999)
            return false;

        return true;
    }

    /**
     * Update the delegate after changes of the adapter.
     * This is the normal use, when the adapter is for
     * editing the delegate's content.
     */
    public void updateDelegateFromAdapter() {
        Calendar calendar = delegate.getValue();
        if (calendar == null)
            return;

        try {
            // read textfield
            String content = getText(0, getLength());

            // convert string to date
            Date date = parseDate(content);
            calendar.setTime(date);

            // set delegate
            delegate.setValue(calendar);
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
        Calendar calendar = delegate.getValue();
        if (calendar == null)
            return;

        try {
            // remove old content
            super.remove(0, getLength());

            // convert date to string, insert into text field
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            format.setCalendar(calendar);
            String dateString = format.format(calendar.getTime());
            super.insertString(0, dateString, null);
        } catch (BadLocationException e) {
            log.severe("error updating adapter: " + e.getMessage());
        }
    }

    // --- overridden methods -----------------------------------

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
        // create a buffer with a simulated delete
        String content = getText(0, getLength());
        StringBuffer buffer = new StringBuffer();
        buffer.append(content.substring(0, offs));
        buffer.append(content.substring(offs + len, content.length()));

        // System.out.println("Remove:"+buffer.toString());

        // test if buffer would be valid
        if (isValidDateString(buffer.toString())) {
            super.remove(offs, len);
            updateDelegateFromAdapter();
        } else
            throw new BadLocationException("Bad date:", offs);
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

        // check for numbers
        if (validValues.indexOf(str) == -1)
            throw new BadLocationException("No valid date entry at offset: ", offs);

        // create a buffer with a simulated insert
        StringBuffer buffer = new StringBuffer(getText(0, getLength()));
        buffer.insert(offs, str);

        // System.out.println("Insert:"+buffer.toString());

        // test if buffer would be valid
        if (isValidDateString(buffer.toString())) {

            // do the insert
            super.insertString(offs, str, a);
            updateDelegateFromAdapter();
        } else
            throw new BadLocationException("Bad date: ", offs);
    }

    // --- member variables ------------------------------------

    private CalendarModel delegate;
    private String dateFormat;
    private static final String validValues = "0123456789";
}
