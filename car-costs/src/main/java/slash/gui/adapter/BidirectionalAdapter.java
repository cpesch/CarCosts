package slash.gui.adapter;

/**
 * An interface for bidirectional adapters.
 *
 * @see slash.gui.adapter.AdapterDelegateMediator
 */

public interface BidirectionalAdapter {

    /**
     * Update the delegate after changes of the adapter.
     * This is the normal use, when the adapter is for
     * editing the delegate's content.
     */
    public void updateDelegateFromAdapter();

    /**
     * Update the adapter after changes of the delegate.
     * This is for inverse use, when the adapter displays
     * the delegate's content.
     */
    public void updateAdapterFromDelegate();
}
