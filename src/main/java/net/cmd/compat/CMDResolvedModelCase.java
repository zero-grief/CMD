package net.cmd.compat;

import java.util.Objects;

/**
 * Fully resolved model entry used by CMD's pack-driven registry.
 *
 * This is the richer long-term model shape for:
 * - browser output
 * - hover previews
 * - smithing resolution
 * - admin apply
 * - rebuild diagnostics
 *
 * It represents the resolved result of joining:
 * - vanilla item-definition entries under assets/minecraft/items/*.json
 * - actual model files under assets/<namespace>/models/.../*.json
 */
public class CMDResolvedModelCase {

    /**
     * Base vanilla item id path.
     *
     * Example:
     * - diamond_sword
     * - totem_of_undying
     */
    public String baseItemId;

    /**
     * Human-facing or registry-facing display id.
     *
     * Usually namespace:modelValue.
     */
    public String displayId;

    /**
     * Canonical merged model value used as the primary trigger identity where
     * possible.
     *
     * Example:
     * - katana
     * - kings_blade
     * - villagerhat
     */
    public String modelValue;

    /**
     * Namespace of the resolved model reference.
     */
    public String modelNamespace;

    /**
     * Full resolved model path under the namespace, without the .json suffix.
     *
     * Example:
     * - armor/hev_suit
     * - katana
     */
    public String modelPath;

    /**
     * Namespace CMD should use when determining the actual asset source.
     *
     * This is especially important for equippables.
     */
    public String assetNamespace;

    /**
     * Whether the base item is equippable according to the running game's item
     * component data.
     */
    public boolean equippable;

    /**
     * High-level selector shape from the source definition.
     */
    public SelectorType selectorType = SelectorType.UNKNOWN;

    /**
     * High-level property family from the source definition.
     */
    public PropertyType propertyType = PropertyType.UNKNOWN;

    /**
     * Original selector value from the source definition when meaningful.
     *
     * Examples:
     * - King's Blade
     * - katana
     * - 1.0 / threshold represented as a string
     */
    public String selectorValue;

    /**
     * Human-facing source display name when a source definition originally used
     * something more readable than the canonical merged identifier.
     */
    public String sourceDisplayName;

    /**
     * Source pack identifier used during scanning.
     *
     * Example:
     * - archive:Arthur.zip
     * - dir:ExamplePack
     */
    public String sourcePackId;

    /**
     * Path of the item definition file where this resolved entry came from.
     */
    public String itemDefinitionPath;

    /**
     * Path of the actual resolved model file, if CMD found it during scan.
     */
    public String modelFilePath;

    /**
     * Suggested Name Tag text for player-facing usage.
     */
    public String suggestedAnvilText;

    public CMDResolvedModelCase() {
    }

    public String registryKey() {
        return safe(baseItemId) + "|"
                + safe(displayId) + "|"
                + safe(modelNamespace) + "|"
                + safe(modelPath) + "|"
                + safe(modelValue) + "|"
                + selectorType.name() + "|"
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