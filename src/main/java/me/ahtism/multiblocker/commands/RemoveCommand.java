package me.ahtism.multiblocker.commands;

import me.ahtism.multiblocker.StructureSettings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RemoveCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            StructureSettings.getInstance().set("structures." + args[0], null);

            List<String> existingStructures = StructureSettings.getConfig().getStringList("structure_names");
            existingStructures.remove(args[0]);
            StructureSettings.getInstance().set("structure_names", existingStructures);

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

        return List.of();
    }
}