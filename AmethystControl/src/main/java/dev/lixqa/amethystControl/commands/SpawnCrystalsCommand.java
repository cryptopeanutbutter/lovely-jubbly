/*
 * Plugin made by Lixqa Development.
 * Do not share the source code
 * Website: https://lix.qa/
 * Discord: https://discord.gg/ldev
 * */

package dev.lixqa.amethystControl.commands;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.command.*;
import dev.lixqa.amethystControl.AreaManager;

import java.io.IOException;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SpawnCrystalsCommand implements CommandExecutor {

    private static final Logger logger = Logger.getLogger("AmethystSpawnLogger");

    static {
        try {
            FileHandler fh = new FileHandler("plugins/AmethystControl/amethyst_spawns.log", true);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class PlacementTarget {
        Block block;
        BlockFace direction;

        PlacementTarget(Block block, BlockFace direction) {
            this.block = block;
            this.direction = direction;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        List<AreaManager.Area> areas = AreaManager.loadAreas();
        List<String> failures = new ArrayList<>();

        for (AreaManager.Area area : areas) {
            Set<Chunk> chunks = AreaManager.getChunksFromKeys(area.chunkKeys());
            int amethysts = 0;
            List<PlacementTarget> potential = new ArrayList<>();

            if (chunks.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Area [" + area.id() + "] has no loaded chunks.");
                continue;
            }

            World world = chunks.iterator().next().getWorld();

            // Count existing amethysts
            for (Chunk chunk : chunks) {
                chunk.load();
                int minY = world.getMinHeight();
                int maxY = world.getMaxHeight();

                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = minY; y < maxY; y++) {
                            Block block = chunk.getBlock(x, y, z);
                            if (block.getType() == Material.AMETHYST_CLUSTER) {
                                amethysts++;
                            }
                        }
                    }
                }
            }

            sender.sendMessage(ChatColor.YELLOW + "Area [" + area.id() + "] currently has " + amethysts + " amethyst crystals (max " + area.crystals() + ").");

            /*if (amethysts >= area.crystals()) {
                sender.sendMessage(ChatColor.GRAY + "No new crystals spawned because max limit reached.");
                continue;
            }*/

            for (Chunk chunk : chunks) {
                int minY = world.getMinHeight();
                int maxY = world.getMaxHeight();

                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = minY; y < maxY; y++) {
                            Block block = chunk.getBlock(x, y, z);
                            if (block.getType() == Material.BUDDING_AMETHYST) {
                                for (BlockFace face : Arrays.asList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.DOWN)) {
                                    Block relative = block.getRelative(face);
                                    if (relative.getType() == Material.AIR) {
                                        potential.add(new PlacementTarget(relative, face));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            sender.sendMessage(ChatColor.YELLOW + "Found " + potential.size() + " potential crystal placement spots.");

            if (potential.isEmpty()) {
                failures.add(ChatColor.RED + "Area [" + area.id() + "] has no available spots to spawn crystals.");
                continue;
            }

            Collections.shuffle(potential);
            int crystalsToSpawn = area.crystals() - amethysts;

            if (crystalsToSpawn <= 0) {
                sender.sendMessage(ChatColor.GRAY + "No new crystals spawned because max limit reached.");
                continue;
            }

            if (potential.size() > crystalsToSpawn) {
                potential = new ArrayList<>(potential.subList(0, crystalsToSpawn));
            }

            for (PlacementTarget target : potential) {
                Block block = target.block;
                BlockFace direction = target.direction;

                block.setType(Material.AMETHYST_CLUSTER);
                BlockData data = Bukkit.createBlockData(Material.AMETHYST_CLUSTER);

                if (data instanceof Directional directional) {
                    directional.setFacing(direction);
                    block.setBlockData(directional);
                }

                block.getWorld().playSound(block.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, 1, 1);

                logger.info(String.format("Spawned amethyst crystal at %s in world '%s', facing %s, area ID: %s",
                        block.getLocation().toVector().toString(),
                        block.getWorld().getName(),
                        direction.name(),
                        area.id()));
            }

            sender.sendMessage(ChatColor.GREEN + "Spawned " + potential.size() + " amethyst crystals in area [" + area.id() + "].");
        }

        if (!failures.isEmpty()) {
            failures.forEach(sender::sendMessage);
        } else {
            sender.sendMessage(ChatColor.GREEN + "All areas processed successfully.");
        }

        return true;
    }
}
