package traildb;

import java.util.Collection;
import java.util.UUID;

/**
 * An interface for adding the data to TrailDB
 * Created by krash on 03.04.18.
 */
public interface TrailDBConstructorInterface extends AutoCloseable {

    public void add(UUID id, long timestamp, Collection<String> fields) throws TrailDBException;

}
