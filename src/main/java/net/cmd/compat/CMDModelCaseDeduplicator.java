package net.cmd.compat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CMDModelCaseDeduplicator {
    public static List<CMDModelCase> deduplicate(List<CMDModelCase> input) {
        Map<String, CMDModelCase> deduped = new LinkedHashMap<>();
        for (CMDModelCase c : input) deduped.putIfAbsent(c.modelId, c);
        return new ArrayList<>(deduped.values());
    }
}
