package me.ahtism.multiblocker.handlers;

import me.ahtism.multiblocker.Multiblocker;
import me.ahtism.multiblocker.StructureSettings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

public class StructureHandler implements Listener {
    private YamlConfiguration config;

    public StructureHandler(Multiblocker plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        config = StructureSettings.getConfig();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        for (String structure : config.getStringList("structure_names")) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && event.getItem().getType() == Material.getMaterial(String.valueOf(config.get("structures." + structure + ".activator_item")))) {
                Block block = event.getClickedBlock();
                assert block != null;

                int bX = block.getX();
                int bY = block.getY();
                int bZ = block.getZ();

                if (block.getType() == Material.getMaterial(String.valueOf(config.get("structures." + structure + ".blocks." + (Integer.parseInt(String.valueOf(config.get("structures." + structure + ".activator_block")).split(",")[1])) + "." + config.get("structures." + structure + ".activator_block"))))) {
                    List<Block> blocks = new ArrayList<>();
                    int ax = Integer.parseInt(config.getString("structures." + structure + ".activator_block").split(",")[0]);
                    int ay = Integer.parseInt(config.getString("structures." + structure + ".activator_block").split(",")[1]);
                    int az = Integer.parseInt(config.getString("structures." + structure + ".activator_block").split(",")[2]);

                    for (int l = 0; l < config.getInt("structures." + structure + ".height"); l++) {
                        for (int x = 0; x < config.getInt("structures." + structure + ".widthx"); x++) {
                            for (int z = 0; z < config.getInt("structures." + structure + ".widthz"); z++) {
                                int xO = config.getInt("structures." + structure + ".widthx") + ax;
                                int yO = config.getInt("structures." + structure + ".height") + ay;
                                int zO = config.getInt("structures." + structure + ".widthz") + az;
                                blocks.add(new Location(block.getWorld(), bX + config.getInt("structures." + structure + ".widthx") + x - xO, bY + config.getInt("structures." + structure + ".height") + l - yO, bZ + config.getInt("structures." + structure + ".widthz") + z - zO).getBlock());
                            }
                        }
                    }

                    if (isStructureValid(structure, blocks)) {
                        List<String> commands = config.getStringList("structures." + structure + ".activation_task");

                        for (String command : commands) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + block.getX() + " " + block.getY() + " " + block.getZ() + " run " + command);
                        }
                    }
                }
            }
        }
    }

    public boolean isStructureValid(String structure, List<Block> blocks) {
        int maxY = -64;

        for (Block block : blocks) {
            maxY = Math.max(maxY, block.getY());
        }

        int bi = 0;
        for (int l = 0; l < config.getInt("structures." + structure + ".height"); l++) {
            for (int x = 0; x < config.getInt("structures." + structure + ".widthx"); x++) {
                for (int z = 0; z < config.getInt("structures." + structure + ".widthz"); z++) {
                    Material blockType = blocks.get(bi).getType();
                    Material targetMaterial = Material.getMaterial(String.valueOf(config.get("structures." + structure + ".blocks." + l + "." + x + "," + l + "," + z)));

                    if (blockType != targetMaterial && !targetMaterial.isAir()) {
                        Bukkit.getLogger().info(blocks.get(bi).getLocation().toString());
                        Bukkit.getLogger().info(blockType + " | " + targetMaterial + " | " + x + "," + l + "," + z);
                        return false;
                    }

                    bi++;
                }
            }
        }

        return true;
    }
}
