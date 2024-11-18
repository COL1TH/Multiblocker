package me.ahtism.multiblocker.handlers;

import me.ahtism.multiblocker.Multiblocker;
import me.ahtism.multiblocker.StructureFile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class StructureHandler implements Listener {
    public StructureHandler(Multiblocker plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        World world = event.getPlayer().getWorld();

        if (event.getItem() != null) {
            if (event.getItem().getType() == Material.BUNDLE && !event.getItem().getEnchantments().isEmpty()) {
                String[] lore = new String[3];

                ItemMeta meta = event.getItem().getItemMeta();
                for (int i = 0; i < meta.lore().size(); i++) {
                    lore[i] = ((TextComponent) meta.lore().get(i)).content();
                }

                if (meta.lore().contains(Component.text("Structure: NONE"))) {
                    if (event.getPlayer().getInventory().getItemInOffHand().getType() == Material.PAPER && event.getPlayer().getInventory().getItemInOffHand().getItemMeta().hasDisplayName()) {
                        String name = ((TextComponent) event.getPlayer().getInventory().getItemInOffHand().getItemMeta().displayName()).content();
                        String corner1 = ((TextComponent) (meta.lore().get(1))).content();
                        String corner2 = ((TextComponent) (meta.lore().get(2))).content();

                        if (!corner1.contains("NONE") && !corner2.contains("NONE")) {
                            // save new structure

                            int x1 = Integer.parseInt(corner1.split(" / ")[0].split("Corner 1: ")[1]);
                            int y1 = Integer.parseInt(corner1.split(" / ")[1]);
                            int z1 = Integer.parseInt(corner1.split(" / ")[2]);

                            int x2 = Integer.parseInt(corner2.split(" / ")[0].split("Corner 2: ")[1]);
                            int y2 = Integer.parseInt(corner2.split(" / ")[1]);
                            int z2 = Integer.parseInt(corner2.split(" / ")[2]);

                            meta.lore(List.of(Component.text("Structure: " + name + " (by " + event.getPlayer().getName() + ")")));

                            Map<Location, Block> blocks = new HashMap<>();

                            if (x2 - x1 <= 0) {
                                int old = x1;
                                x1 = x2;
                                x2 = old;
                            }

                            if (y2 - y1 <= 0) {
                                int old = y1;
                                y1 = y2;
                                y2 = old;
                            }

                            if (z2 - z1 <= 0) {
                                int old = z1;
                                z1 = z2;
                                z2 = old;
                            }

                            for (int level : event.getItem().getEnchantments().values()) {
                                if ((x2 - x1) * (y2 - y1) * (z2 - z1) > Math.pow(Math.pow(level, 2), 3)) {
                                    event.getPlayer().sendMessage(Component.text("Your structure is too big!").color(TextColor.color(150, 0, 0)));
                                    return;
                                }
                            }

                            for (int y = y1; y <= y2; y++) {
                                for (int z = z1; z <= z2; z++) {
                                    for (int x = x1; x <= x2; x++) {
                                        int X = (x2 - x1) - (x2 - x);
                                        int Y = (y2 - y1) - (y2 - y);
                                        int Z = (z2 - z1) - (z2 - z);

                                        Location location = new Location(world, x, y, z);
                                        Location relativeLocation = new Location(world, X, Y, Z);
                                        Block block = location.getBlock();

                                        if (block.getType() != Material.AIR) {
                                            blocks.put(relativeLocation, block);
                                        }
                                    }
                                }
                            }

                            StructureFile file = new StructureFile(event.getPlayer().getName() + " - " + name, false);
                            file.saveBlocks(blocks);
                            blocks.values().forEach(block -> block.setType(Material.AIR));

                            event.getPlayer().getInventory().getItemInOffHand().setAmount(event.getPlayer().getInventory().getItemInOffHand().getAmount() - 1);
                        } else {
                            // load existing structure

//                            if (name.split(" - ").length > 1) {
//                                meta.lore(List.of(Component.text("Structure: " + name.split(" - ")[1] + " (by " + name.split(" - ")[0] + ")")));
//                                event.getPlayer().getInventory().getItemInOffHand().setAmount(event.getPlayer().getInventory().getItemInOffHand().getAmount() - 1);
//                            }
                        }
                    } else {
                        // save coordinates of structure

                        String[] previousLore = lore;

                        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            Location loc = event.getClickedBlock().getLocation();

                            if (!event.getPlayer().isSneaking()) {
                                lore[1] = "Corner 1: " + loc.blockX() + " / " + loc.blockY() + " / " + loc.blockZ();
                                lore[2] = previousLore[2];
                            } else {
                                lore[1] = previousLore[1];
                                lore[2] = "Corner 2: " + loc.blockX() + " / " + loc.blockY() + " / " + loc.blockZ();
                            }
                        } else if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                            if (!event.getPlayer().isSneaking()) {
                                lore[1] = "Corner 1: NONE";
                                lore[2] = previousLore[2];
                            } else {
                                lore[1] = previousLore[1];
                                lore[2] = "Corner 2: NONE";
                            }
                        }

                        meta.lore(List.of(Component.text(lore[0]), Component.text(lore[1]), Component.text(lore[2])));
                    }

                    event.getItem().setItemMeta(meta);
                } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    // place structure

                    meta.lore(List.of(Component.text("Structure: NONE"), Component.text("Corner 1: NONE"), Component.text("Corner 2: NONE")));
                    event.getItem().setItemMeta(meta);

                    String structureName = lore[0].split(" \\(by ")[1].split("\\)")[0] + " - " + lore[0].split("Structure: ")[1].split(" \\(by ")[0];
                    StructureFile file = new StructureFile(structureName, false);
                    Map<Location, String> blocks = file.parseBlocks(world);

                    Block block = event.getClickedBlock();
                    assert block != null;

                    int bx = block.getX();
                    int by = block.getY();
                    int bz = block.getZ();

                    List<Integer> yValues = new ArrayList<>();
                    List<Integer> xValues = new ArrayList<>();
                    List<Integer> zValues = new ArrayList<>();

                    for (int i = 0; i < blocks.size(); i++) {
                        Location location = (Location) blocks.keySet().toArray()[i];

                        yValues.add(location.blockY());
                        xValues.add(location.blockX());
                        zValues.add(location.blockZ());
                    }

                    int height = Collections.max(yValues) - Collections.min(yValues);
                    int widthx = Collections.max(xValues) - Collections.min(xValues);
                    int widthz = Collections.max(zValues) - Collections.min(zValues);

                    for (int y = 0; y <= height; y++) {
                        for (int x = 0; x <= widthx; x++) {
                            for (int z = 0; z <= widthz; z++) {
                                if (blocks.get(new Location(world, x, y, z)) != null) {
                                    String command = "execute positioned " + bx + " " + by + " " + bz + " run setblock ~" + x + " ~" + y + " ~" + z + " " + blocks.get(new Location(world, x, y, z)).split("\\|")[1] + " destroy";
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                                }
                            }
                        }
                    }

                    try {
                        Files.deleteIfExists(Path.of(Multiblocker.getInstance().getDataFolder().getPath() + "/structures/" + structureName + ".multiblock"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            File file = new File(Multiblocker.getInstance().getDataFolder(), "structures/triggerable/");
            if (file.exists()) {
                for (String structure : Objects.requireNonNull(file.list())) {
                    boolean isValid = true;

                    StructureFile structureFile = new StructureFile(structure.split(".multiblock")[0], true);
                    Map<String, Object> activationData = structureFile.parseActivationData();

                    Map<Location, String> blocks = structureFile.parseBlocks(world);

                    String aCoords = String.valueOf(activationData.get("B"));
                    Location relativeActivationLocation = new Location(world, Integer.parseInt(aCoords.split("x")[1].split("y")[0]), Integer.parseInt(aCoords.split("y")[1].split("z")[0]), Integer.parseInt(aCoords.split("z")[1].split(";")[0]));

                    Block block = event.getClickedBlock();

                    if (block != null && event.getItem() != null && activationData.get("I") == event.getItem().getType() && Material.getMaterial(blocks.get(relativeActivationLocation).split("\\|")[0]) == block.getType()) {
                        int bx = block.getX();
                        int by = block.getY();
                        int bz = block.getZ();

                        List<Integer> yValues = new ArrayList<>();
                        List<Integer> xValues = new ArrayList<>();
                        List<Integer> zValues = new ArrayList<>();

                        for (int i = 0; i < blocks.size(); i++) {
                            Location location = (Location) blocks.keySet().toArray()[i];

                            yValues.add(location.blockY());
                            xValues.add(location.blockX());
                            zValues.add(location.blockZ());
                        }

                        int height = Collections.max(yValues) - Collections.min(yValues);
                        int widthx = Collections.max(xValues) - Collections.min(xValues);
                        int widthz = Collections.max(zValues) - Collections.min(zValues);

                        int ax = relativeActivationLocation.blockX();
                        int ay = relativeActivationLocation.blockY();
                        int az = relativeActivationLocation.blockZ();

                        for (int y = 0; y <= height; y++) {
                            for (int x = 0; x <= widthx; x++) {
                                for (int z = 0; z <= widthz; z++) {
                                    if (blocks.get(new Location(world, x, y, z)) != null) {
                                        Location locationToCheck = new Location(world, bx + x - ax, by + y - ay, bz + z - az);
                                        if (locationToCheck.getBlock().getType() != Material.getMaterial(blocks.get(new Location(world, x, y, z)).split("\\|")[0])) {
                                            isValid = false;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        isValid = false;
                    }

                    if (isValid) {
                        List<String> commands = new ArrayList<>();

                        int ch;
                        StringBuilder line = new StringBuilder();
                        FileReader fr = null;

                        try {
                            fr = new FileReader(new File(Multiblocker.getInstance().getDataFolder(), "commands/" + structure.replace(".multiblock", ".txt")));
                        } catch (FileNotFoundException e) {
                            Bukkit.getLogger().info("File not found: " + e.getMessage());
                        }

                        try {
                            while ((ch = fr.read()) != -1) {
                                if ((char) ch != ';') {
                                    line.append((char) ch);
                                } else {
                                    commands.add(line.toString());
                                    line = new StringBuilder();
                                }
                            }

                            fr.close();
                        } catch (Exception e) {
                            Bukkit.getLogger().info("Parsing failed: " + e.getMessage());
                        }

                        event.getPlayer().getInventory().getItemInMainHand().setAmount(event.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
                        commands.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + block.getX() + " " + block.getY() + " " + block.getZ() + " run " + command.replace("@s", event.getPlayer().getName())));
                    }
                }
            }
        }
    }
}
