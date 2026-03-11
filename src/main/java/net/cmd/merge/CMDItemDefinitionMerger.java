package net.cmd.merge;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.cmd.compat.CMDModelCase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CMDItemDefinitionMerger {
    public static JsonObject merge(String baseItem, List<CMDModelCase> rawCases) {
        List<CMDModelCase> cases = normalizeUniqueCases(rawCases);
        cases.sort(Comparator.comparing(c -> c.modelId == null ? "" : c.modelId));

        JsonArray jsonCases = new JsonArray();
        for (CMDModelCase c : cases) {
            JsonObject caseObject = new JsonObject();
            caseObject.addProperty("when", c.modelId);

            JsonObject modelObject = new JsonObject();
            modelObject.addProperty("type", "minecraft:model");
            modelObject.addProperty("model", c.modelPath);

            caseObject.add("model", modelObject);
            jsonCases.add(caseObject);
        }

        JsonObject fallback = new JsonObject();
        fallback.addProperty("type", "minecraft:model");
        fallback.addProperty("model", "minecraft:item/" + baseItem);

        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:select");
        model.addProperty("property", "minecraft:custom_model_data");
        model.add("fallback", fallback);
        model.add("cases", jsonCases);

        JsonObject root = new JsonObject();
        root.add("model", model);
        return root;
    }

    public static List<CMDModelCase> normalizeUniqueCases(List<CMDModelCase> rawCases) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        List<CMDModelCase> out = new ArrayList<>();
        for (CMDModelCase input : rawCases) {
            if (input == null || input.modelId == null || input.modelId.isBlank()) continue;
            String base = input.modelId.trim();
            int next = counts.getOrDefault(base.toLowerCase(), 0) + 1;
            counts.put(base.toLowerCase(), next);

            CMDModelCase copy = copyOf(input);
            if (next > 1) {
                copy.modelId = base + next;
                if (copy.suggestedAnvilText != null && !copy.suggestedAnvilText.contains(":")) {
                    copy.suggestedAnvilText = copy.modelId;
                }
            } else {
                copy.modelId = base;
            }
            out.add(copy);
        }
        return out;
    }

    private static CMDModelCase copyOf(CMDModelCase source) {
        CMDModelCase out = new CMDModelCase();
        out.baseItem = source.baseItem;
        out.modelId = source.modelId;
        out.modelPath = source.modelPath;
        out.sourceDisplayName = source.sourceDisplayName;
        out.sourceThreshold = source.sourceThreshold;
        out.sourceType = source.sourceType;
        out.modelValue = source.modelValue;
        out.modelNamespace = source.modelNamespace;
        out.assetNamespace = source.assetNamespace;
        out.equippable = source.equippable;
        out.itemDefinitionPath = source.itemDefinitionPath;
        out.modelFilePath = source.modelFilePath;
        out.suggestedAnvilText = source.suggestedAnvilText;
        return out;
    }
}
