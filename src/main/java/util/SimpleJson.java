package util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tiny, dependency-free helpers to pull common fields from the SerpApi JSON.
 * NOTE: This is intentionally simple and not a full JSON parser.
 * It works for the expected Google Scholar Author payload shape.
 */
public class SimpleJson {

    // Extracts a top-level string field: {"error":"..."} --> "..."
    public static String extractTopLevelString(String json, String field) {
        String regex = "\""+Pattern.quote(field)+"\"\\s*:\\s*\"([^\"]*)\"";
        Matcher m = Pattern.compile(regex).matcher(json);
        return m.find() ? m.group(1) : null;
    }

    // Extracts a string field from within a named object: {"author":{"name":"...","affiliations":"..."}}
    public static String extractNestedString(String json, String object, String field) {
        String objRegex = "\""+Pattern.quote(object)+"\"\\s*:\\s*\\{(.*?)\\}";
        Matcher mObj = Pattern.compile(objRegex, Pattern.DOTALL).matcher(json);
        if (mObj.find()) {
            String block = mObj.group(1);
            String fieldRegex = "\""+Pattern.quote(field)+"\"\\s*:\\s*\"([^\"]*)\"";
            Matcher mField = Pattern.compile(fieldRegex).matcher(block);
            if (mField.find()) return mField.group(1);
        }
        return null;
    }

    // Extracts an integer field from a nested object: e.g., "cited_by":{"value":12}
    public static Integer extractNestedInt(String json, String object, String field) {
        String objRegex = "\""+Pattern.quote(object)+"\"\\s*:\\s*\\{(.*?)\\}";
        Matcher mObj = Pattern.compile(objRegex, Pattern.DOTALL).matcher(json);
        if (mObj.find()) {
            String block = mObj.group(1);
            String fieldRegex = "\""+Pattern.quote(field)+"\"\\s*:\\s*(\\d+)";
            Matcher mField = Pattern.compile(fieldRegex).matcher(block);
            if (mField.find()) return Integer.parseInt(mField.group(1));
        }
        return null;
    }

    // Extracts each JSON object from an array:  "articles":[ {...}, {...}, ... ]
    public static List<String> extractArrayObjects(String json, String arrayName) {
        List<String> blocks = new ArrayList<>();
        String arrRegex = "\""+Pattern.quote(arrayName)+"\"\\s*:\\s*\\[(.*?)]";
        Matcher mArr = Pattern.compile(arrRegex, Pattern.DOTALL).matcher(json);
        if (!mArr.find()) return blocks;

        String arrBody = mArr.group(1);
        // split top-level objects inside the array in a simple way: find { ... } non-greedily
        Matcher mObj = Pattern.compile("\\{(?>[^{}]|\\{[^{}]*\\})*\\}").matcher(arrBody);
        while (mObj.find()) {
            blocks.add(mObj.group());
        }
        return blocks;
    }

    // Extracts a string field from a single object block
    public static String extractFieldString(String objectBlock, String field) {
        String fieldRegex = "\""+Pattern.quote(field)+"\"\\s*:\\s*\"([^\"]*)\"";
        Matcher m = Pattern.compile(fieldRegex).matcher(objectBlock);
        return m.find() ? m.group(1) : null;
    }
}
