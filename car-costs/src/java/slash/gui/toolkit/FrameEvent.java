package slash.gui.toolkit;

import java.awt.*;

/**
 * The frame event, which indicates that something with
 * a registred frame has changed.
 */

public class FrameEvent {

    /**
     * Marks the first integer id for the range of frame event ids.
     */
    public static final int FRAME_FIRST = 1200;

    /**
     * Marks the last integer id for the range of frame event ids.
     */
    public static final int FRAME_LAST = 1202;

    /**
     * The frame opened event type.  This event is delivered only
     * the first time a frame is made visible.
     */
    public static final int FRAME_OPENED = FRAME_FIRST;

    /**
     * The frame closed event type. This event is delivered after
     * the frame has been closed as the result of a call to destroy.
     */
    public static final int FRAME_CLOSED = 1 + FRAME_FIRST;

    /**
     * The frame closed event type. This event is delivered after
     * the frame has been closed as the result of a call to destroy.
     */
    public static final int FRAME_CHANGED_TITLE = 2 + FRAME_FIRST;

    /**
     * Constructs a FrameEvent object with the specified source frame
     * and type.
     *
     * @param source the component where the event originated
     * @param id     the event type
     */
    public FrameEvent(Frame source, int index, int id) {
        this.source = source;
        this.index = index;
        this.id = id;
    }

    /**
     * Returns the frame that changed.
     */
    public Frame getSource() {
        return source;
    }

    /**
     * Returns the number of the frame that changed.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns the event type.
     */
    public int getID() {
        return id;
    }

    public String paramString() {
        String typeStr;
        switch (id) {
            case FRAME_OPENED:
                typeStr = "FRAME_OPENED";
                break;
            case FRAME_CLOSED:
                typeStr = "FRAME_CLOSED";
                break;
            case FRAME_CHANGED_TITLE:
                typeStr = "FRAME_CHANGED_TITLE";
                break;
            default:
                typeStr = "unknown type";
        }
        return typeStr;
    }

    // --- member variables ------------------------------------

    protected Frame source;
    protected int index;
    protected int id;
}
