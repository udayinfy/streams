import java.util.stream.IntStream;
import java.util.Iterator;

/**
 * Iterator over an array of votes, returning the index of each vote however
 * many times indicated by the value at that index. Based on Summer 2014.
 */
public class VoteIterator implements Iterator<Integer> {

    private Iterator<Integer> _iterator;

    /** Create a new VoteIterator on VOTES. */
    public VoteIterator(int[] votes) {
        _iterator = IntStream.range(0, votes.length)
                             .flatMap(i -> IntStream.generate(() -> i)
                                                    .limit(votes[i]))
                             .iterator();
    }

    @Override
    public boolean hasNext() {
        return _iterator.hasNext();
    }

    @Override
    public Integer next() {
        return _iterator.next();
    }

    @Override
    public void remove() {
        _iterator.remove();
    }

    public static void main(String[] args) {
        int[] votes = {0, 2, 1, 0, 1, 0};
        VoteIterator iter = new VoteIterator(votes);
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }
    }
}

