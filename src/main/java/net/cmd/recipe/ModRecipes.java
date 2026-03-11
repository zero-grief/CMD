package net.cmd.recipe;

import net.cmd.core.CMDServer;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Registers CMD recipe serializers.
 *
 * This follows the original CIM structure closely, including the deprecated
 * recipe packet codec stub, because that was part of the user's stable reference.
 */
public class ModRecipes {

    public static final RecipeSerializer<CustomItemModelSmithingRecipe> CUSTOM_ITEM_MODEL_SMITHING_SERIALIZER =
            new CustomItemModelSmithingRecipe.Serializer();

    public static void init() {
        Registry.register(
                Registries.RECIPE_SERIALIZER,
                Identifier.of(CMDServer.MOD_ID, "smithing"),
                CUSTOM_ITEM_MODEL_SMITHING_SERIALIZER
        );
    }

    public static <B, T> PacketCodec<B, T> deprecatedRecipePacketCodec() {
        return PacketCodec.of((buffer, value) -> {
            throw new IllegalStateException("Recipe packet codecs are deprecated");
        }, buffer -> {
            throw new IllegalStateException("Recipe packet codecs are deprecated");
        });
    }
}
