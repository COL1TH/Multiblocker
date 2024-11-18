package me.ahtism.multiblocker.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.ahtism.multiblocker.Multiblocker;
import me.ahtism.multiblocker.StructureFile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class StructureCommand implements BasicCommand, TabCompleter {
    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, String[] args) {
        if (args.length > 2) {
            if (args[0].equals("commands")) {
                new File(Multiblocker.getInstance().getDataFolder(), "commands").mkdirs();

                if (args[1].equals("add") && new File(Multiblocker.getInstance().getDataFolder(), args[2]).exists()) {
                    File file = new File("commands/" + args[2] + ".txt");
                    FileWriter fw;
                    try {
                        file.createNewFile();
                        fw = new FileWriter(file);

                        for (String arg : args) {
                            if (Arrays.stream(args).toList().indexOf(arg) > 2) {
                                fw.append(arg).append(" ");
                            }
                        }

                        fw.append(";\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (args[1].equals("removeall") && new File(Multiblocker.getInstance().getDataFolder(), args[2]).exists()) {
                    new File("commands/" + args[2] + ".txt").delete();
                }
            }

            if (args[0].equals("create-triggerable")) {
                Player player = (Player) commandSourceStack.getSender();
                ItemStack item = player.getActiveItem();
                if (item.getType() == Material.BUNDLE && !item.getEnchantments().isEmpty()) {
                    ItemMeta meta = item.getItemMeta();
                    String corner1 = ((TextComponent) (Objects.requireNonNull(meta.lore()).get(1))).content();
                    String corner2 = ((TextComponent) (Objects.requireNonNull(meta.lore()).get(2))).content();

                    if (!corner1.contains("NONE") && !corner2.contains("NONE")) {
                        String name = args[1];

                        int x1 = Integer.parseInt(corner1.split(" / ")[0].split("Corner 1: ")[1]);
                        int y1 = Integer.parseInt(corner1.split(" / ")[1]);
                        int z1 = Integer.parseInt(corner1.split(" / ")[2]);

                        int x2 = Integer.parseInt(corner2.split(" / ")[0].split("Corner 2: ")[1]);
                        int y2 = Integer.parseInt(corner2.split(" / ")[1]);
                        int z2 = Integer.parseInt(corner2.split(" / ")[2]);

                        meta.lore(List.of(Component.text("Structure: " + name + " (by " + player.getName() + ")")));

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

                        for (int y = y1; y <= y2; y++) {
                            for (int z = z1; z <= z2; z++) {
                                for (int x = x1; x <= x2; x++) {
                                    int X = (x2 - x1) - (x2 - x);
                                    int Y = (y2 - y1) - (y2 - y);
                                    int Z = (z2 - z1) - (z2 - z);

                                    Location location = new Location(player.getWorld(), x, y, z);
                                    Location relativeLocation = new Location(player.getWorld(), X, Y, Z);
                                    Block block = location.getBlock();

                                    if (block.getType() != Material.AIR) {
                                        blocks.put(relativeLocation, block);
                                    }
                                }
                            }
                        }

                        StructureFile file = new StructureFile(player.getName() + " - " + name, true);
                        file.saveBlocks(blocks);
                        blocks.values().forEach(block -> block.setType(Material.AIR));
                    }
                }
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            return List.of("commands", "create-triggerable");
        }

        if (args.length == 1) {
            if (args[0].equals("commands")) return List.of("add", "removeall");
            if (args[0].equals("create-triggerable")) return List.of("");
        }

        if (args.length == 2) {
            return Arrays.stream(Objects.requireNonNull(new File(Multiblocker.getInstance().getDataFolder(), "structures/triggerable").list())).map(file -> file.split(".multiblock")[0]).toList();
        }

        return List.of();
    }
}
