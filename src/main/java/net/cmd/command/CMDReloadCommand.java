package net.cmd.command;

import net.cmd.reload.LiveReloadManager;

/**
 * Command that refreshes the in-memory registry only.
 */
public class CMDReloadCommand {

    public static int execute() {
        LiveReloadManager.reload();
        return 1;
    }
}
