package net.cmd.compat;

import net.cmd.merge.CMDItemDefinitionMerger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Legacy normalized registry used by the merge/build pipeline.
 */
public class CMDItemModelRegistry {

    private static final Map<String, List<CMDModelCase>> ITEM_CASES = new HashMap<>();

    public static void clear() { ITEM_CASES.clear(); }
    public static void addCase(String baseItem, CMDModelCase modelCase) { ITEM_CASES.computeIfAbsent(baseItem, k -> new ArrayList<>()).add(modelCase); }

    public static void loadFromMap(Map<String, List<CMDModelCase>> map) {
        clear();
        for (Map.Entry<String, List<CMDModelCase>> entry : map.entrySet()) {
            ITEM_CASES.put(entry.getKey(), CMDItemDefinitionMerger.normalizeUniqueCases(entry.getValue()));
        }
    }

    public static List<CMDModelCase> getCases(String baseItem) { return ITEM_CASES.getOrDefault(baseItem, List.of()); }
    public static List<CMDModelCase> get(String baseItem) { return getCases(baseItem); }
    public static Map<String, List<CMDModelCase>> getAll() { return Collections.unmodifiableMap(ITEM_CASES); }
}
