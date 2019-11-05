package slash.gui.toolkit;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A FrameManager maintains a list of frames used by an application.
 * FrameListeners can connect to it and list to FrameEvents. For the
 * getFrame() method, a <code>RegistredJFrame</code> is assumed.
 */

public class FrameManager {

    /**
     * Create a new RegistredJFrame.
     *
     * @param programName the programName of the frame
     * @param count       the count of the frame
     * @return a RegistredJFrame, which registers and unregisters itself
     *         at this FrameManager
     */
    public RegistredJFrame createFrame(String programName, int count) {
        return new RegistredJFrame(this, programName, count);
    }

    /**
     * Register a frame in the frame manager.
     *
     * @param frame the frame to register
     */
    public synchronized void registerFrame(Frame frame) {
        frames.add(frame);
        fireFrameOpened(new FrameEvent(frame, frames.size() - 1,
                FrameEvent.FRAME_OPENED));
    }

    /**
     * Unregister a previously registred frame in the frame manager.
     *
     * @param frame frame to unregister
     */
    public synchronized void unregisterFrame(Frame frame) {
        int index = frames.indexOf(frame);

        // first remove element from list
        frames.remove(frame);

        // fire event only if frame was found
        if (index != -1)
            fireFrameClosed(new FrameEvent(frame, index,
                    FrameEvent.FRAME_CLOSED));
    }

    /**
     * Changed the name of a registred frame in the frame manager.
     *
     * @param frame frame whose title changed
     */
    public void changeTitleForFrame(Frame frame) {
        int index = frames.indexOf(frame);

        // fire event only if frame was found
        if (index != -1)
            fireFrameChangedTitle(new FrameEvent(frame, index,
                    FrameEvent.FRAME_CHANGED_TITLE));
    }


    /**
     * Gets a registred frame by its user data, an <code>Object</code>, by which
     * this frame can be retrieved later, for the frame.
     *
     * @param data the user data of the frame to get
     * @return the frame
     */
    public RegistredJFrame getFrameByUserData(Object data) {
        for (Iterator iterator = frames.iterator(); iterator.hasNext();) {
            Object object = iterator.next();

            // test for RegistredJFrames
            if (object instanceof RegistredJFrame) {
                RegistredJFrame frame = (RegistredJFrame) object;

                // check if the given user data equals the one of the frame
                Object frameData = frame.getUserData();
                if ((frameData != null) && (frameData.equals(data))) {
                    return frame;
                }
            }
        }

        return null;
    }

    /**
     * Gets a registred frame by its title.  The comparison is based on
     * the <code>indexOf</code> method of the String class and therefore
     * returns a frame, if the given title is a substring of the found
     * frame title.
     *
     * @param title the title of the frame
     * @return the frame
     */
    public Frame getFrameByTitle(String title) {
        for (Iterator iterator = frames.iterator(); iterator.hasNext();) {
            Frame frame = (Frame) iterator.next();

            // check if given title is substring of found title
            if (frame.getTitle().indexOf(title) != -1) {
                return frame;
            }
        }

        return null;
    }


    /**
     * Gets the number of registred frames.
     *
     * @return the number of frames
     */
    public int getFrameCount() {
        return frames.size();
    }

    /**
     * Returns whether the given frame is still registred in
     * the FrameManager
     *
     * @return if the frame is still registred
     */
    public boolean getFrameIsRegistred(Frame frame) {
        for (Iterator iterator = frames.iterator(); iterator.hasNext();) {
            Frame nextFrame = (Frame) iterator.next();
            if (nextFrame == frame)
                return true;
        }

        return false;
    }


    /**
     * Adds the specified frame listener to receive frame events from this
     * frame manager.
     *
     * @param listener the frame listener
     */
    public void addFrameListener(FrameListener listener) {
        listeners.add(listener);

        // post the actual status of the frames
        for (Iterator iterator = frames.iterator(); iterator.hasNext();) {
            Frame frame = (Frame) iterator.next();
            listener.frameOpened(new FrameEvent(frame, frames.indexOf(frame),
                    FrameEvent.FRAME_OPENED));
        }
    }

    /**
     * Removes the specified frame listener so it no longer receives frame
     * events from this frame manager.
     *
     * @param listener the frame listener
     */
    public void removeFrameListener(FrameListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     *
     * @param e the event
     */
    public synchronized void fireFrameOpened(FrameEvent e) {
        for (FrameListener listener : listeners) {
            listener.frameOpened(e);
        }
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     *
     * @param e the event
     */
    public synchronized void fireFrameClosed(FrameEvent e) {
        for (FrameListener listener : listeners) {
            listener.frameClosed(e);
        }
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     *
     * @param e the event
     */
    public synchronized void fireFrameChangedTitle(FrameEvent e) {
        for (FrameListener listener : listeners) {
            listener.frameChangedTitle(e);
        }
    }

    // --- member variables ------------------------------------

    private static List<FrameListener> listeners = new ArrayList<FrameListener>();
    private static List<Frame> frames = new ArrayList<Frame>();
}
