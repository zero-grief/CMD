package net.cmd.command;

/**
 * Resolves a representative vanilla item for a material label in the chat browser.
 */
public class CMDMaterialPreviewResolver {

    public static String representativeItemId(String materialName, String fallbackBaseItemId) {
        if (materialName == null || materialName.isBlank()) return fallbackBaseItemId;
        return switch (materialName) {
            case "Wooden" -> "oak_planks";
            case "Stone" -> "cobblestone";
            case "Iron" -> "iron_ingot";
            case "Golden" -> "gold_ingot";
            case "Diamond" -> "diamond";
            case "Netherite" -> "netherite_ingot";
            case "Leather" -> "leather";
            case "Chainmail" -> "chainmail_helmet";
            case "Turtle" -> "turtle_helmet";
            case "Shield" -> "shield";
            case "Bow" -> "bow";
            case "Crossbow" -> "crossbow";
            case "Trident" -> "trident";
            case "Mace" -> "mace";
            case "Elytra" -> "elytra";
            case "Totem" -> "totem_of_undying";
            case "Carved Pumpkin" -> "carved_pumpkin";
            case "Fishing Rod" -> "fishing_rod";
            case "Shears" -> "shears";
            case "Flint and Steel" -> "flint_and_steel";
            case "Brush" -> "brush";
            case "Carrot on a Stick" -> "carrot_on_a_stick";
            case "Warped Fungus on a Stick" -> "warped_fungus_on_a_stick";
            default -> fallbackBaseItemId;
        };
    }
}
