package net.cmd.config;

/**
 * Flattened command-permissions view built from the main config.
 */
public class CMDCommandPermissionsConfig {

    public boolean rootUsePermissionNode;
    public String rootPermissionNode;
    public boolean rootRequiresAdminFallback;

    public boolean helpUsePermissionNode;
    public String helpPermissionNode;
    public boolean helpRequiresAdminFallback;

    public boolean listUsePermissionNode;
    public String listPermissionNode;
    public boolean listRequiresAdminFallback;

    public boolean listFilterUsePermissionNode;
    public String listFilterPermissionNode;
    public boolean listFilterRequiresAdminFallback;

    public boolean filtersUsePermissionNode;
    public String filtersPermissionNode;
    public boolean filtersRequiresAdminFallback;

    public boolean applyUsePermissionNode;
    public String applyPermissionNode;
    public boolean applyRequiresAdminFallback;
    public boolean applyAllowCreativeBypass;

    public boolean resetUsePermissionNode;
    public String resetPermissionNode;
    public boolean resetRequiresAdminFallback;
    public boolean resetAllowCreativeBypass;

    public boolean reloadUsePermissionNode;
    public String reloadPermissionNode;
    public boolean reloadRequiresAdminFallback;

    public boolean rebuildUsePermissionNode;
    public String rebuildPermissionNode;
    public boolean rebuildRequiresAdminFallback;

    public boolean rebuildConfirmUsePermissionNode;
    public String rebuildConfirmPermissionNode;
    public boolean rebuildConfirmRequiresAdminFallback;

    public boolean rebuildDenyUsePermissionNode;
    public String rebuildDenyPermissionNode;
    public boolean rebuildDenyRequiresAdminFallback;

    /**
     * Builds the flattened command-permission view from the unified main config.
     */
    public static CMDCommandPermissionsConfig fromMainConfig(CMDMainConfig main) {
        CMDCommandPermissionsConfig out = new CMDCommandPermissionsConfig();

        if (main == null) {
            return out;
        }

        out.rootUsePermissionNode = main.commands.root.usePermissionNode;
        out.rootPermissionNode = main.commands.root.permissionNode;
        out.rootRequiresAdminFallback = main.commands.root.requiresAdminFallback;

        out.helpUsePermissionNode = main.commands.help.usePermissionNode;
        out.helpPermissionNode = main.commands.help.permissionNode;
        out.helpRequiresAdminFallback = main.commands.help.requiresAdminFallback;

        out.listUsePermissionNode = main.commands.list.usePermissionNode;
        out.listPermissionNode = main.commands.list.permissionNode;
        out.listRequiresAdminFallback = main.commands.list.requiresAdminFallback;

        out.listFilterUsePermissionNode = main.commands.listFilter.usePermissionNode;
        out.listFilterPermissionNode = main.commands.listFilter.permissionNode;
        out.listFilterRequiresAdminFallback = main.commands.listFilter.requiresAdminFallback;

        out.filtersUsePermissionNode = main.commands.filters.usePermissionNode;
        out.filtersPermissionNode = main.commands.filters.permissionNode;
        out.filtersRequiresAdminFallback = main.commands.filters.requiresAdminFallback;

        out.applyUsePermissionNode = main.commands.apply.usePermissionNode;
        out.applyPermissionNode = main.commands.apply.permissionNode;
        out.applyRequiresAdminFallback = main.commands.apply.requiresAdminFallback;
        out.applyAllowCreativeBypass = main.commands.apply.allowCreativeBypass;

        out.resetUsePermissionNode = main.commands.reset.usePermissionNode;
        out.resetPermissionNode = main.commands.reset.permissionNode;
        out.resetRequiresAdminFallback = main.commands.reset.requiresAdminFallback;
        out.resetAllowCreativeBypass = main.commands.reset.allowCreativeBypass;

        out.reloadUsePermissionNode = main.commands.reload.usePermissionNode;
        out.reloadPermissionNode = main.commands.reload.permissionNode;
        out.reloadRequiresAdminFallback = main.commands.reload.requiresAdminFallback;

        out.rebuildUsePermissionNode = main.commands.rebuild.usePermissionNode;
        out.rebuildPermissionNode = main.commands.rebuild.permissionNode;
        out.rebuildRequiresAdminFallback = main.commands.rebuild.requiresAdminFallback;

        out.rebuildConfirmUsePermissionNode = main.commands.rebuildConfirm.usePermissionNode;
        out.rebuildConfirmPermissionNode = main.commands.rebuildConfirm.permissionNode;
        out.rebuildConfirmRequiresAdminFallback = main.commands.rebuildConfirm.requiresAdminFallback;

        out.rebuildDenyUsePermissionNode = main.commands.rebuildDeny.usePermissionNode;
        out.rebuildDenyPermissionNode = main.commands.rebuildDeny.permissionNode;
        out.rebuildDenyRequiresAdminFallback = main.commands.rebuildDeny.requiresAdminFallback;

        return out;
    }
}
