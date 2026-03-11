package net.cmd.compat;

/**
 * Legacy normalized case shape used by the merge/build pipeline.
 *
 * This class is retained for compatibility, while richer metadata now lives in
 * CMDResolvedModelCase.
 */
public class CMDModelCase {
    public String baseItem;
    public String modelId;
    public String modelPath;
    public String sourceDisplayName;
    public Integer sourceThreshold;
    public SourceType sourceType;

    // Metadata mirrored from the resolved registry when available
    public String modelValue;
    public String modelNamespace;
    public String assetNamespace;
    public boolean equippable;
    public String itemDefinitionPath;
    public String modelFilePath;
    public String suggestedAnvilText;

    public static CMDModelCase fromResolved(CMDResolvedModelCase resolved) {
        CMDModelCase out = new CMDModelCase();
        out.baseItem = resolved.baseItemId;
        out.modelId = resolved.displayId;
        out.modelPath = resolved.modelNamespace + ":" + resolved.modelPath;
        out.sourceDisplayName = resolved.sourceDisplayName;
        out.modelValue = resolved.modelValue;
        out.modelNamespace = resolved.modelNamespace;
        out.assetNamespace = resolved.assetNamespace;
        out.equippable = resolved.equippable;
        out.itemDefinitionPath = resolved.itemDefinitionPath;
        out.modelFilePath = resolved.modelFilePath;
        out.suggestedAnvilText = resolved.suggestedAnvilText;

        out.sourceType = switch (resolved.propertyType) {
            case CUSTOM_NAME -> SourceType.CUSTOM_NAME;
            case CUSTOM_MODEL_DATA -> SourceType.CMD_STRING;
            default -> SourceType.CMD_STRING;
        };

        return out;
    }
}
