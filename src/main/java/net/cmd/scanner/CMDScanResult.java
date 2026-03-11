package net.cmd.scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.cmd.compat.CMDModelCase;
import net.cmd.compat.CMDResolvedModelCase;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Scanner output container.
 */
public class CMDScanResult {

    public Map<String, List<CMDModelCase>> legacyRegistry = new LinkedHashMap<>();
    public Map<String, List<CMDResolvedModelCase>> resolvedRegistry = new LinkedHashMap<>();
    public List<String> sourceIds = new ArrayList<>();
    public Map<String, String> sourceDisplayNames = new LinkedHashMap<>();
    public int archivesFound = 0;
    public int totalModelJsonCount = 0;
    public int itemModelJsonCount = 0;
    public int equipmentModelJsonCount = 0;
    public int humanoidModelJsonCount = 0;

    public static String extractSourceDisplayName(JsonObject packMcmetaRoot) {
        if (packMcmetaRoot == null || !packMcmetaRoot.has("pack")) {
            return null;
        }

        JsonObject pack = packMcmetaRoot.getAsJsonObject("pack");
        if (!pack.has("description")) {
            return null;
        }

        JsonElement description = pack.get("description");

        if (description.isJsonPrimitive()) {
            try {
                String text = description.getAsString();
                return text == null || text.isBlank() ? null : text.trim();
            } catch (Exception ignored) {
                return null;
            }
        }

        if (description.isJsonObject()) {
            JsonObject object = description.getAsJsonObject();

            if (object.has("text")) {
                try {
                    String text = object.get("text").getAsString();
                    if (text != null && !text.isBlank()) {
                        return text.trim();
                    }
                } catch (Exception ignored) {
                }
            }

            if (object.has("extra")) {
                JsonElement extra = object.get("extra");
                if (extra.isJsonArray()) {
                    String nested = firstTextInArray(extra.getAsJsonArray());
                    if (nested != null && !nested.isBlank()) {
                        return nested.trim();
                    }
                }
            }
        }

        return null;
    }

    private static String firstTextInArray(JsonArray array) {
        for (JsonElement element : array) {
            if (element == null || element.isJsonNull()) continue;

            if (element.isJsonPrimitive()) {
                try {
                    String value = element.getAsString();
                    if (value != null && !value.isBlank()) {
                        return value;
                    }
                } catch (Exception ignored) {
                }
            }

            if (element.isJsonObject()) {
                JsonObject object = element.getAsJsonObject();
                if (object.has("text")) {
                    try {
                        String value = object.get("text").getAsString();
                        if (value != null && !value.isBlank()) {
                            return value;
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        return null;
    }
}
