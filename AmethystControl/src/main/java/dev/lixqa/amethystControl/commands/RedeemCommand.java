package dev.lixqa.amethystControl.commands;

import dev.lixqa.amethystControl.AmethystControl;
import dev.lixqa.amethystControl.ledger.CrystalLedger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class RedeemCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("amethystcontrol.redeem")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to redeem crystals.");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can redeem crystals.");
            return true;
        }

        ItemStack stack = player.getInventory().getItemInMainHand();
        if (stack == null || stack.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You must hold a crystal to redeem it.");
            return true;
        }

        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            player.sendMessage(ChatColor.RED + "This item cannot be redeemed.");
            return true;
        }

        String rawId = meta.getPersistentDataContainer().get(AmethystControl.getInstance().getCrystalKey(), PersistentDataType.STRING);
        if (rawId == null) {
            player.sendMessage(ChatColor.RED + "This item is not a minted crystal.");
            return true;
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawId);
        } catch (IllegalArgumentException ex) {
            player.sendMessage(ChatColor.RED + "This crystal has an invalid serial number.");
            return true;
        }

        CrystalLedger ledger = AmethystControl.getInstance().getLedger();
        if (!ledger.exists(uuid)) {
            player.sendMessage(ChatColor.RED + "This crystal is not present in the ledger.");
            return true;
        }

        if (!ledger.close(uuid)) {
            player.sendMessage(ChatColor.RED + "This crystal has already been redeemed.");
            return true;
        }

        player.getInventory().setItemInMainHand(null);
        player.sendMessage(ChatColor.GREEN + "Crystal " + ChatColor.WHITE + uuid + ChatColor.GREEN + " has been redeemed.");
        return true;
    }
}
