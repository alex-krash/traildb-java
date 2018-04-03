package traildb.constructor;

import traildb.TrailDBConstructorInterface;
import traildb.TrailDBException;

import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;

/**
 * API for interacting TrailDB API
 * after #close() is called, object is still available for operations
 * to avoid if() calls, implementation is substituted with stub, that throws exceptions
 * Created by krash on 03.04.18.
 */
public class TrailDBConstructor implements TrailDBConstructorInterface {

    static {
        System.loadLibrary("traildbjava");
    }

    private final int fieldCount;
    private TrailDBConstructorInterface delegate;

    public TrailDBConstructor(Path path, Collection<String> fieldNames) throws TrailDBException {
        if (null == path) {
            throw new IllegalArgumentException("Null path given");
        } else if (null == fieldNames) {
            throw new IllegalArgumentException("Null fieldNames given");
        }
        fieldCount = fieldNames.size();

        delegate = new TrailDBNativeConstructor(path.toAbsolutePath().toString(), fieldNames);
    }

    @Override
    public void add(UUID id, long timestamp, Collection<String> fields) throws TrailDBException {
        if (null == id) {
            throw new IllegalArgumentException("Null id given");
        } else if (null == fields) {
            throw new IllegalArgumentException("Null fields given");
        } else if (fields.size() != fieldCount) {
            throw new IllegalArgumentException("Wrong field count: " + fields.size() + " != " + fieldCount);
        }
        delegate.add(id, timestamp, fields);
    }

    @Override
    public void close() throws Exception {
        delegate.close();
        delegate = new TrailDBClosedConstructor();
    }
}
