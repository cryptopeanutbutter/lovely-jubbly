package dev.lixqa.amethystControl.commands;

import dev.lixqa.amethystControl.AmethystControl;
import dev.lixqa.amethystControl.ledger.CrystalLedger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SupplyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CrystalLedger ledger = AmethystControl.getInstance().getLedger();
        int minted = ledger.countMinted();
        int open = ledger.countOpen();

        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Minted crystals in ledger: " + ChatColor.WHITE + minted);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Active circulation: " + ChatColor.WHITE + open);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Redeemed: " + ChatColor.WHITE + (minted - open));
        return true;
    }
}
