package net.cmd.resourcepack;

import com.google.gson.JsonObject;
import net.cmd.compat.CMDModelCase;
import net.cmd.compat.CMDTranslationKeyFactory;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CMDLangGenerator {
    public JsonObject generate(Map<String, List<CMDModelCase>> itemCases) {
        Map<String, String> entries = new LinkedHashMap<>();
        for (Map.Entry<String, List<CMDModelCase>> itemEntry : itemCases.entrySet()) {
            for (CMDModelCase modelCase : itemEntry.getValue()) {
                String key = CMDTranslationKeyFactory.fromModelPath(modelCase.modelPath);
                String value = resolveDisplayName(modelCase);
                entries.putIfAbsent(key, value);
            }
        }
        JsonObject json = new JsonObject();
        for (Map.Entry<String, String> entry : entries.entrySet()) json.addProperty(entry.getKey(), entry.getValue());
        return json;
    }

    private String resolveDisplayName(CMDModelCase modelCase) {
        if (modelCase.sourceDisplayName != null && !modelCase.sourceDisplayName.isBlank()) return modelCase.sourceDisplayName;
        String id = modelCase.modelId;
        int colon = id.indexOf(':');
        if (colon >= 0 && colon + 1 < id.length()) id = id.substring(colon + 1);
        return beautifyIdentifier(id);
    }

    private String beautifyIdentifier(String identifier) {
        String normalized = identifier.replace('-', '_');
        String[] parts = normalized.split("_");
        StringBuilder out = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) continue;
            if (!out.isEmpty()) out.append(" ");
            out.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) out.append(part.substring(1));
        }
        return out.toString();
    }
}
