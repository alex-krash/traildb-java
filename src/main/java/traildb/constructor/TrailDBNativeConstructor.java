package traildb.constructor;

import traildb.TrailDBConstructorInterface;
import traildb.TrailDBException;
import traildb.UUIDUtil;

import java.util.Collection;
import java.util.UUID;

/**
 * A class, proxying requests to native TrailDB API
 * Created by krash on 03.04.18.
 */
class TrailDBNativeConstructor implements TrailDBConstructorInterface {

    private long constructorPointer;

    TrailDBNativeConstructor(String path, Collection<String> fieldNames) throws TrailDBException {
        this.constructorPointer = open(path, fieldNames.stream().toArray(String[]::new));
    }

    @Override
    public void add(UUID id, long timestamp, Collection<String> fields) throws TrailDBException {
        String[] arr = fields.stream().toArray(String[]::new);
        add(constructorPointer, UUIDUtil.asBytes(id), timestamp, arr);
    }

    @Override
    public void close() throws Exception {
        close(constructorPointer);
        constructorPointer = 0;
    }

    private native long open(String path, String[] fields) throws TrailDBException;

    private native long add(long pointer, byte[] id, long timestamp, String[] values) throws TrailDBException;

    private native long close(long pointer) throws TrailDBException;
}
