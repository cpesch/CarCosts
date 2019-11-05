package slash.gui.toolkit;

import java.util.EventListener;

/**
 * The listener interface for receiving frame events.
 */

public interface FrameListener extends EventListener {
    /**
     * Invoked when a frame has been opened.
     */
    public void frameOpened(FrameEvent e);

    /**
     * Invoked when a frame has been closed.
     */
    public void frameClosed(FrameEvent e);

    /**
     * Invoked when a window changed it's title.
     */
    public void frameChangedTitle(FrameEvent e);
}
