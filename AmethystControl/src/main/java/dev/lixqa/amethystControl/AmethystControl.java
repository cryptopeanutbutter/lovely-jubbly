/*
 * Plugin made by Lixqa Development.
 * Do not share the source code
 * Website: https://lix.qa/
 * Discord: https://discord.gg/ldev
 * */

package dev.lixqa.amethystControl;

import dev.lixqa.amethystControl.commands.RedeemCommand;
import dev.lixqa.amethystControl.commands.SpawnCrystalsCommand;
import dev.lixqa.amethystControl.commands.SupplyCommand;
import dev.lixqa.amethystControl.ledger.CrystalLedger;
import dev.lixqa.amethystControl.listeners.FortuneListener;
import dev.lixqa.amethystControl.listeners.GrowthListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class AmethystControl extends JavaPlugin {
    private static AmethystControl instance;
    private CrystalLedger ledger;
    private NamespacedKey crystalKey;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        crystalKey = new NamespacedKey(this, "crystal_id");

        try {
            ledger = new CrystalLedger(this);
        } catch (Exception ex) {
            getLogger().severe("Failed to initialise crystal ledger: " + ex.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new FortuneListener(), this);
        getServer().getPluginManager().registerEvents(new GrowthListener(), this);
        getCommand("spawncrystals").setExecutor(new SpawnCrystalsCommand());
        getCommand("supply").setExecutor(new SupplyCommand());
        getCommand("redeem").setExecutor(new RedeemCommand());
    }

    @Override
    public void onDisable() {
        if (ledger != null) {
            ledger.close();
        }
    }

    public static AmethystControl getInstance() {
        return instance;
    }

    public CrystalLedger getLedger() {
        return ledger;
    }

    public NamespacedKey getCrystalKey() {
        return crystalKey;
    }

    public ItemStack mintCrystal() {
        ItemStack stack = new ItemStack(Material.AMETHYST_SHARD, 1);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return stack;
        }

        String id = ledger != null ? ledger.recordMint() : null;
        if (id == null) {
            id = UUID.randomUUID().toString();
        }

        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Amethyst Crystal");
        meta.setLore(java.util.List.of(
                ChatColor.DARK_PURPLE + "Ledger Serial:",
                ChatColor.GRAY + id
        ));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(crystalKey, PersistentDataType.STRING, id);
        stack.setItemMeta(meta);
        return stack;
    }
}
