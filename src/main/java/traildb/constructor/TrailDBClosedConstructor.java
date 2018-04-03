package traildb.constructor;

import traildb.TrailDBConstructorInterface;
import traildb.TrailDBException;

import java.util.Collection;
import java.util.UUID;

/**
 * Created by krash on 03.04.18.
 */
class TrailDBClosedConstructor implements TrailDBConstructorInterface {
    @Override
    public void add(UUID id, long timestamp, Collection<String> fields) throws TrailDBException {
        throw new TrailDBException("Attempt to add to closed database");
    }

    @Override
    public void close() throws Exception {
        // do nothing
    }
}
