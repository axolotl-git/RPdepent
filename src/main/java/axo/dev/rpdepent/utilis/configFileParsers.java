package axo.dev.rpdepent.utilis;

import java.nio.file.Path;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class configFileParsers {
    public static Map<String, String> parseJSON(Path path) {
        //use the path to parse the JSON
        Object file = JSONValue.parse(path.toString());
        JSONObject jsonObjectdecode = (JSONObject)file;
        //extract the dictionary from the JSON
        Map<String, String> IDs = (Map<String, String>)jsonObjectdecode.get("depends");
        return IDs;
    }
}
