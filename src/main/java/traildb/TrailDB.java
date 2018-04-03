package traildb;

import traildb.constructor.TrailDBConstructor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by krash on 02.04.18.
 */
public class TrailDB {

    public static int FLAG = 1;

    static {
        System.loadLibrary("traildbjava");
    }

    private long ptr;

    public TrailDB(Path path) {
        ptr = openDb(path.toAbsolutePath().toString());
    }

    public static void main(String... argv) throws Exception {
        TrailDB db = new TrailDB(Paths.get("/home/krash/tdb/1.tdb"));
        db.createCursor().peek();
    }

    public TrailDBCursor createCursor() throws TrailDBException {
        return new TrailDBCursor(ptr);
    }

    public UUID getUUID(long trail_id) throws TrailDBException {
        return UUIDUtil.asUuid(getUUID(ptr, trail_id));
    }

    /**
     * Opens a database and returns pointer
     */
    private native long openDb(String string);

    private native byte[] getUUID(long pointer, long trail_id) throws TrailDBException;

}
