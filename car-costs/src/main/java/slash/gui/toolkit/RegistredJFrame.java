package slash.gui.toolkit;

import javax.swing.*;

/**
 * A frame that is registred in the <code>FrameRegistry</code>. Use this frame
 * if you want it displayed in the list of frames that clients of the
 * FrameRegistry use. Normally, a RegistredJFrame is created by the
 * FrameManager, where it registers and unregisters itself.  Furthermore by
 * dividing the title of the frame into program name, document name and frame
 * count, it's possible to update them independantly.
 */

public class RegistredJFrame extends JFrame {

    /**
     * Construct a new registred frame.
     *
     * @param frameMgr    the FrameManager where to register/unregister
     * @param programName the programName of the frame
     * @param count       the count of the frame
     */
    public RegistredJFrame(FrameManager frameMgr, String programName, int count) {
        super(programName + " :" + count);

        this.frameMgr = frameMgr;
        this.programName = programName;
        this.count = count;

        frameMgr.registerFrame(this);
    }

    /**
     * Dispose the registred frame.
     */
    public void dispose() {
        frameMgr.unregisterFrame(this);

        super.dispose();
    }

    /**
     * Set the title of the registred frame.
     */
    public void setTitle(String title) {
        // first, set new title
        super.setTitle(title);

        // set the name of the frame
        setName(title);

        // second, notify listeners
        frameMgr.changeTitleForFrame(this);
    }

    // --- added methods ---------------------------------------

    /**
     * Get the program name of the registred frame.
     *
     * @return the program name of the frame
     */
    public String getProgramName() {
        return programName;
    }

    /**
     * Get the program name of the registred frame.
     *
     * @param programName the program name of the frame
     */
    public void setProgramName(String programName) {
        this.programName = programName;

        setFormattedTitle();
    }


    /**
     * Get the document name of the registred frame.
     *
     * @return the name of the document in the frame
     */
    public String getDocumentName() {
        return documentName;
    }

    /**
     * Set the document name of the registred frame. This method
     * assembles a Windows like title string for the frame.
     */
    public void setDocumentName(String documentName) {
        this.documentName = documentName;

        setFormattedTitle();
    }


    /**
     * Set the formatted title of the frame. This is put
     * together by program name, document name and frame count.
     */
    protected void setFormattedTitle() {
        if (documentName.length() > 0)
            setTitle(programName + " - " + documentName + " :" + count);
        else if (count > 0)
            setTitle(programName + " :" + count);
        else
            setTitle(programName);
    }


    /**
     * Get the user data, an <code>Object</code>, by which this frame can be
     * retrieved later, for the frame.
     *
     * @return the user data
     */
    public Object getUserData() {
        return data;
    }

    /**
     * Set the user data, an <code>Object</code>, by which this frame can be
     * retrieved later, for the frame.
     *
     * @param data the user data of the frame
     */
    public void setUserData(Object data) {
        this.data = data;
    }

    // --- member variables ------------------------------------

    private FrameManager frameMgr;
    private String programName;
    private String documentName;
    private int count;
    private Object data = null;
}
