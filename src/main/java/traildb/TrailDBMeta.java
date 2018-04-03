package traildb;

/**
 * Created by krash on 03.04.18.
 */
public class TrailDBMeta {

    private final long numTrails;
    private final long numEvents;
    private final long numFields;
    private final long minTimestamp;
    private final long maxTimestamp;

    public TrailDBMeta(long numTrails, long numEvents, long numFields, long minTimestamp, long maxTimestamp) {
        this.numTrails = numTrails;
        this.numEvents = numEvents;
        this.numFields = numFields;
        this.minTimestamp = minTimestamp;
        this.maxTimestamp = maxTimestamp;
    }

    public long getNumTrails() {
        return numTrails;
    }

    public long getNumEvents() {
        return numEvents;
    }

    public long getNumFields() {
        return numFields;
    }

    public long getMinTimestamp() {
        return minTimestamp;
    }

    public long getMaxTimestamp() {
        return maxTimestamp;
    }
}
