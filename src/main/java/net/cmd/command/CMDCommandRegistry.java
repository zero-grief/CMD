package net.cmd.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

/**
 * Central Brigadier/Fabric command registration for CMD.
 */
public class CMDCommandRegistry {

    /**
     * Registers the CMD command tree with Fabric's command registration callback.
     */
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> register(dispatcher));
    }

    /**
     * Builds the actual `/cmd` command tree.
     */
    private static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("cmd")
                .requires(CMDPermissionHelper::canRoot)
                .executes(CMDModelCommand::executeRoot)

                .then(CommandManager.literal("help")
                        .requires(CMDPermissionHelper::canHelp)
                        .executes(CMDModelCommand::executeHelp))

                .then(CommandManager.literal("list")
                        .requires(CMDPermissionHelper::canList)
                        .executes(CMDModelCommand::executeList)
                        .then(CommandManager.literal("filter")
                                .requires(CMDPermissionHelper::canListFilter)
                                .then(CommandManager.argument("query", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                                        .executes(CMDModelCommand::executeListFilter))))

                .then(CommandManager.literal("filters")
                        .requires(CMDPermissionHelper::canFilters)
                        .executes(CMDModelCommand::executeFilters))

                .then(CommandManager.literal("apply")
                        .requires(CMDPermissionHelper::canApply)
                        .then(CommandManager.argument("model", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                                .executes(CMDModelCommand::executeApply)))

                .then(CommandManager.literal("reset")
                        .requires(CMDPermissionHelper::canReset)
                        .executes(CMDModelCommand::executeReset))

                .then(CommandManager.literal("reload")
                        .requires(CMDPermissionHelper::canReload)
                        .executes(CMDReloadCommand::reload))

                .then(CommandManager.literal("rebuild")
                        .requires(CMDPermissionHelper::canRebuild)
                        .executes(CMDMergeCommand::previewRebuild)
                        .then(CommandManager.literal("confirm")
                                .requires(CMDPermissionHelper::canRebuildConfirm)
                                .executes(CMDMergeCommand::confirmRebuild))
                        .then(CommandManager.literal("deny")
                                .requires(CMDPermissionHelper::canRebuildDeny)
                                .executes(CMDMergeCommand::denyRebuild))));
    }
}
