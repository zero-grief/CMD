package net.cmd.compat;

import java.util.Objects;

/**
 * Fully resolved model case entry used by CMD's pack-driven registry.
 *
 * This becomes the canonical entry shape for:
 * - chat browser
 * - hover previews
 * - smithing resolution
 * - admin apply
 *
 * It stores the resolved result of joining:
 * - vanilla item definition cases under assets/minecraft/items/*.json
 * - actual model files under assets/<namespace>/models/**/*.json
 */
public class CMDResolvedModelCase {

    public String baseItemId;
    public String displayId;
    public String modelValue;
    public String modelNamespace;
    public String modelPath;
    public String assetNamespace;
    public boolean equippable;
    public SelectorType selectorType = SelectorType.UNKNOWN;
    public PropertyType propertyType = PropertyType.UNKNOWN;
    public String selectorValue;
    public String sourceDisplayName;
    public String sourcePackId;
    public String itemDefinitionPath;
    public String modelFilePath;
    public String suggestedAnvilText;

    public CMDResolvedModelCase() {
    }

    public String registryKey() {
        return safe(baseItemId)
                + "|"
                + safe(displayId)
                + "|"
                + safe(modelNamespace)
                + "|"
                + safe(modelPath)
                + "|"
                + safe(modelValue)
                + "|"
                + selectorType.name()
                + "|"
                + propertyType.name();
    }

    public String debugSummary() {
        return "CMDResolvedModelCase{"
                + "baseItemId='" + baseItemId + '\''
                + ", displayId='" + displayId + '\''
                + ", modelValue='" + modelValue + '\''
                + ", modelNamespace='" + modelNamespace + '\''
                + ", modelPath='" + modelPath + '\''
                + ", assetNamespace='" + assetNamespace + '\''
                + ", equippable=" + equippable
                + ", selectorType=" + selectorType
                + ", propertyType=" + propertyType
                + '}';
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    @Override
    public String toString() {
        return debugSummary();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CMDResolvedModelCase other)) return false;
        return Objects.equals(registryKey(), other.registryKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(registryKey());
    }

    public enum SelectorType {
        SELECT,
        RANGE_DISPATCH,
        LEGACY_NAME,
        UNKNOWN
    }

    public enum PropertyType {
        CUSTOM_MODEL_DATA,
        CUSTOM_NAME,
        UNKNOWN
    }
}
