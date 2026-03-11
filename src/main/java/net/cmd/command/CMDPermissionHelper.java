package net.cmd.command;

import net.cmd.config.CMDCommandPermissionsConfig;
import net.cmd.config.CMDConfigManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Central command permission checks for CMD.
 *
 * Current intended behavior:
 * - root/help/list/filter/filters are open to players by default
 * - apply/reset are available to:
 *   - host/server/admin
 *   - players granted the configured permission node
 *   - creative players as a built-in fallback
 * - reload/rebuild/rebuild confirm/rebuild deny are available to:
 *   - host/server/admin
 *   - players granted the configured permission node
 * - creative is NOT an admin fallback for reload/rebuild actions
 *
 * If a permission mod/provider is installed, the configured nodes can be used
 * without making such a mod mandatory for CMD.
 */
public class CMDPermissionHelper {

    /**
     * Reads the current flattened command-permissions view from the main config.
     */
    private static CMDCommandPermissionsConfig cfg() {
        return CMDCommandPermissionsConfig.fromMainConfig(CMDConfigManager.getConfig());
    }

    /**
     * Returns true when the source is not a normal player source.
     */
    private static boolean isConsoleOrServerSource(ServerCommandSource source) {
        try {
            return source.getPlayer() == null;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Returns the player source if one exists, otherwise null.
     */
    private static ServerPlayerEntity getPlayerSafe(ServerCommandSource source) {
        try {
            return source.getPlayer();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns true if the player source exists and is currently in creative mode.
     */
    private static boolean isCreativePlayer(ServerCommandSource source) {
        ServerPlayerEntity player = getPlayerSafe(source);
        return player != null && player.isCreative();
    }

    /**
     * Returns true if the source is represented in the server OP list.
     */
    private static boolean isListedOperator(ServerCommandSource source) {
        try {
            ServerPlayerEntity player = getPlayerSafe(source);
            if (player == null) {
                return true;
            }

            String playerName = player.getName().getString();
            if (playerName == null || playerName.isBlank()) {
                return false;
            }

            String[] opNames = source.getServer()
                    .getPlayerManager()
                    .getOpList()
                    .getNames();

            for (String opName : opNames) {
                if (playerName.equalsIgnoreCase(opName)) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns true for any source CMD should treat as administrative in its
     * built-in fallback rules.
     */
    private static boolean isAdminSource(ServerCommandSource source) {
        return isConsoleOrServerSource(source) || isListedOperator(source);
    }

    /**
     * Checks one configured permission node through an optional external
     * permissions provider.
     */
    private static boolean checkNode(ServerCommandSource source, String node, boolean fallbackAdminOnly) {
        if (node == null || node.isBlank()) {
            return fallbackAdminOnly ? isAdminSource(source) : true;
        }

        Boolean external = CMDOptionalPermissionBridge.check(source, node, fallbackAdminOnly ? 4 : 0);
        if (external != null) {
            return external;
        }

        return fallbackAdminOnly ? isAdminSource(source) : true;
    }

    /**
     * Shared permission evaluation for one command.
     */
    private static boolean evaluate(
            boolean usePermissionNode,
            String permissionNode,
            boolean requiresAdminFallback,
            boolean allowCreativeBypass,
            ServerCommandSource source
    ) {
        if (isConsoleOrServerSource(source)) {
            return true;
        }

        if (allowCreativeBypass && isCreativePlayer(source)) {
            return true;
        }

        if (usePermissionNode) {
            return checkNode(source, permissionNode, requiresAdminFallback);
        }

        if (!requiresAdminFallback) {
            return true;
        }

        return isAdminSource(source);
    }

    public static boolean canRoot(ServerCommandSource source) {
        CMDCommandPermissionsConfig c = cfg();
        return evaluate(c.rootUsePermissionNode, c.rootPermissionNode, c.rootRequiresAdminFallback, false, source);
    }

    public static boolean canHelp(ServerCommandSource source) {
        CMDCommandPermissionsConfig c = cfg();
        return evaluate(c.helpUsePermissionNode, c.helpPermissionNode, c.helpRequiresAdminFallback, false, source);
    }

    public static boolean canList(ServerCommandSource source) {
        CMDCommandPermissionsConfig c = cfg();
        return evaluate(c.listUsePermissionNode, c.listPermissionNode, c.listRequiresAdminFallback, false, source);
    }

    public static boolean canListFilter(ServerCommandSource source) {
        CMDCommandPermissionsConfig c = cfg();
        return evaluate(c.listFilterUsePermissionNode, c.listFilterPermissionNode, c.listFilterRequiresAdminFallback, false, source);
    }

    public static boolean canFilters(ServerCommandSource source) {
        CMDCommandPermissionsConfig c = cfg();
        return evaluate(c.filtersUsePermissionNode, c.filtersPermissionNode, c.filtersRequiresAdminFallback, false, source);
    }

    public static boolean canApply(ServerCommandSource source) {
        CMDCommandPermissionsConfig c = cfg();
        return evaluate(c.applyUsePermissionNode, c.applyPermissionNode, c.applyRequiresAdminFallback, c.applyAllowCreativeBypass, source);
    }

    public static boolean canReset(ServerCommandSource source) {
        CMDCommandPermissionsConfig c = cfg();
        return evaluate(c.resetUsePermissionNode, c.resetPermissionNode, c.resetRequiresAdminFallback, c.resetAllowCreativeBypass, source);
    }

    public static boolean canReload(ServerCommandSource source) {
        CMDCommandPermissionsConfig c = cfg();
        return evaluate(c.reloadUsePermissionNode, c.reloadPermissionNode, c.reloadRequiresAdminFallback, false, source);
    }

    public static boolean canRebuild(ServerCommandSource source) {
        CMDCommandPermissionsConfig c = cfg();
        return evaluate(c.rebuildUsePermissionNode, c.rebuildPermissionNode, c.rebuildRequiresAdminFallback, false, source);
    }

    public static boolean canRebuildConfirm(ServerCommandSource source) {
        CMDCommandPermissionsConfig c = cfg();
        return evaluate(
                c.rebuildConfirmUsePermissionNode,
                c.rebuildConfirmPermissionNode,
                c.rebuildConfirmRequiresAdminFallback,
                false,
                source
        );
    }

    public static boolean canRebuildDeny(ServerCommandSource source) {
        CMDCommandPermissionsConfig c = cfg();
        return evaluate(
                c.rebuildDenyUsePermissionNode,
                c.rebuildDenyPermissionNode,
                c.rebuildDenyRequiresAdminFallback,
                false,
                source
        );
    }
}
