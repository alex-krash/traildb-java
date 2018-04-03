package traildb;

import java.util.UUID;

/**
 * Created by krash on 02.04.18.
 */
public class TrailDBCursor {

    static {
        System.loadLibrary("traildbjava");
    }

    private long pointer;

    public TrailDBCursor(long tdb) throws TrailDBException
    {
        pointer = constructor(tdb);
    }

    public void peek()
    {
        peek(pointer);
    }

    public UUID getUUID(long trail_id) throws TrailDBException
    {
        byte[] bytes = getUUID(pointer, trail_id);
        return null;
    }

    private native void peek(long cursor);

    /**
     * Initialize a cursor resource
     */
    private native long constructor(long tdb) throws TrailDBException;

    /**
     * Get a binary representation of particular trail_id
     */
    private native byte[] getUUID(long pointer, long trail_id) throws TrailDBException;

}
