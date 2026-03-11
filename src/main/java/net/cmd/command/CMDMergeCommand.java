package net.cmd.command;

import com.mojang.brigadier.context.CommandContext;
import net.cmd.reload.LiveReloadManager;
import net.cmd.reload.LiveReloadManager.RebuildPreviewReport;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

/**
 * Administrative rebuild command handler.
 */
public class CMDMergeCommand {

    /**
     * Executes the rebuild preview/analyze step.
     */
    public static int previewRebuild(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        RebuildPreviewReport report = LiveReloadManager.prepareRebuildPreview();

        source.sendFeedback(() -> Text.literal("CMD rebuild analysis complete."), false);
        source.sendFeedback(() -> Text.literal("Paths read:"), false);

        for (String path : report.pathsRead()) {
            source.sendFeedback(() -> Text.literal("- " + path), false);
        }

        source.sendFeedback(() -> Text.literal("Archives found: " + report.archivesFound()), false);
        source.sendFeedback(() -> Text.literal("Model JSON files found: " + report.totalModelJsonCount()), false);
        source.sendFeedback(() -> Text.literal(" - item: " + report.itemModelJsonCount()), false);
        source.sendFeedback(() -> Text.literal(" - equipment: " + report.equipmentModelJsonCount()), false);
        source.sendFeedback(() -> Text.literal(" - humanoid: " + report.humanoidModelJsonCount()), false);

        source.sendFeedback(() -> Text.literal("Run /cmd rebuild confirm to build the merged pack."), false);
        source.sendFeedback(() -> Text.literal("Run /cmd rebuild deny to cancel the pending rebuild."), false);

        return 1;
    }

    /**
     * Executes the confirmed rebuild request.
     */
    public static int confirmRebuild(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        if (!LiveReloadManager.hasPendingRebuild()) {
            source.sendError(Text.literal("No rebuild is pending. Run /cmd rebuild first."));
            return 0;
        }

        LiveReloadManager.confirmPreparedRebuild();
        source.sendFeedback(() -> Text.literal("CMD rebuild completed."), false);
        return 1;
    }

    /**
     * Cancels a pending rebuild prepared by `/cmd rebuild`.
     */
    public static int denyRebuild(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        if (!LiveReloadManager.hasPendingRebuild()) {
            source.sendFeedback(() -> Text.literal("No pending rebuild to cancel."), false);
            return 1;
        }

        LiveReloadManager.cancelPreparedRebuild();
        source.sendFeedback(() -> Text.literal("CMD rebuild cancelled."), false);
        return 1;
    }
}
