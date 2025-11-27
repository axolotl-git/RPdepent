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

public class configFileParsers {

    /**
     * Legge un file JSON e restituisce la mappa "depends" come Map<String, String>.
     * In caso di errore, torna una mappa vuota.
     */
    public static Map<String, String> parseJSON(Path path) throws IOException {
        if (path == null) {
            return Collections.emptyMap();
        }

        // 1. Leggi il file
        String jsonContent = Files.readString(path, StandardCharsets.UTF_8);

        // 2. Esegui il parsing del JSON
        Object parsedObject;
        try {
            parsedObject = JSONValue.parse(jsonContent);
        } catch (Exception e) {
            System.err.println("Errore di parsing JSON nel file: " + path + " - " + e.getMessage());
            return Collections.emptyMap();
        }

        // 3. Verifica che il JSON principale sia un JSONObject
        if (!(parsedObject instanceof JSONObject jsonObjectDecode)) {
            System.err.println("Il JSON principale non è un JSONObject: " + path);
            return Collections.emptyMap();
        }

        // 4. Estrai "depends"
        Object dependsObject = jsonObjectDecode.get("depends");

        // 5. Verifica che "depends" sia un JSONObject
        if (!(dependsObject instanceof JSONObject dependsJson)) {
            System.err.println("La chiave 'depends' non è un oggetto JSON: " + path);
            return Collections.emptyMap();
        }

        // 6. Converti il JSONObject in Map<String, String>
        Map<String, String> result = new HashMap<>();

        for (Map.Entry<String, Object> entry : dependsJson.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String stringValue) {
                result.put(key, stringValue);
            } else {
                System.err.println("Valore non stringa nella sezione 'depends': chiave = " + key);
            }
        }

        return result;
    }
}