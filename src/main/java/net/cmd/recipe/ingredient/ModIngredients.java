package net.cmd.recipe.ingredient;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;

/**
 * Registers custom recipe ingredients used by CMD smithing.
 *
 * This follows the original CIM structure closely to minimize serializer issues.
 */
public class ModIngredients {

    public static final CustomIngredientSerializer<AnyItemIngredient> ANY_ITEM_SERIALIZER =
            new AnyItemIngredient.Serializer();

    public static void init() {
        CustomIngredientSerializer.register(ANY_ITEM_SERIALIZER);
    }
}
