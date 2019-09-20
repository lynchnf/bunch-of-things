package norman.bunch.of.things;

import org.junit.After;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;

public class ApplicationTest {
    @After
    public void tearDown() throws Exception {
        String dirPath =
                System.getProperty("user.home") + System.getProperty("file.separator") + ".bunch-of-things-test";
        String filePath = dirPath + System.getProperty("file.separator") + "bunch-of-things-test.properties";
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        File dir = new File(dirPath);
        if (dir.exists()) {
            dir.delete();
        }
    }

    @Test
    public void storeProps() throws Exception {
        // Create a properties object which we're gonna store.
        Properties inProps = new Properties();
        inProps.setProperty("test.one", "111");
        inProps.setProperty("test.two", "222");
        inProps.setProperty("test.three", "333");

        // Store it.
        Application.storeProps(inProps);

        // The properties should have been written to a file.
        String path = System.getProperty("user.home") + System.getProperty("file.separator") + ".bunch-of-things-test" +
                System.getProperty("file.separator") + "bunch-of-things-test.properties";

        Map<String, String> actual = new HashMap<>();

        // Read the file and verify it contains what we expect.
        // Line 1 is our comments.
        // Line 2 is a timestamp.
        // Lines 3 and greater are our properties in no particular order.
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(path)))) {
            String line = bufferedReader.readLine();
            int lineCount = 0;
            while (line != null) {
                lineCount++;
                if (lineCount == 1) {
                    assertEquals("#Bunch Of Things Test", line);
                } else if (lineCount >= 3) {
                    String[] split = line.split("\\=", 2);
                    actual.put(split[0], split[1]);
                }

                line = bufferedReader.readLine();
            }
        }

        assertEquals(3, actual.size());
        assertTrue(actual.containsKey("test.one"));
        assertEquals("111", actual.get("test.one"));
        assertTrue(actual.containsKey("test.two"));
        assertEquals("222", actual.get("test.two"));
        assertTrue(actual.containsKey("test.three"));
        assertEquals("333", actual.get("test.three"));
    }

    @Test
    public void getAppDir() throws Exception {
        File appDir = Application.getAppDir();
        String expected =
                System.getProperty("user.home") + System.getProperty("file.separator") + ".bunch-of-things-test";
        assertEquals(expected, appDir.getCanonicalPath());
    }
}
