package slash.util;

public class Pair {
    public Object first;
    public Object second;

    public Pair(Object first, Object second) {
        this.first = first;
        this.second = second;
    }

    public boolean equals(Object anObject) {
        if (this == anObject) return true;

        if ((anObject != null) && (anObject instanceof Pair)) {
            Pair other = (Pair) anObject;
            return areEqual(first, other.first) && areEqual(second, other.second);
        }

        return false;
    }

    private boolean areEqual(Object a, Object b) {
        return a == null ? (b == null) : (a.equals(b));
    }

    public int hashCode() {
        int hf = first == null ? 0 : first.hashCode();
        int hs = second == null ? 0 : second.hashCode();

        return ((hf & 0xFFFF) << 16) | (hs & 0xFFFF);
    }

    public String toString() {
        return "Pair(" + first + "," + second + ")";
    }
}
