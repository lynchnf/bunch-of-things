package norman.bunch.of.things.temp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SetUpExampleData {
    public static void main(String[] args) {
        InputStream inputStream =
                Thread.currentThread().getContextClassLoader().getResourceAsStream("example-rule-book.json");
        String javaHome = System.getProperty("java.home");
        System.out.println("javaHome=\"" + javaHome + "\"");
        String userHome = System.getProperty("user.home");
        System.out.println("userHome=\"" + userHome + "\"");

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(inputStream);

            File resultFile = new File(userHome, "example-rule-book.json");

            objectMapper.writeValue(resultFile, jsonNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
