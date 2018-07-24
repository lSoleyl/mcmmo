package lsoleyl.mcmmo.utility;

public class Tuple<A,B> {
    public A a;
    public B b;

    public Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof  Tuple) {
            Tuple other = (Tuple) obj;
            return a.equals(other.a) && b.equals(other.b);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return a.hashCode() ^ b.hashCode();
    }

    @Override
    public String toString() {
        return "(" + a + "," + b + ")";
    }
}
