package net.cmd.command;

import com.mojang.brigadier.context.CommandContext;
import net.cmd.apply.CMDSecureItemApplicator;
import net.cmd.config.CMDConfigPaths;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class CMDModelCommand {

    public static int list(CommandContext<ServerCommandSource> ctx, int page) {
        ServerCommandSource src = ctx.getSource();
        for (Text line : CMDListFormatter.buildTreePage(page)) {
            src.sendFeedback(() -> line, false);
        }
        return 1;
    }

    public static int listFiltered(CommandContext<ServerCommandSource> ctx, String filter, int page) {
        ServerCommandSource src = ctx.getSource();
        for (Text line : CMDListFormatter.buildFilteredTreePage(page, filter)) {
            src.sendFeedback(() -> line, false);
        }
        return 1;
    }

    public static int help(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource src = ctx.getSource();

        src.sendFeedback(() -> Text.literal("CMD Help"), false);
        src.sendFeedback(() -> Text.literal(""), false);
        src.sendFeedback(() -> Text.literal("1. Rename a Name Tag in an anvil to the shown [Anvil: ...] text"), false);
        src.sendFeedback(() -> Text.literal("2. Put the Name Tag and your item into a smithing table"), false);
        src.sendFeedback(() -> Text.literal("3. CMD resolves the registered case and applies the correct model data"), false);
        src.sendFeedback(() -> Text.literal("4. Equippable asset source is derived from the scanned model namespace"), false);
        src.sendFeedback(() -> Text.literal("5. Use an unnamed Name Tag to reset the item"), false);
        src.sendFeedback(() -> Text.literal(""), false);
        src.sendFeedback(() -> Text.literal("Chat browser behavior:"), false);
        src.sendFeedback(() -> Text.literal("├─ Category rows use vanilla-style show_item hover"), false);
        src.sendFeedback(() -> Text.literal("├─ Subgroup rows use vanilla-style show_item hover"), false);
        src.sendFeedback(() -> Text.literal("├─ Material rows use material-linked vanilla previews such as iron -> iron_ingot"), false);
        src.sendFeedback(() -> Text.literal("└─ Model rows use show_item hover and show the exact anvil name in the row text"), false);
        src.sendFeedback(() -> Text.literal(""), false);
        src.sendFeedback(() -> Text.literal("Useful commands:"), false);
        src.sendFeedback(() -> Text.literal("├─ /cmd list"), false);
        src.sendFeedback(() -> Text.literal("├─ /cmd list filter <query>"), false);
        src.sendFeedback(() -> Text.literal("├─ /cmd filters"), false);
        src.sendFeedback(() -> Text.literal("├─ /cmd apply <namespace:model>"), false);
        src.sendFeedback(() -> Text.literal("├─ /cmd reset"), false);
        src.sendFeedback(() -> Text.literal("├─ /cmd reload"), false);
        src.sendFeedback(() -> Text.literal("└─ /cmd rebuild confirm"), false);
        src.sendFeedback(() -> Text.literal(""), false);
        src.sendFeedback(() -> Text.literal("Config file: " + CMDConfigPaths.getMainConfigFile().getPath()), false);

        return 1;
    }

    public static int filters(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource src = ctx.getSource();
        for (Text line : CMDFilterHelpFormatter.buildFilterHelp()) {
            src.sendFeedback(() -> line, false);
        }
        return 1;
    }

    public static int apply(CommandContext<ServerCommandSource> ctx, String modelId) {
        ServerPlayerEntity player;
        try {
            player = ctx.getSource().getPlayerOrThrow();
        } catch (Exception e) {
            ctx.getSource().sendError(Text.literal("This command can only be used by a player."));
            return 0;
        }

        ItemStack stack = player.getMainHandStack();
        if (stack.isEmpty()) {
            ctx.getSource().sendError(Text.literal("You must hold an item in your main hand."));
            return 0;
        }

        boolean applied = CMDSecureItemApplicator.apply(stack, modelId);
        if (!applied) {
            ctx.getSource().sendError(Text.literal("This model is not valid for the item in your hand."));
            return 0;
        }

        ctx.getSource().sendFeedback(() -> Text.literal("Applied model " + modelId), false);
        return 1;
    }

    public static int reset(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player;
        try {
            player = ctx.getSource().getPlayerOrThrow();
        } catch (Exception e) {
            ctx.getSource().sendError(Text.literal("This command can only be used by a player."));
            return 0;
        }

        ItemStack stack = player.getMainHandStack();
        if (stack.isEmpty()) {
            ctx.getSource().sendError(Text.literal("You must hold an item in your main hand."));
            return 0;
        }

        CMDSecureItemApplicator.reset(stack);
        ctx.getSource().sendFeedback(() -> Text.literal("Removed CMD model data from held item."), false);
        return 1;
    }
}
