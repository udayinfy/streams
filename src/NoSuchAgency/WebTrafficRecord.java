package NoSuchAgency;

import static NoSuchAgency.NoSuchAgency.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
* Container class for Web Traffic Records: a representation of a single network
* request.
*/
class WebTrafficRecord {

    private final long timestamp;
    private final String source;
    private final String destination;
    private final String payload;

    private static final int DELTA = 25;
    private static final int BOUND = 256;

    private static List<WebTrafficRecord> records =
        new ArrayList<>((int) MAX_SIZE);

    /** Create a new WebTrafficRecord with the appropriate fields. */
    public WebTrafficRecord(long timestamp, String source, String destination,
                            String payload) {
        this.timestamp = timestamp;
        this.source = source;
        this.destination = destination;
        this.payload = payload;
        records.add(this);
    }

    /** Randomly generate a new WebTrafficRecord that might be a match. */
    public static WebTrafficRecord random() {
        return random(RANDOM);
    }

    /** Generate a (fake) response record. */
    public WebTrafficRecord response() {
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
    public boolean matches(WebTrafficRecord other) {
        return ((this.getSource().equals(other.getSource())) &&
                (Math.abs(this.getTimestamp() - other.getTimestamp()) < 10));
    }

    /**
     * Returns true iff this record is equivalent to the other record. Two
     * records are equal iff every field but the timestamp is the same.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WebTrafficRecord)) {
            return false;
        }
        WebTrafficRecord other = (WebTrafficRecord) o;
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

    private static WebTrafficRecord createResponse(WebTrafficRecord request,
                                                   Random random) {
        long timestamp = request.getTimestamp() + random.nextInt(DELTA);
        String source = request.getDestination();
        String destination = request.getSource();
        String payload = new StringBuilder(request.getPayload()).reverse()
                                                                .toString();
        return new WebTrafficRecord(timestamp, source, destination, payload);
    }

    private static WebTrafficRecord random(Random random) {
        long timestamp;
        String source, destination;
        String payload = Long.toString(Math.abs(random.nextLong()), 36);

        boolean createMatch = random.nextBoolean();
        if (createMatch) {
            WebTrafficRecord match = records.get(random.nextInt(records.size()));
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
        return new WebTrafficRecord(timestamp, source, destination, payload);
    }
}
