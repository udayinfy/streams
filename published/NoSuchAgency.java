import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.*;

/**
 * Copyright (C) 2016 No Such Agency
 *
 *      [ R E D A C T E D ]
 *
 */
public class NoSuchAgency {

    private static final long SEED     = Long.parseLong("CS61BL", 36);
    private static final Random RANDOM = new Random(SEED);
    private static final long MAX_SIZE = 1000;
    private static final long ROUNDS   = 1000;

    private static final int DELTA     = 25;
    private static final int BOUND     = 256;

    private static Map<String, List<WTR>> sources = new HashMap<>();
    private static List<WTR> dangerous = new ArrayList<>();

    /**
     * Perform the following steps in a single stream expression.
     *
     *  1. Populate the sources database by grouping by sources. Note: avoid
     *     collect since it will terminate the stream.
     *  2. For each remaining record in the stream, disregard it if there is no
     *     matching record that can be found in sources. Use WTR.matches.
     *  3. Remove any records that have source groups of size less than 100.
     *  4. For each remaining record, generate a fake response packet with
     *     the WTR.response method.
     *  5. Add all the results to dangerous.
     */
    public static void update(Stream<WTR> data) {
        // FIXME
    }

    /** Data is updated in several small batches of MAX_SIZE each. */
    public static void main(String[] args) {
        for (int i = 0; i < ROUNDS; i++) {
            update(Stream.generate(() -> WTR.random()).limit(MAX_SIZE));
        }
        dangerous.stream()
                 .sorted((w1,w2) -> (int) (w1.getTimestamp() - w2.getTimestamp()))
                 .forEach(System.out::println);
    }

    /**
     * Container class for Web Traffic Records.
     */
    public static class WTR {

        private final long timestamp;
        private final String source;
        private final String destination;
        private final String payload;

        private static List<WTR> records = new ArrayList<>((int) MAX_SIZE);

        /** Create a new WTR with the appropriate fields. */
        public WTR(long timestamp, String source, String destination,
                   String payload) {
            this.timestamp = timestamp;
            this.source = source;
            this.destination = destination;
            this.payload = payload;
            records.add(this);
        }

        /** Randomly generate a new WTR that might be a match. */
        public static WTR random() {
            return random(RANDOM);
        }

        /** Create a (fake) response record. */
        public WTR response() {
            return createResponse(this, RANDOM);
        }

        /** Return the timestamp. */
        public long getTimestamp() {
            return this.timestamp;
        }

        /** Return the source IP. */
        public String getSource() {
            return this.source;
        }

        /** Return the destination IP. */
        public String getDestination() {
            return this.destination;
        }

        /** Return the data payload. */
        public String getPayload() {
            return this.payload;
        }

        /**
         * Returns true iff this record matches the other. Two records match
         * if their sources are the same and their timestamps differ by less
         * than 10 seconds.
         */
        public boolean matches(WTR other) {
            return ((this.getSource().equals(other.getSource())) &&
                    (Math.abs(this.getTimestamp()
                              - other.getTimestamp()) < 10));
        }

        /**
         * Returns true iff this record is equivalent to the other record. Two
         * records are equal iff every field but the timestamp is the same.
         */
        @Override
        public boolean equals(Object o) {
            if (!(other instanceof WTR) {
                return false;
            }
            WTR other = (WTR) o;
            return ((this.getSource().equals(other.getSource())) &&
                    (this.getDestination().equals(other.getDestination())) &&
                    (this.getPayload().equals(other.getPayload())));
        }

        @Override
        public String toString() {
            return new StringBuilder().append(this.getTimestamp()).append('\t')
                                      .append(this.getSource()).append('\t')
                                      .append(this.getDestination()).append('\t')
                                      .append(this.getPayload())
                                      .toString();
        }

        private static WTR createResponse(WTR request, Random random) {
            long timestamp = request.getTimestamp() + random.nextInt(DELTA);
            String source = request.getDestination();
            String destination = request.getSource();
            String payload = new StringBuilder(request.getPayload()).reverse()
                                                                    .toString();
            return new WTR(timestamp, source, destination, payload);
        }

        private static WTR random(Random random) {
            long timestamp;
            String source, destination;
            String payload = Long.toString(Math.abs(random.nextLong()), 36);

            boolean createMatch = random.nextBoolean();
            if (createMatch) {
                WTR match = records.get(random.nextInt(records.size()));
                timestamp = match.getTimestamp() + random.nextInt(DELTA)
                                                 - random.nextInt(DELTA);
                source = match.getDestination();
                destination = match.getSource();
            } else {
                timestamp = SEED + random.nextInt(DELTA * 100);
                source = new StringBuilder().append(random.nextInt(BOUND))
                                            .append('.')
                                            .append(random.nextInt(BOUND))
                                            .append('.')
                                            .append(random.nextInt(BOUND))
                                            .append('.')
                                            .append(random.nextInt(BOUND))
                                            .toString();
                destination =
                         new StringBuilder().append(random.nextInt(BOUND))
                                            .append('.')
                                            .append(random.nextInt(BOUND))
                                            .append('.')
                                            .append(random.nextInt(BOUND))
                                            .append('.')
                                            .append(random.nextInt(BOUND))
                                            .toString();
            }
            return new WTR(timestamp, source, destination, payload);
        }
    }
}

