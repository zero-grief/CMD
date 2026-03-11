package net.cmd.command;

import net.cmd.compat.CMDResolvedModelCase;
import net.cmd.compat.CMDResolvedModelRegistry;
import net.cmd.config.CMDConfigManager;
import net.cmd.model.CMDItemClassifier;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Builds a tree-like chat list for /cmd list.
 *
 * Built from the resolved registry, so the browser reflects actual pack-driven
 * resolved cases rather than loose string guesses.
 */
public class CMDListFormatter {

    public static List<Text> buildTreePage(int page) {
        return buildTreePage(page, null);
    }

    public static List<Text> buildTreePage(int page, String filter) {
        return paginate(buildTreePageUnpaginated(filter), page, normalizeFilter(filter));
    }

    public static List<Text> buildFilteredTreePage(int page, String filter) {
        return paginate(buildTreePageUnpaginated(filter), page, normalizeFilter(filter));
    }

    private static List<Text> buildTreePageUnpaginated(String rawFilter) {
        String filter = normalizeFilter(rawFilter);

        List<Text> lines = new ArrayList<>();
        lines.add(Text.literal("Custom Model Database"));

        if (filter != null) {
            lines.add(Text.literal("Filter: " + filter));
        }

        lines.add(Text.literal(""));

        Map<String, CategoryNode> grouped = buildFilteredTree(filter);

        if (grouped.isEmpty()) {
            lines.add(Text.literal("No matching models found."));
            return lines;
        }

        List<Map.Entry<String, CategoryNode>> categoryEntries = new ArrayList<>(grouped.entrySet());

        for (int categoryIndex = 0; categoryIndex < categoryEntries.size(); categoryIndex++) {
            Map.Entry<String, CategoryNode> categoryEntry = categoryEntries.get(categoryIndex);

            lines.add(CMDHoverTextFactory.itemHover(
                    categoryEntry.getKey(),
                    CMDChatPreviewFactory.createCategoryPreview(categoryEntry.getKey())
            ));

            List<Map.Entry<String, SubGroupNode>> subEntries =
                    new ArrayList<>(categoryEntry.getValue().subgroups.entrySet());

            for (int subIndex = 0; subIndex < subEntries.size(); subIndex++) {
                Map.Entry<String, SubGroupNode> subEntry = subEntries.get(subIndex);
                boolean isLastSubGroup = subIndex == subEntries.size() - 1;

                String subPrefix = isLastSubGroup ? "└─ " : "├─ ";
                String materialIndent = isLastSubGroup ? "   " : "│  ";

                lines.add(Text.literal(subPrefix).append(
                        CMDHoverTextFactory.itemHover(
                                subEntry.getKey(),
                                CMDChatPreviewFactory.createSubgroupPreview(subEntry.getKey())
                        )
                ));

                List<Map.Entry<String, MaterialNode>> materialEntries =
                        new ArrayList<>(subEntry.getValue().materials.entrySet());

                for (int materialIndex = 0; materialIndex < materialEntries.size(); materialIndex++) {
                    Map.Entry<String, MaterialNode> materialEntry = materialEntries.get(materialIndex);
                    boolean isLastMaterial = materialIndex == materialEntries.size() - 1;

                    String materialPrefix = materialIndent + (isLastMaterial ? "└─ " : "├─ ");
                    String modelIndent = materialIndent + (isLastMaterial ? "   " : "│  ");

                    String materialPreviewItemId = CMDMaterialPreviewResolver.representativeItemId(
                            materialEntry.getKey(),
                            materialEntry.getValue().fallbackBaseItemId
                    );

                    lines.add(Text.literal(materialPrefix).append(
                            CMDHoverTextFactory.itemHover(
                                    materialEntry.getKey(),
                                    CMDChatPreviewFactory.createBaseItemPreview(materialPreviewItemId)
                            )
                    ));

                    List<ModelEntry> models = materialEntry.getValue().models;

                    for (int modelIndex = 0; modelIndex < models.size(); modelIndex++) {
                        ModelEntry modelEntry = models.get(modelIndex);
                        boolean isLastModel = modelIndex == models.size() - 1;

                        String modelPrefix = modelIndent + (isLastModel ? "└─ " : "├─ ");
                        String requiredAnvilName = CMDAnvilNameResolver.requiredNameTag(modelEntry.modelCase);

                        String modelRowLabel = modelEntry.modelCase.displayId
                                + " [Anvil: " + requiredAnvilName + "]";

                        lines.add(Text.literal(modelPrefix).append(
                                CMDHoverTextFactory.clickableCommandItem(
                                        modelRowLabel,
                                        "/cmd apply " + modelEntry.modelCase.displayId,
                                        CMDChatPreviewFactory.createAppliedModelPreview(
                                                modelEntry.baseItemId,
                                                modelEntry.modelCase
                                        )
                                )
                        ));
                    }
                }
            }

            if (categoryIndex < categoryEntries.size() - 1) {
                lines.add(Text.literal(""));
            }
        }

        return lines;
    }

    private static Map<String, CategoryNode> buildFilteredTree(String filter) {
        Map<String, CategoryNode> grouped = new TreeMap<>();

        for (Map.Entry<String, List<CMDResolvedModelCase>> entry : CMDResolvedModelRegistry.getAll().entrySet()) {
            String itemId = entry.getKey();
            String category = CMDItemClassifier.configuredCategory(itemId);
            String subgroup = CMDItemClassifier.configuredSubGroup(itemId);
            String material = CMDItemClassifier.material(itemId);

            boolean categoryMatches = matchesToken(category, filter);
            boolean subgroupMatches = matchesToken(subgroup, filter);
            boolean materialMatches = matchesToken(material, filter);
            boolean itemMatches = matchesToken(itemId, filter);

            List<CMDResolvedModelCase> matchingModels = filterModels(entry.getValue(), filter);

            boolean anyMatch = filter == null
                    || categoryMatches
                    || subgroupMatches
                    || materialMatches
                    || itemMatches
                    || !matchingModels.isEmpty();

            if (!anyMatch) continue;

            List<CMDResolvedModelCase> modelsToStore =
                    (filter == null || categoryMatches || subgroupMatches || materialMatches || itemMatches)
                            ? entry.getValue()
                            : matchingModels;

            if (modelsToStore.isEmpty()) continue;

            CategoryNode categoryNode = grouped.computeIfAbsent(category, k -> new CategoryNode());
            SubGroupNode subGroupNode = categoryNode.subgroups.computeIfAbsent(subgroup, k -> new SubGroupNode());
            MaterialNode materialNode = subGroupNode.materials.computeIfAbsent(material, k -> new MaterialNode());

            if (materialNode.fallbackBaseItemId == null) {
                materialNode.fallbackBaseItemId = itemId;
            }

            for (CMDResolvedModelCase modelCase : modelsToStore) {
                materialNode.models.add(new ModelEntry(itemId, modelCase));
            }
        }

        return grouped;
    }

    private static List<CMDResolvedModelCase> filterModels(List<CMDResolvedModelCase> modelCases, String filter) {
        if (filter == null) return new ArrayList<>(modelCases);

        List<CMDResolvedModelCase> out = new ArrayList<>();

        for (CMDResolvedModelCase modelCase : modelCases) {
            if (modelCase.displayId != null && modelCase.displayId.toLowerCase().contains(filter)) {
                out.add(modelCase);
                continue;
            }

            if (modelCase.modelPath != null && modelCase.modelPath.toLowerCase().contains(filter)) {
                out.add(modelCase);
                continue;
            }

            if (modelCase.sourceDisplayName != null && modelCase.sourceDisplayName.toLowerCase().contains(filter)) {
                out.add(modelCase);
            }
        }

        return out;
    }

    private static boolean matchesToken(String value, String filter) {
        if (filter == null) return true;
        if (value == null) return false;
        String token = value.toLowerCase();
        return token.equals(filter) || token.contains(filter);
    }

    private static String normalizeFilter(String filter) {
        if (filter == null) return null;
        String normalized = filter.trim().toLowerCase();
        return normalized.isEmpty() ? null : normalized;
    }

    private static List<Text> paginate(List<Text> lines, int page, String filter) {
        int pageSize = Math.max(1, CMDConfigManager.getSettings().listPageSize);
        int safePage = Math.max(1, page);

        int start = (safePage - 1) * pageSize;
        int end = Math.min(lines.size(), start + pageSize);

        if (start >= lines.size()) {
            return List.of(
                    Text.literal("Custom Model Database"),
                    Text.literal(""),
                    Text.literal("No entries on this page.")
            );
        }

        List<Text> out = new ArrayList<>(lines.subList(start, end));
        int pageCount = Math.max(1, (int) Math.ceil(lines.size() / (double) pageSize));

        out.add(Text.literal(""));
        out.add(Text.literal("[Page " + safePage + "/" + pageCount + "] ")
                .append(CMDHoverTextFactory.clickableCommandText(
                        "[Prev]",
                        buildPageCommand(safePage - 1, filter),
                        "Go to previous page"
                ))
                .append(Text.literal(" "))
                .append(CMDHoverTextFactory.clickableCommandText(
                        "[Next]",
                        buildPageCommand(safePage + 1, filter),
                        "Go to next page"
                )));

        return out;
    }

    private static String buildPageCommand(int page, String filter) {
        int safePage = Math.max(1, page);
        if (filter == null || filter.isBlank()) return "/cmd list " + safePage;
        return "/cmd list filter " + filter + " " + safePage;
    }

    private static class CategoryNode {
        private final Map<String, SubGroupNode> subgroups = new TreeMap<>();
    }

    private static class SubGroupNode {
        private final Map<String, MaterialNode> materials = new TreeMap<>();
    }

    private static class MaterialNode {
        private String fallbackBaseItemId;
        private final List<ModelEntry> models = new ArrayList<>();
    }

    private static class ModelEntry {
        private final String baseItemId;
        private final CMDResolvedModelCase modelCase;

        private ModelEntry(String baseItemId, CMDResolvedModelCase modelCase) {
            this.baseItemId = baseItemId;
            this.modelCase = modelCase;
        }
    }
}
