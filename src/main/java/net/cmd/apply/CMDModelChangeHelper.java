package net.cmd.apply;

import net.cmd.compat.CMDResolvedModelCase;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

/**
 * Shared item component application rules for CMD.
 *
 * Preferred path:
 * - resolved cases from the pack-driven registry
 *
 * Fallback path:
 * - raw text parsing when a resolved case is not available
 */
public class CMDModelChangeHelper {

    public record ParsedModelInput(String raw, String modelId, String assetNamespace, boolean namespaced) {}

    public static ParsedModelInput parse(String input) {
        String trimmed = input == null ? "" : input.trim();

        String modelString = trimmed;
        String assetNamespace = null;
        boolean namespaced = false;

        int colonIndex = trimmed.indexOf(':');
        if (colonIndex > 0 && colonIndex < trimmed.length() - 1) {
            String namespace = trimmed.substring(0, colonIndex).trim();
            String path = trimmed.substring(colonIndex + 1).trim();

            if (!namespace.isEmpty() && !path.isEmpty()) {
                modelString = path;
                assetNamespace = namespace;
                namespaced = true;
            }
        }

        return new ParsedModelInput(trimmed, modelString, assetNamespace, namespaced);
    }

    public static ComponentChanges buildApplyChanges(ItemStack item, String input) {
        ParsedModelInput parsed = parse(input);
        return buildApplyChanges(item, parsed.modelId(), parsed.assetNamespace());
    }

    public static ComponentChanges buildApplyChanges(ItemStack item, CMDResolvedModelCase resolvedCase) {
        if (resolvedCase == null) {
            return buildApplyChanges(item, "", null);
        }

        if (resolvedCase.propertyType == CMDResolvedModelCase.PropertyType.CUSTOM_MODEL_DATA
                && resolvedCase.selectorType == CMDResolvedModelCase.SelectorType.RANGE_DISPATCH) {
            float threshold = parseFloatOrDefault(resolvedCase.selectorValue, 0.0F);
            return buildApplyChanges(item, resolvedCase.modelValue, resolvedCase.assetNamespace, new CustomModelDataComponent(
                    List.of(threshold),
                    List.of(),
                    List.of(),
                    List.of()
            ));
        }

        String cmdStringValue = resolvedCase.selectorValue;
        if (cmdStringValue == null || cmdStringValue.isBlank()) {
            cmdStringValue = resolvedCase.modelValue;
        }

        return buildApplyChanges(item, resolvedCase.modelValue, resolvedCase.assetNamespace, new CustomModelDataComponent(
                List.of(),
                List.of(),
                List.of(cmdStringValue),
                List.of()
        ));
    }

    private static ComponentChanges buildApplyChanges(ItemStack item, String modelValue, String assetNamespace) {
        return buildApplyChanges(item, modelValue, assetNamespace, new CustomModelDataComponent(
                List.of(),
                List.of(),
                List.of(modelValue),
                List.of()
        ));
    }

    private static ComponentChanges buildApplyChanges(ItemStack item, String modelValue, String assetNamespace, CustomModelDataComponent modelComponent) {

        ComponentChanges.Builder builder = ComponentChanges.builder()
                .add(DataComponentTypes.CUSTOM_MODEL_DATA, modelComponent)
                .add(DataComponentTypes.LORE, new LoreComponent(List.of(
                        Text.literal("Model: " + modelValue)
                                .setStyle(Style.EMPTY.withColor(Formatting.GRAY))
                )));

        if (assetNamespace != null
                && !assetNamespace.isBlank()
                && item.getDefaultComponents().contains(DataComponentTypes.EQUIPPABLE)) {

            ComponentMap componentMap = item.getComponents();
            EquippableComponent baseEquippable = componentMap.get(DataComponentTypes.EQUIPPABLE);

            if (baseEquippable != null) {
                RegistryKey<net.minecraft.item.equipment.EquipmentAsset> assetKey = EquipmentAssetKeys.register(assetNamespace);

                EquippableComponent updated = new EquippableComponent(
                        baseEquippable.slot(),
                        baseEquippable.equipSound(),
                        Optional.of(assetKey),
                        baseEquippable.cameraOverlay(),
                        baseEquippable.allowedEntities(),
                        baseEquippable.dispensable(),
                        baseEquippable.swappable(),
                        baseEquippable.damageOnHurt(),
                        baseEquippable.equipOnInteract(),
                        baseEquippable.canBeSheared(),
                        baseEquippable.shearingSound()
                );
                builder.add(DataComponentTypes.EQUIPPABLE, updated);
            }
        }

        return builder.build();
    }


    private static float parseFloatOrDefault(String value, float fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    public static ComponentChanges buildResetChanges(ItemStack item) {
        ComponentChanges.Builder builder = ComponentChanges.builder()
                .remove(DataComponentTypes.CUSTOM_MODEL_DATA)
                .remove(DataComponentTypes.LORE);

        if (item.getDefaultComponents().contains(DataComponentTypes.EQUIPPABLE)) {
            EquippableComponent defaultEquippable = item.getDefaultComponents().get(DataComponentTypes.EQUIPPABLE);
            if (defaultEquippable != null) {
                builder.add(DataComponentTypes.EQUIPPABLE, defaultEquippable);
            }
        }

        return builder.build();
    }
}
