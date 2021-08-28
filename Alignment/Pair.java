import java.util.Objects;

public class Pair {

    String seq1;
    String seq2;

    public Pair(String seq1, String seq2) {
        this.seq1 = seq1;
        this.seq2 = seq2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return (seq1.equals(pair.seq1) && seq2.equals(pair.seq2)) || (seq2.equals(pair.seq1) && seq1.equals(pair.seq2));
    }

    @Override
    public int hashCode() {
        return Objects.hash(seq1, seq2);
    }

    @Override
    public String toString() {
        return seq1 + " " + seq2;
    }
}
