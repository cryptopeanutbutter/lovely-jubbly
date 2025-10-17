/*
 * Plugin made by Lixqa Development.
 * Do not share the source code
 * Website: https://lix.qa/
 * Discord: https://discord.gg/ldev
 * */

package dev.lixqa.amethystControl;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import java.util.*;

public class AreaManager {
    public record Area(String id, int crystals, List<String> chunkKeys) {}

    public static List<Area> loadAreas() {
        List<Area> areas = new ArrayList<>();

        List<Map<?, ?>> rawAreas = AmethystControl.getInstance().getConfig().getMapList("areas");

        for (Map<?, ?> rawArea : rawAreas) {
            String id = (String) rawArea.get("id");
            int crystals = (int) rawArea.get("crystals");
            List<String> chunks = (List<String>) rawArea.get("chunks");
            areas.add(new Area(id, crystals, chunks));
        }

        return areas;
    }

    public static Set<Chunk> getChunksFromKeys(List<String> keys) {
        Set<Chunk> chunks = new HashSet<>();
        for (String key : keys) {
            String[] parts = key.split(",");
            int x = Integer.parseInt(parts[0]);
            int z = Integer.parseInt(parts[1]);
            chunks.add(Bukkit.getWorlds().get(0).getChunkAt(x, z));
        }
        return chunks;
    }
}
