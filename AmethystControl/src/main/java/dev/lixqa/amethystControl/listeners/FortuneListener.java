/*
 * Plugin made by Lixqa Development.
 * Do not share the source code
 * Website: https://lix.qa/
 * Discord: https://discord.gg/ldev
 * */

package dev.lixqa.amethystControl.listeners;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class FortuneListener implements Listener {
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.AMETHYST_CLUSTER) {
            ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
            if (tool.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                event.setDropItems(false);
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.AMETHYST_SHARD, 1));
            }
        }
    }
}
