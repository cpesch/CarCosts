package slash.gui.toolkit;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A FrameManager maintains a list of closed frames and applies the
 * data to newly opened frames if the titles match. Special support
 * is for the <code>RegistredJFrame</code>, where the internal titles
 * are used.
 */

public class HistoryFrameManager extends FrameManager {

    /**
     * Register a frame in the frame manager.
     *
     * @param frame the frame to register
     */
    public synchronized void registerFrame(Frame frame) {
        setFrameData(frame);

        super.registerFrame(frame);
    }

    /**
     * Unregister a previously registred frame in the frame manager.
     *
     * @param frame the frame to unregister
     */
    public synchronized void unregisterFrame(Frame frame) {
        addFrameData(frame);

        super.unregisterFrame(frame);
    }

    // --- helper methods --------------------------------------


    /**
     * Return the title of the Frame;
     *
     * @param frame frame to get the title from
     * @return return the title of the frame
     */
    private String getTitle(Frame frame) {
        String title = frame.getTitle();

        // special treatment for RegistredJFrames
        if (frame instanceof RegistredJFrame)
            title = ((RegistredJFrame) frame).getProgramName();

        return title;
    }

    /**
     * Return the frame data for the given title.
     *
     * @param title the title of the frame
     * @return return the frame data
     */
    private FrameData findFrame(String title) {
        for (FrameData data : history) {
            if (data.title.equals(title))
                return data;
        }
        return null;
    }

    /**
     * Set the frame data from the history. If there is no
     * such frame in the history, set default values.
     *
     * @param frame the Frame to add
     */
    private void setFrameData(Frame frame) {
        String title = getTitle(frame);
        FrameData data = findFrame(title);

        if (data != null) {
            // found in history
            frame.setLocation(data.location);
            frame.setSize(data.dimension);
        } else {
            // set default
            int height = frame.getSize().height;
            int width = frame.getSize().height;
            if ((height < 100) || (height > 1000) ||
                    (width < 100) || (width > 1000))
                frame.setSize(defaultDimension);

            frame.setLocation(defaultLocation);
            defaultLocation.x = defaultLocation.x + 32;
            defaultLocation.y = defaultLocation.y + 28;
            if ((defaultLocation.x > 400) || (defaultLocation.y > 300)) {
                defaultLocation.x = 0;
                defaultLocation.y = 0;
            }
        }
    }

    /**
     * Add the frame to the history. Avoid duplicated entries.
     *
     * @param frame the Frame to add
     */
    private void addFrameData(Frame frame) {
        String title = getTitle(frame);
        FrameData data = findFrame(title);

        if (data != null) {
            // update old entry
            data.location = frame.getLocation();
            data.dimension = frame.getSize();
        } else {
            // create new entry
            history.add(new FrameData(title, frame.getLocation(), frame.getSize()));
        }
    }

    // --- Inner classes ---------------------------------------

    public class FrameData {
        public FrameData(String title, Point location, Dimension dimension) {
            this.title = title;
            this.location = location;
            this.dimension = dimension;
        }

        public String title;
        public Point location;
        public Dimension dimension;
    }

    // --- member variables ------------------------------------

    private List<FrameData> history = new ArrayList<FrameData>();
    private Dimension defaultDimension = new Dimension(500, 400);
    private Point defaultLocation = new Point(7, 20);
}
