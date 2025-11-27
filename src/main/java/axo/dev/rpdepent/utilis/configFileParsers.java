package axo.dev.rpdepent.utilis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class configFileParsers {

    static String MOD_ID = "axo.dev.rpd";
    static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /**
     * Reads a JSON file and returns the map "depends" as Map<String, String>.
     * If an error occurs, returns an empty map.
     */
    public static Map<String, String> parseJSON(Path path) throws IOException {
        if (path == null) {
            return Collections.emptyMap();
        }

        // 1. Read the file
        String jsonContent = Files.readString(path, StandardCharsets.UTF_8);

        // 2. Execute JSON parsing
        Object parsedObject;
        try {
            parsedObject = JSONValue.parse(jsonContent);
        } catch (Exception e) {
            LOGGER.error("JSON parsing error: " + path + " - " + e.getMessage());
            return Collections.emptyMap();
        }

        // 3.Verify that the main JSON is a JSONObject
        if (!(parsedObject instanceof JSONObject jsonObjectDecode)) {
            LOGGER.error("the main JSON is a JSONObject: " + path);
            return Collections.emptyMap();
        }

        // 4. Extract "depends"
        Object dependsObject = jsonObjectDecode.get("depends");

        // 5. Verify that "depends" is a JSONObject
        if (!(dependsObject instanceof JSONObject dependsJson)) {
            LOGGER.error("'depends' key is not a JSON object: " + path);
            return Collections.emptyMap();
        }

        // 6. Convert the JSONObject in Map<String, String>
        Map<String, String> result = new HashMap<>();

        for (Map.Entry<String, Object> entry : dependsJson.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String stringValue) {
                result.put(key, stringValue);
            } else {
                LOGGER.error("non string value in section 'depends': key = " + key);
            }
        }

        return result;
    }
}