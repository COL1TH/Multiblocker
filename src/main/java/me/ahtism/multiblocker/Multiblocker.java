package me.ahtism.multiblocker;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.ahtism.multiblocker.handlers.StructureHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;

public final class Multiblocker extends JavaPlugin {
    @Override
    public void onEnable() {
        new StructureHandler(this);

        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(
                    literal("multiblocker")
                            .then(literal("command")
                                    .then(literal("set")
                                            .then(argument("structure", StringArgumentType.string())
                                                    .then(argument("command", StringArgumentType.greedyString())
                                                            .executes(ctx -> {
                                                                if (!ctx.getSource().getSender().hasPermission("multiblocker.commands")) {
                                                                    ctx.getSource().getSender().sendMessage(Component.text("You don't have permission to execute this command!").color(TextColor.color(150, 0, 0)));
                                                                    return Command.SINGLE_SUCCESS;
                                                                }

                                                                String structure = StringArgumentType.getString(ctx, "structure");
                                                                String command = StringArgumentType.getString(ctx, "command");

                                                                if (Files.exists(new File(Multiblocker.getInstance().getDataFolder(), "structures/triggerable/" + structure + ".multiblock").toPath())) {
                                                                    File file = new File(Multiblocker.getInstance().getDataFolder(), "commands/" + structure + ".txt");
                                                                    FileWriter fw;
                                                                    try {
                                                                        new File(Multiblocker.getInstance().getDataFolder(), "commands/").mkdirs();
                                                                        file.createNewFile();
                                                                        fw = new FileWriter(file);

                                                                        fw.append(command).append(";\n");

                                                                        fw.close();
                                                                    } catch (IOException e) {
                                                                        throw new RuntimeException(e);
                                                                    }
                                                                }

                                                                return Command.SINGLE_SUCCESS;
                                                            })))))
                            .then(literal("create-triggerable")
                                    .then(argument("name", StringArgumentType.greedyString())
                                            .executes(ctx -> {
                                                if (!ctx.getSource().getSender().hasPermission("multiblocker.commands")) {
                                                    ctx.getSource().getSender().sendMessage(Component.text("You don't have permission to execute this command!").color(TextColor.color(150, 0, 0)));
                                                    return Command.SINGLE_SUCCESS;
                                                }

                                                Player player = (Player) ctx.getSource().getSender();
                                                ItemStack item = player.getInventory().getItemInMainHand();
                                                Location looking = Objects.requireNonNull(player.getTargetBlockExact(5, FluidCollisionMode.NEVER)).getLocation();

                                                if (item.getType() == Material.BUNDLE && !item.getEnchantments().isEmpty()) {
                                                    ItemMeta meta = item.getItemMeta();
                                                    String corner1 = ((TextComponent) (Objects.requireNonNull(meta.lore()).get(1))).content();
                                                    String corner2 = ((TextComponent) (Objects.requireNonNull(meta.lore()).get(2))).content();

                                                    if (!corner1.contains("NONE") && !corner2.contains("NONE")) {
                                                        String name = StringArgumentType.getString(ctx, "name");

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

                                                        Location aPos = new Location(player.getWorld(), (x2 - x1) - (x2 - looking.blockX()), (y2 - y1) - (y2 - looking.blockY()), (z2 - z1) - (z2 - looking.blockZ()));

                                                        StructureFile file = new StructureFile(player.getName() + " - " + name, true);
                                                        file.saveBlocks(blocks, player.getInventory().getItemInOffHand().getType(), aPos);
                                                        blocks.values().forEach(block -> block.setType(Material.AIR));
                                                    }
                                                }

                                                return Command.SINGLE_SUCCESS;
                                            })))
                            .build(),

                    "Modify the functionality of a Multiblocker structure.",
                    List.of("")
            );
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Multiblocker getInstance() {
        return getPlugin(Multiblocker.class);
    }
}
