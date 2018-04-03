package traildb.constructor;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import traildb.TrailDB;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by krash on 03.04.18.
 */
public class TrailDBConstructorTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testWrite() throws Exception {
        File file = folder.newFolder();

        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("field_one");
        fieldNames.add("field_two");
        fieldNames.add("field_three");

        try (TrailDBConstructor constructor = new TrailDBConstructor(file.toPath(), fieldNames)) {
            for (int i = 0; i < 100; i++) {
                List<String> values = new ArrayList<>();
                for (int j = 0; j < 3; j++) {
                    values.add(String.valueOf(i * j));
                }
                constructor.add(UUID.randomUUID(), i, values);
            }
        }

    }

}
