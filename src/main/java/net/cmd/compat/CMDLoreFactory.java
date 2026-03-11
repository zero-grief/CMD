package net.cmd.compat;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.List;

public class CMDLoreFactory {
    public static List<Text> createTranslatedLore(CMDModelCase modelCase) {
        String key = CMDTranslationKeyFactory.fromModelPath(modelCase.modelPath);
        Text loreLine = Text.literal("Model: ").append(Text.translatable(key)).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        return List.of(loreLine);
    }
}
