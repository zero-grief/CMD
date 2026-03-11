package net.cmd.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.cmd.apply.CMDModelChangeHelper;
import net.cmd.compat.CMDResolvedModelCase;
import net.cmd.compat.CMDResolvedModelRegistry;
import net.cmd.recipe.ingredient.AnyItemIngredient;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.IngredientPlacement;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Nullables;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Smithing recipe adapted from the original CIM mod.
 *
 * Preferred path:
 * - resolve by base item + anvil text against the canonical resolved registry
 *
 * Fallback:
 * - raw text parsing if no resolved case exists
 */
public class CustomItemModelSmithingRecipe implements SmithingRecipe {

    private final Ingredient addition;

    public CustomItemModelSmithingRecipe(Ingredient addition) {
        this.addition = addition;
    }

    @Override
    public boolean matches(SmithingRecipeInput input, World world) {
        if (!input.template().isEmpty()) return false;
        if (input.base().isEmpty()) return false;
        return addition.test(input.addition());
    }

    @Override
    public ItemStack craft(SmithingRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return craft(
                input.base(),
                Nullables.map(input.addition().get(DataComponentTypes.CUSTOM_NAME), Text::getString)
        );
    }

    @Override
    public RecipeSerializer<CustomItemModelSmithingRecipe> getSerializer() {
        return ModRecipes.CUSTOM_ITEM_MODEL_SMITHING_SERIALIZER;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return IngredientPlacement.NONE;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public Optional<Ingredient> template() {
        return Optional.empty();
    }

    @Override
    public Ingredient base() {
        return AnyItemIngredient.INSTANCE.toVanilla();
    }

    @Override
    public Optional<Ingredient> addition() {
        return Optional.of(addition);
    }

    public static ItemStack craft(ItemStack input, @Nullable String customModelText) {
        ItemStack result = input.copyWithCount(1);
        ComponentChanges changes;

        if (customModelText != null && !customModelText.isBlank()) {
            String baseItemId = Registries.ITEM.getId(result.getItem()).getPath();
            CMDResolvedModelCase resolved = CMDResolvedModelRegistry.findByAnvilInput(baseItemId, customModelText);

            changes = resolved != null
                    ? CMDModelChangeHelper.buildApplyChanges(result, resolved)
                    : CMDModelChangeHelper.buildApplyChanges(result, customModelText);
        } else {
            changes = CMDModelChangeHelper.buildResetChanges(result);
        }

        result.applyChanges(changes);
        return result;
    }

    public static class Serializer implements RecipeSerializer<CustomItemModelSmithingRecipe> {

        private static final MapCodec<CustomItemModelSmithingRecipe> CODEC =
                RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Ingredient.CODEC.fieldOf("addition").forGetter(recipe -> recipe.addition)
                ).apply(instance, CustomItemModelSmithingRecipe::new));

        private static final PacketCodec<RegistryByteBuf, CustomItemModelSmithingRecipe> PACKET_CODEC =
                ModRecipes.deprecatedRecipePacketCodec();

        @Override
        public MapCodec<CustomItemModelSmithingRecipe> codec() {
            return CODEC;
        }

        @Override
        @Deprecated
        public PacketCodec<RegistryByteBuf, CustomItemModelSmithingRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
