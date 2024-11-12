package me.ahtism.multiblocker.commands;

import me.ahtism.multiblocker.StructureSettings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SaveCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 11) {
            final String name = args[0];
            final String activatorItem = args[1];
            final int ax = Integer.parseInt(args[2]);
            final int ay = Integer.parseInt(args[3]);
            final int az = Integer.parseInt(args[4]);
            final int x1 = Integer.parseInt(args[5]);
            final int y1 = Integer.parseInt(args[6]);
            final int z1 = Integer.parseInt(args[7]);
            final int x2 = Integer.parseInt(args[8]) + 1;
            final int y2 = Integer.parseInt(args[9]) + 1;
            final int z2 = Integer.parseInt(args[10]) + 1;

            StructureSettings config = StructureSettings.getInstance();
            List<String> existingNames = StructureSettings.getConfig().getStringList("structure_names");

            if (existingNames.contains(name)) {
                config.set("structures." + name, null);
            }

            config.set("structures." + name + ".activation_task", List.of(new String[]{"tell " + commandSender.getName() + " Structure '" + name + "' has been activated!"}));
            config.set("structures." + name + ".widthx", x2 - x1);
            config.set("structures." + name + ".widthz", z2 - z1);
            config.set("structures." + name + ".height", y2 - y1);
            config.set("structures." + name + ".activator_item", activatorItem);
            config.set("structures." + name + ".activator_block", (ax - x1) + "," + (ay - y1) + "," + (az - z1));

            for (int y = y1; y < y2; y++) {
                for (int z = z1; z < z2; z++) {
                    for (int x = x1; x < x2; x++) {
                        int X = (x2 - x1) - (x2 - x);
                        int Y = (y2 - y1) - (y2 - y);
                        int Z = (z2 - z1) - (z2 - z);
                        config.set("structures." + name + ".blocks." + Y + "." + X + "," + Y + "," + Z, String.valueOf(new Location(((Player) commandSender).getWorld(), x1 + X, y1 + Y, z1 + Z).getBlock().getType()));
                    }
                }
            }

            if (!existingNames.contains(name)) {
                existingNames.add(name);
                config.set("structure_names", existingNames);
            }

            return true;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>();

            for (String name : StructureSettings.getConfig().getStringList("structure_names")) {
                if (name.startsWith(args[0])) {
                    list.add(name);
                }
            }

            return list;
        }

        if (args.length == 2) {
            List<String> list = new ArrayList<>();

            for (Material material : Material.values()) {
                if (material.name().startsWith(args[1])) {
                    list.add(material.name());
                }
            }

            return list;
        }

        if (args.length == 3) return List.of("<ax>");
        if (args.length == 4) return List.of("<ay>");
        if (args.length == 5) return List.of("<az>");

        if (args.length == 6) return List.of("<x1>");
        if (args.length == 7) return List.of("<y1>");
        if (args.length == 8) return List.of("<z1>");

        if (args.length == 9) return List.of("<x2>");
        if (args.length == 10) return List.of("<y2>");
        if (args.length == 11) return List.of("<z2>");

        return List.of();
    }
}
