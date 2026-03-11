package net.cmd.core;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.cmd.command.CMDCommandRegistry;
import net.cmd.config.CMDConfigManager;
import net.cmd.preview.PreviewRotationTask;
import net.cmd.recipe.ModRecipes;
import net.cmd.recipe.ingredient.ModIngredients;

/**
 * Main CMD mod entrypoint.
 */
public class CMDServer implements ModInitializer {

    public static final String MOD_ID = "cmd";

    @Override
    public void onInitialize() {
        System.out.println("[CMD] Initializing Custom Model Data framework...");
        CMDConfigManager.load();
        CMDCommandRegistry.register();
        ModIngredients.init();
        ModRecipes.init();
        CMDStartupBuildManager.handleStartupBuild();
        ServerTickEvents.END_SERVER_TICK.register(server -> PreviewRotationTask.tick());
    }
}
