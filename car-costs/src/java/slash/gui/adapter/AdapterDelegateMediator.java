package slash.gui.adapter;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A mediator, which mediates between an adapter and a delegate. This
 * is useful for inverse use of the adapters.
 *
 * @see BidirectionalAdapter
 */

public class AdapterDelegateMediator implements ChangeListener {

    /**
     * Constructs a adapter to delegate mediator.
     */
    public AdapterDelegateMediator(BidirectionalAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * Process the notification.
     */
    public void stateChanged(ChangeEvent e) {
        adapter.updateAdapterFromDelegate();
    }

    // --- member variables ------------------------------------

    private BidirectionalAdapter adapter;
}
