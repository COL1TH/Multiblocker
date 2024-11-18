package me.ahtism.multiblocker;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class StructureFile {
    private File file;

    public StructureFile(String name, boolean triggerable) {
        String path;

        if (triggerable) {
            path = "structures/triggerable/" + name + ".multiblock";
            new File(Multiblocker.getInstance().getDataFolder(), "structures/triggerable/").mkdirs();
        } else {
            path = "structures/" + name + ".multiblock";
            new File(Multiblocker.getInstance().getDataFolder(), "structures/").mkdirs();
        }

        file = new File(Multiblocker.getInstance().getDataFolder(), path);

        try {
            file.createNewFile();
        } catch (IOException e) {
            Bukkit.getLogger().info("Unable to create new structure file: " + e.getMessage());
        }
    }

    public HashMap<String, Object> parseActivationData() {
        HashMap<String, Object> result = new HashMap<>();

        int ch;
        StringBuilder line = new StringBuilder();
        FileReader fr = null;

        try {
            fr = new FileReader(file);
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().info("File not found: " + e.getMessage());
        }

        try {
            while ((ch = fr.read()) != -1) {
                if ((char) ch != ';') {
                    line.append((char) ch);
                } else {
                    if (line.toString().contains("#I") && !result.containsKey("I")) {
                        Material i = Material.valueOf(line.toString().split("#I:")[1]);
                        result.put("I", i);
                    }

                    if (line.toString().contains("#B") && !result.containsKey("B")) {
                        String b = line.toString().split("#B:")[1];
                        result.put("B", b);
                    }

                    line = new StringBuilder();
                }
            }



            fr.close();
        } catch (Exception e) {
            Bukkit.getLogger().info("Parsing failed: " + e.getMessage());
        }

        return result;
    }

    public Map<Location, String> parseBlocks(World world) {
        Map<Location, String> blocks = new HashMap<>();

        int ch;
        StringBuilder line = new StringBuilder();
        FileReader fr = null;

        try {
            fr = new FileReader(file);
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().info("File not found: " + e.getMessage());
        }

        try {
            while ((ch = fr.read()) != -1) {
                if ((char) ch != ';') {
                    line.append((char) ch);
                } else {
                    if (!line.toString().contains("#")) {
                        String rawLocation = line.toString().split("-")[0];
                        Location location = new Location(world, Integer.parseInt(rawLocation.split("x")[1].split("y")[0]), Integer.parseInt(rawLocation.split("y")[1].split("z")[0]), Integer.parseInt(rawLocation.split("z")[1].split(";")[0]));
                        String block = line.toString().split("-")[1];

                        blocks.put(location, block);
                    }

                    line = new StringBuilder();
                }
            }

            fr.close();
        } catch (Exception e) {
            Bukkit.getLogger().info("Parsing failed: " + e.getMessage());
        }

        return blocks;
    }

    public void saveBlocks(Map<Location, Block> blocks, Material activatorItem, Location activatorBlock) {
        FileWriter fw;
        String string = "#I:" + activatorItem + ";\n#B:x" + activatorBlock.blockX() + "y" + activatorBlock.blockY() + "z" + activatorBlock.blockZ() + ";\n";

        try {
            fw = new FileWriter(file);
            fw.append(string);

            for (Location location : blocks.keySet()) {
                BlockData data = blocks.get(location).getBlockData();
                String bData = data.getAsString();
                fw.append("x").append(String.valueOf(location.blockX())).append("y").append(String.valueOf(location.blockY())).append("z").append(String.valueOf(location.blockZ())).append("-").append(String.valueOf(blocks.get(location).getType())).append("|").append(bData).append(";\n");
            }

            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveBlocks(Map<Location, Block> blocks) {
        FileWriter fw;

        try {
            fw = new FileWriter(file);

            for (Location location : blocks.keySet()) {
                BlockData data = blocks.get(location).getBlockData();
                String bData = data.getAsString();
                fw.append("x").append(String.valueOf(location.blockX())).append("y").append(String.valueOf(location.blockY())).append("z").append(String.valueOf(location.blockZ())).append("-").append(String.valueOf(blocks.get(location).getType())).append("|").append(bData).append(";\n");
            }

            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
