/*
 * Plugin made by Lixqa Development.
 * Do not share the source code
 * Website: https://lix.qa/
 * Discord: https://discord.gg/ldev
 * */

package dev.lixqa.amethystControl.listeners;

import dev.lixqa.amethystControl.AmethystControl;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class FortuneListener implements Listener {
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.AMETHYST_CLUSTER) {
            event.setDropItems(false);
            ItemStack minted = AmethystControl.getInstance().mintCrystal();
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), minted);
        }
    }
}
