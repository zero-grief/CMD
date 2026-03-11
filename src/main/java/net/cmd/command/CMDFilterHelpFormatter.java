package net.cmd.command;

import net.cmd.config.CMDCategoryConfig;
import net.cmd.config.CMDConfigManager;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Builds help output for available category and item filters.
 */
public class CMDFilterHelpFormatter {

    public static List<Text> buildFilterHelp() {
        List<Text> lines = new ArrayList<>();
        CMDCategoryConfig config = CMDCategoryConfig.fromMainConfig(CMDConfigManager.getConfig());

        lines.add(Text.literal("CMD Filters"));
        lines.add(Text.literal(""));
        lines.add(Text.literal("Use: /cmd list filter <query>"));
        lines.add(Text.literal(""));

        for (Map.Entry<String, CMDCategoryConfig.CategoryDefinition> categoryEntry : config.categories.entrySet()) {
            String categoryName = categoryEntry.getKey();
            ItemStack categoryPreview = CMDChatPreviewFactory.createCategoryPreview(categoryName);

            lines.add(CMDHoverTextFactory.clickableCommandItem(
                    categoryName,
                    "/cmd list filter " + categoryName.toLowerCase(),
                    categoryPreview
            ));

            for (Map.Entry<String, java.util.List<String>> subgroupEntry : categoryEntry.getValue().subgroups.entrySet()) {
                String subgroupName = subgroupEntry.getKey();
                ItemStack subgroupPreview = CMDChatPreviewFactory.createSubgroupPreview(subgroupName);

                lines.add(Text.literal("└─ ").append(
                        CMDHoverTextFactory.clickableCommandItem(
                                subgroupName,
                                "/cmd list filter " + subgroupName.toLowerCase(),
                                subgroupPreview
                        )
                ));

                java.util.List<String> items = subgroupEntry.getValue();
                for (int i = 0; i < items.size(); i++) {
                    String itemId = items.get(i);
                    boolean isLast = i == items.size() - 1;
                    String prefix = isLast ? "   └─ " : "   ├─ ";

                    lines.add(Text.literal(prefix).append(
                            CMDHoverTextFactory.clickableCommandItem(
                                    itemId,
                                    "/cmd list filter " + itemId,
                                    CMDChatPreviewFactory.createBaseItemPreview(itemId)
                            )
                    ));
                }
            }

            lines.add(Text.literal(""));
        }

        return lines;
    }
}
