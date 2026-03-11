package net.cmd.command;

import net.minecraft.server.command.ServerCommandSource;

/**
 * Optional bridge for external permission mods.
 *
 * Design goals:
 * - no hard dependency on external permission mods
 * - no extra setup required for normal CMD use
 * - if a compatible permission API is present, CMD can still use it
 */
public final class CMDOptionalPermissionBridge {

    private CMDOptionalPermissionBridge() {
    }

    /**
     * Returns:
     * - Boolean.TRUE if an external permission provider explicitly grants access
     * - Boolean.FALSE if an external permission provider explicitly denies access
     * - null if no compatible provider is available or no usable node is configured
     */
    public static Boolean check(ServerCommandSource source, String permissionNode, int fallbackLevel) {
        if (permissionNode == null || permissionNode.isBlank()) {
            return null;
        }

        Boolean reflected = tryFabricPermissionsApi(source, permissionNode, fallbackLevel);
        if (reflected != null) {
            return reflected;
        }

        return null;
    }

    /**
     * Tries to call Fabric Permissions API by reflection so CMD remains usable
     * even when no separate permission mod is installed.
     */
    private static Boolean tryFabricPermissionsApi(ServerCommandSource source, String permissionNode, int fallbackLevel) {
        try {
            Class<?> permissionsClass = Class.forName("me.lucko.fabric.api.permissions.v0.Permissions");

            try {
                java.lang.reflect.Method booleanMethod = permissionsClass.getMethod(
                        "check",
                        ServerCommandSource.class,
                        String.class,
                        boolean.class
                );

                Object result = booleanMethod.invoke(null, source, permissionNode, fallbackLevel >= 4);
                if (result instanceof Boolean bool) {
                    return bool;
                }
            } catch (NoSuchMethodException ignored) {
                // Try the int overload below.
            }

            try {
                java.lang.reflect.Method intMethod = permissionsClass.getMethod(
                        "check",
                        ServerCommandSource.class,
                        String.class,
                        int.class
                );

                Object result = intMethod.invoke(null, source, permissionNode, fallbackLevel);
                if (result instanceof Boolean bool) {
                    return bool;
                }
            } catch (NoSuchMethodException ignored) {
                // No compatible overload found.
            }

        } catch (ClassNotFoundException ignored) {
            // External permission API is not installed.
        } catch (Throwable ignored) {
            // Any reflection/runtime failure should fall back silently.
        }

        return null;
    }
}
