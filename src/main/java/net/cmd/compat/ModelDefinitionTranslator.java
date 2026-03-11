package net.cmd.compat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelDefinitionTranslator {

    public static List<CMDResolvedModelCase> translateResolved(
            JsonObject root,
            String baseItem,
            String itemDefinitionPath,
            String sourcePackId,
            Map<String, CMDPackModelFile> modelIndex
    ) {
        List<CMDResolvedModelCase> out = new ArrayList<>();
        if (!root.has("model")) return out;

        JsonObject model = root.getAsJsonObject("model");
        if (!model.has("property") || !model.has("type")) return out;

        String type = model.get("type").getAsString();
        String property = model.get("property").getAsString();

        if (type.contains("select") && isCustomModelData(property) && model.has("cases")) {
            out.addAll(parseStringCases(model.getAsJsonArray("cases"), baseItem, itemDefinitionPath, sourcePackId, modelIndex));
            return out;
        }
        if (type.contains("range_dispatch") && isCustomModelData(property) && model.has("entries")) {
            out.addAll(parseNumericEntries(model.getAsJsonArray("entries"), baseItem, itemDefinitionPath, sourcePackId, modelIndex));
            return out;
        }
        if (type.contains("select") && isCustomName(property) && model.has("cases")) {
            out.addAll(parseCustomNameCases(model.getAsJsonArray("cases"), baseItem, itemDefinitionPath, sourcePackId, modelIndex));
            return out;
        }
        return out;
    }

    public static List<CMDModelCase> translate(JsonObject root, String baseItem) {
        // Legacy fallback retained for compatibility if used elsewhere.
        List<CMDResolvedModelCase> resolved = translateResolved(root, baseItem, null, null, Map.of());
        List<CMDModelCase> legacy = new ArrayList<>();
        for (CMDResolvedModelCase c : resolved) {
            legacy.add(CMDModelCase.fromResolved(c));
        }
        return legacy;
    }

    private static List<CMDResolvedModelCase> parseStringCases(
            JsonArray cases,
            String baseItem,
            String itemDefinitionPath,
            String sourcePackId,
            Map<String, CMDPackModelFile> modelIndex
    ) {
        List<CMDResolvedModelCase> out = new ArrayList<>();
        for (JsonElement element : cases) {
            JsonObject entry = element.getAsJsonObject();
            if (!entry.has("model")) continue;
            JsonObject modelObject = entry.getAsJsonObject("model");
            if (!modelObject.has("model")) continue;

            String modelRef = modelObject.get("model").getAsString();
            CMDResolvedModelCase c = createResolvedCase(
                    baseItem,
                    modelRef,
                    itemDefinitionPath,
                    sourcePackId,
                    modelIndex,
                    CMDResolvedModelCase.SelectorType.SELECT,
                    CMDResolvedModelCase.PropertyType.CUSTOM_MODEL_DATA
            );
            c.selectorValue = readPrimaryWhenValue(entry.get("when"), c.modelValue);
            out.add(c);
        }
        return out;
    }

    private static List<CMDResolvedModelCase> parseNumericEntries(
            JsonArray entries,
            String baseItem,
            String itemDefinitionPath,
            String sourcePackId,
            Map<String, CMDPackModelFile> modelIndex
    ) {
        List<CMDResolvedModelCase> out = new ArrayList<>();
        for (JsonElement element : entries) {
            JsonObject entry = element.getAsJsonObject();
            if (!entry.has("model")) continue;
            JsonObject modelObject = entry.getAsJsonObject("model");
            if (!modelObject.has("model")) continue;

            String modelRef = modelObject.get("model").getAsString();
            CMDResolvedModelCase c = createResolvedCase(
                    baseItem,
                    modelRef,
                    itemDefinitionPath,
                    sourcePackId,
                    modelIndex,
                    CMDResolvedModelCase.SelectorType.RANGE_DISPATCH,
                    CMDResolvedModelCase.PropertyType.CUSTOM_MODEL_DATA
            );
            c.selectorValue = entry.has("threshold") ? entry.get("threshold").getAsString() : null;
            out.add(c);
        }
        return out;
    }

    private static List<CMDResolvedModelCase> parseCustomNameCases(
            JsonArray cases,
            String baseItem,
            String itemDefinitionPath,
            String sourcePackId,
            Map<String, CMDPackModelFile> modelIndex
    ) {
        List<CMDResolvedModelCase> out = new ArrayList<>();
        for (JsonElement element : cases) {
            JsonObject entry = element.getAsJsonObject();
            if (!entry.has("when") || !entry.has("model")) continue;
            JsonObject modelObject = entry.getAsJsonObject("model");
            if (!modelObject.has("model")) continue;

            String modelRef = modelObject.get("model").getAsString();
            CMDResolvedModelCase c = createResolvedCase(
                    baseItem,
                    modelRef,
                    itemDefinitionPath,
                    sourcePackId,
                    modelIndex,
                    CMDResolvedModelCase.SelectorType.LEGACY_NAME,
                    CMDResolvedModelCase.PropertyType.CUSTOM_NAME
            );
            c.sourceDisplayName = readPrimaryWhenValue(entry.get("when"), c.modelValue);
            c.selectorValue = c.sourceDisplayName;
            out.add(c);
        }
        return out;
    }


    private static String readPrimaryWhenValue(JsonElement whenElement, String fallback) {
        if (whenElement == null || whenElement.isJsonNull()) {
            return fallback;
        }
        if (whenElement.isJsonPrimitive()) {
            try {
                return whenElement.getAsString();
            } catch (Exception ignored) {
                return fallback;
            }
        }
        if (whenElement.isJsonArray()) {
            JsonArray values = whenElement.getAsJsonArray();
            for (JsonElement value : values) {
                if (value != null && value.isJsonPrimitive()) {
                    try {
                        return value.getAsString();
                    } catch (Exception ignored) {
                        // Continue to fallback below.
                    }
                }
            }
        }
        return fallback;
    }

    private static CMDResolvedModelCase createResolvedCase(
            String baseItem,
            String modelRef,
            String itemDefinitionPath,
            String sourcePackId,
            Map<String, CMDPackModelFile> modelIndex,
            CMDResolvedModelCase.SelectorType selectorType,
            CMDResolvedModelCase.PropertyType propertyType
    ) {
        ResolvedModelRef ref = resolveModelRef(modelRef);
        CMDPackModelFile modelFile = modelIndex.get(ref.fullId());

        CMDResolvedModelCase c = new CMDResolvedModelCase();
        c.baseItemId = baseItem;
        c.displayId = ref.namespace() + ":" + ref.leaf();
        c.modelValue = ref.leaf();
        c.modelNamespace = ref.namespace();
        c.modelPath = ref.path();
        c.assetNamespace = modelFile != null ? modelFile.namespace : ref.namespace();
        c.equippable = isEquippableItem(baseItem);
        c.selectorType = selectorType;
        c.propertyType = propertyType;
        c.itemDefinitionPath = itemDefinitionPath;
        c.modelFilePath = modelFile != null ? modelFile.filePath : null;
        c.sourcePackId = sourcePackId;
        c.suggestedAnvilText = c.equippable ? c.displayId : c.modelValue;
        return c;
    }

    private static boolean isCustomModelData(String property) {
        return property.equals("custom_model_data") || property.equals("minecraft:custom_model_data");
    }

    private static boolean isCustomName(String property) {
        return property.equals("custom_name") || property.equals("minecraft:custom_name");
    }

    public static String deriveCanonicalModelId(String modelPath) {
        ResolvedModelRef ref = resolveModelRef(modelPath);
        return ref.namespace() + ":" + ref.leaf();
    }

    public static ResolvedModelRef resolveModelRef(String modelRef) {
        String namespace = "minecraft";
        String path = modelRef;

        int colon = modelRef.indexOf(':');
        if (colon > 0) {
            namespace = modelRef.substring(0, colon);
            path = modelRef.substring(colon + 1);
        }

        if (path.startsWith("item/")) {
            path = path.substring("item/".length());
        }

        String leaf = path;
        int slash = path.lastIndexOf('/');
        if (slash >= 0 && slash < path.length() - 1) {
            leaf = path.substring(slash + 1);
        }

        return new ResolvedModelRef(namespace, path, leaf);
    }

    private static boolean isEquippableItem(String baseItemId) {
        try {
            Item item = Registries.ITEM.get(Identifier.of("minecraft", baseItemId));
            return item != null
                    && item != Items.AIR
                    && item.getDefaultComponents().contains(DataComponentTypes.EQUIPPABLE);
        } catch (Exception e) {
            return false;
        }
    }

    public record ResolvedModelRef(String namespace, String path, String leaf) {
        public String fullId() {
            return namespace + ":" + path;
        }
    }

    public static class CMDPackModelFile {
        public final String namespace;
        public final String path;
        public final String filePath;
        public final String sourcePackId;

        public CMDPackModelFile(String namespace, String path, String filePath, String sourcePackId) {
            this.namespace = namespace;
            this.path = path;
            this.filePath = filePath;
            this.sourcePackId = sourcePackId;
        }

        public String fullId() {
            return namespace + ":" + path;
        }
    }
}
