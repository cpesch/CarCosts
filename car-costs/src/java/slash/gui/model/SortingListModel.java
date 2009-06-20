package slash.gui.model;

import slash.util.Pair;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.Arrays;
import java.util.Comparator;

/**
 * A <code>ListModel</code> that sorts it's entries with the
 * given <code>Comparator</code>.
 */

public class SortingListModel extends AbstractListModel {

    /**
     * Construct a new sorting list model.
     */
    public SortingListModel(ListModel delegate, Comparator comparator) {
        this.delegate = delegate;
        this.comparator = new PairComparator(comparator);

        delegate.addListDataListener(listener);
        initializeIndices();
    }

    /**
     * Detach from delegate and detach all registred listeners.
     */
    public void detach() {
        delegate.removeListDataListener(listener);

        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                removeListDataListener((ListDataListener) listeners[i + 1]);
            }
        }
    }

    // --- ListModel interface ---------------------------------

    /**
     * Add a listener to the list that's notified each time a change
     * to the data model occurs.
     *
     * @param l the ListDataListener
     */
    public void addListDataListener(ListDataListener l) {
        super.addListDataListener(l);
    }

    /**
     * Remove a listener from the list that's notified each time a
     * change to the data model occurs.
     *
     * @param l the ListDataListener
     */
    public void removeListDataListener(ListDataListener l) {
        super.removeListDataListener(l);
    }

    /**
     * Returns the value at the specified index.
     */
    public Object getElementAt(int index) {
        return delegate.getElementAt(translate(index));
    }

    /**
     * Returns the length of the list.
     */
    public int getSize() {
        if (comparator != null)
            return indices.length;
        else
            return delegate.getSize();
    }

    // --- helper methods --------------------------------------

    /**
     * increments all entries that contain a number greater
     * than key by one
     */
    private void incrementAllGreaterEqual(int[] arr, int key) {
        for (int i = 0; i < arr.length; i++)
            if (arr[i] >= key)
                arr[i]++;
    }

    /**
     * decrements all entries that contain a number greater
     * than key by one
     */
    private void decrementAllGreater(int[] arr, int key) {
        for (int i = 0; i < arr.length; i++)
            if (arr[i] > key)
                arr[i]--;
    }


    /**
     * inserts the entry data into the array at the position insert.
     */
    private int[] insertElementAt(int[] indexes, int data, int insert) {
        int[] newData = new int[indexes.length + 1];
        // first chunk
        if (insert > 0)
            System.arraycopy(indexes, 0, newData, 0, insert);
        newData[insert] = data;
        // second chunk
        if (indexes.length > insert) {
            System.arraycopy(indexes, insert,
                    newData, insert + 1, indexes.length - insert);
        }
        return newData;
    }

    /**
     * removes the entry at the position remove in the array.
     */
    private int[] removeElementAt(int[] indexes, int remove) {
        int[] newData = new int[indexes.length - 1];
        // first chunk
        if (remove > 0)
            System.arraycopy(indexes, 0, newData, 0, remove);
        // second chunk
        if (indexes.length > remove) {
            System.arraycopy(indexes, remove + 1,
                    newData, remove, indexes.length - remove - 1);
        }
        return newData;
    }

    /**
     * Initialize the mapping.
     */
    private synchronized void initializeIndices() {
        int size = delegate.getSize();

        Pair[] pairs = new Pair[size];
        for (int i = 0; i < size; i++)
            pairs[i] = new Pair(delegate.getElementAt(i), i);

        Arrays.sort(pairs, comparator);

        indices = new int[size];
        for (int i = 0; i < size; i++) {
            indices[i] = (Integer) pairs[i].second;
        }

        // Debug.printArray(indices);

        fireContentsChanged(this, 0, size);
    }

    /**
     * Insert one entry.
     */
    private synchronized void insert(int index) {
        // System.out.println("inserting: " + index);

        // increment all indices greater equal to index
        incrementAllGreaterEqual(indices, index);

        // find key
        Object key = delegate.getElementAt(index);

        Pair[] pairs = new Pair[indices.length];
        for (int i = 0, c = pairs.length; i < c; i++)
            pairs[i] = new Pair(getElementAt(i), i);

        // use binary search to locate insertion point
        int retVal = Arrays.binarySearch(pairs, new Pair(key, new Integer(index)),
                comparator);

        int insert = (retVal < 0) ? -(retVal + 1) : retVal;
        // System.out.println("bs returned " + retVal + " => insert at " + insert);

        indices = insertElementAt(indices, index, insert);

        fireIntervalAdded(this, insert, insert);
    }

    /**
     * Remove one entry.
     */
    public synchronized void remove(int index) {
        // System.out.println("removing: " + index);
        int remove = -1;

        for (int i = 0; i < indices.length; i++) {
            if (index == indices[i]) {
                remove = i;
                break;
            }
        }

        if (remove >= 0) {
            indices = removeElementAt(indices, remove);

            fireIntervalRemoved(this, remove, remove);

            // decrements all indices greater than the removed
            decrementAllGreater(indices, index);
        }
    }

    /**
     * Translate an entry via the mapping.
     */
    private synchronized int translate(int entry) {
        if (comparator != null)
            return indices[entry];
        else
            return entry;
    }

    // --- inner classes ---------------------------------------

    /**
     * Encapsulate the given comparator to suit the sorting
     * of the <code>Array</code> class.
     */
    public static class PairComparator implements Comparator<Pair> {
        /**
         * Initialize with delegate.
         */
        public PairComparator(Comparator delegate) {
            this.delegate = delegate;
        }

        /**
         * Fetches the FolderModels out of the given Pairs and
         * calls the delegate <code>Comparator</code>
         */
        public int compare(Pair o1, Pair o2) {
            return delegate.compare(o1.first, o2.first);
        }

        /**
         * Returns the result of the delegate <code>Comparator</code>.
         */
        public boolean equals(Object o) {
            return delegate.equals(o);
        }

        private Comparator delegate;
    }

    /**
     * Listens at the delegate <code>ListModel</code> and
     * starts the sort of the entries on a notification,
     * after the sort, it notifies the delegate.
     */
    public class DelegateListener implements ListDataListener {
        public void contentsChanged(ListDataEvent e) {
            int index0 = e.getIndex0();
            int index1 = e.getIndex1();
            // System.out.println("contentsChanged from "+index0+" to "+index1);

            if (index0 != index1)
                initializeIndices();
            else {
                remove(index0);
                insert(index0);
            }
        }

        public void intervalAdded(ListDataEvent e) {
            int index0 = e.getIndex0();
            int index1 = e.getIndex1();
            // System.out.println("intervalAdded from "+index0+" to "+index1);

            if (index0 != index1)
                initializeIndices();
            else
                insert(index0);
        }

        public void intervalRemoved(ListDataEvent e) {
            int index0 = e.getIndex0();
            int index1 = e.getIndex1();
            // System.out.println("intervalRemoved from "+index0+" to "+index1);

            if (index0 != index1)
                initializeIndices();
            else
                remove(index0);
        }
    }

    // --- member variables ------------------------------------

    private ListModel delegate;
    private Comparator comparator;
    private int[] indices;
    private ListDataListener listener = new DelegateListener();
}
