/*
 * Plugin made by Lixqa Development.
 * Do not share the source code
 * Website: https://lix.qa/
 * Discord: https://discord.gg/ldev
 * */

package dev.lixqa.amethystControl;

import dev.lixqa.amethystControl.commands.SpawnCrystalsCommand;
import dev.lixqa.amethystControl.listeners.FortuneListener;
import dev.lixqa.amethystControl.listeners.GrowthListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class AmethystControl extends JavaPlugin {
    private static AmethystControl instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new FortuneListener(), this);
        getServer().getPluginManager().registerEvents(new GrowthListener(), this);
        getCommand("spawncrystals").setExecutor(new SpawnCrystalsCommand());
    }

    public static AmethystControl getInstance() {
        return instance;
    }
}
