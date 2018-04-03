package traildb;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import traildb.constructor.TrailDBConstructor;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by krash on 03.04.18.
 */
public class IntegrationTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testOneRecord() throws Exception {

        Path path = folder.newFolder().toPath();

        final UUID uuid = UUID.randomUUID();
        final long timestamp = 100;
        List<String> fields = Collections.singletonList("field");
        try (TrailDBConstructor cons = new TrailDBConstructor(path, fields)) {
            cons.add(uuid, timestamp, Collections.singleton("one"));
            cons.add(uuid, timestamp * 2, Collections.singleton("two"));
        }

        TrailDB db = new TrailDB(path);

        // checking meta information
        TrailDBMeta meta = db.getMeta();
        assertEquals(1, meta.getNumTrails());
        assertEquals(2, meta.getNumEvents());
        assertEquals(fields.size() + 1, meta.getNumEvents()); // "time" is a field as well
        assertEquals(timestamp, meta.getMinTimestamp());
        assertEquals(timestamp * 2, meta.getMaxTimestamp());

        assertEquals(uuid, db.getUUID(0));
    }

    @Test
    public void testTwoRecords() throws Exception {
        Path path = folder.newFolder().toPath();

        final UUID uuid_one = UUID.randomUUID();
        final UUID uuid_two = UUID.randomUUID();
        final long timestamp_one = 100;
        final long timestamp_two = 1000;
        List<String> fields = Collections.singletonList("field");
        try (TrailDBConstructor cons = new TrailDBConstructor(path, fields)) {
            cons.add(uuid_one, timestamp_one, Collections.singleton("один"));
            cons.add(uuid_one, timestamp_one * 2, Collections.singleton("two"));
            cons.add(uuid_one, timestamp_one * 3, Collections.singleton("three"));

            cons.add(uuid_two, timestamp_two, Collections.singleton("два"));
            cons.add(uuid_two, timestamp_two * 2, Collections.singleton("five"));
            cons.add(uuid_two, timestamp_two * 3, Collections.singleton("six"));
        }

        TrailDB db = new TrailDB(path);
        db.createCursor();
    }
}
