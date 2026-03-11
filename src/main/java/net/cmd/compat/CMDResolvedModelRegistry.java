package net.cmd.compat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Canonical resolved case registry.
 */
public class CMDResolvedModelRegistry {

    private static final Map<String, List<CMDResolvedModelCase>> ITEM_CASES = new HashMap<>();

    public static void clear() { ITEM_CASES.clear(); }
    public static void addCase(String baseItemId, CMDResolvedModelCase modelCase) { ITEM_CASES.computeIfAbsent(baseItemId, k -> new ArrayList<>()).add(modelCase); }

    public static void loadFromMap(Map<String, List<CMDResolvedModelCase>> map) {
        clear();
        for (Map.Entry<String, List<CMDResolvedModelCase>> entry : map.entrySet()) {
            ITEM_CASES.put(entry.getKey(), normalizeUniqueDisplayIds(entry.getValue()));
        }
    }

    private static List<CMDResolvedModelCase> normalizeUniqueDisplayIds(List<CMDResolvedModelCase> input) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        List<CMDResolvedModelCase> out = new ArrayList<>();
        for (CMDResolvedModelCase source : input) {
            CMDResolvedModelCase copy = copyOf(source);
            String baseValue = copy.modelValue != null && !copy.modelValue.isBlank() ? copy.modelValue : copy.displayId;
            if (baseValue == null || baseValue.isBlank()) {
                out.add(copy);
                continue;
            }
            int next = counts.getOrDefault(baseValue.toLowerCase(), 0) + 1;
            counts.put(baseValue.toLowerCase(), next);
            if (next > 1) {
                String uniqueValue = baseValue + next;
                copy.modelValue = uniqueValue;
                copy.displayId = (copy.modelNamespace != null ? copy.modelNamespace + ":" : "") + uniqueValue;
                if (copy.suggestedAnvilText != null && !copy.suggestedAnvilText.contains(":")) copy.suggestedAnvilText = uniqueValue;
            }
            out.add(copy);
        }
        return out;
    }

    private static CMDResolvedModelCase copyOf(CMDResolvedModelCase source) {
        CMDResolvedModelCase out = new CMDResolvedModelCase();
        out.baseItemId = source.baseItemId;
        out.displayId = source.displayId;
        out.modelValue = source.modelValue;
        out.modelNamespace = source.modelNamespace;
        out.modelPath = source.modelPath;
        out.assetNamespace = source.assetNamespace;
        out.equippable = source.equippable;
        out.selectorType = source.selectorType;
        out.propertyType = source.propertyType;
        out.selectorValue = source.selectorValue;
        out.sourceDisplayName = source.sourceDisplayName;
        out.sourcePackId = source.sourcePackId;
        out.itemDefinitionPath = source.itemDefinitionPath;
        out.modelFilePath = source.modelFilePath;
        out.suggestedAnvilText = source.suggestedAnvilText;
        return out;
    }

    public static List<CMDResolvedModelCase> getCases(String baseItemId) { return ITEM_CASES.getOrDefault(baseItemId, List.of()); }
    public static Map<String, List<CMDResolvedModelCase>> getAll() { return Collections.unmodifiableMap(ITEM_CASES); }

    public static CMDResolvedModelCase findByDisplayId(String baseItemId, String displayId) {
        for (CMDResolvedModelCase c : getCases(baseItemId)) if (c.displayId != null && c.displayId.equalsIgnoreCase(displayId)) return c;
        return null;
    }

    public static CMDResolvedModelCase findByAnvilInput(String baseItemId, String input) {
        if (input == null) return null;
        for (CMDResolvedModelCase c : getCases(baseItemId)) {
            if (c.suggestedAnvilText != null && c.suggestedAnvilText.equalsIgnoreCase(input)) return c;
            if (c.displayId != null && c.displayId.equalsIgnoreCase(input)) return c;
        }
        return null;
    }
}
