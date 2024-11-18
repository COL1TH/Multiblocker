package me.ahtism.multiblocker;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class StructureItem extends ItemStack {
    public StructureItem() {
        super(Material.BUNDLE);
    }

    public void prepare() {
        addEnchantment(Objects.requireNonNull(Enchantment.getByKey(NamespacedKey.fromString("multiblocker:architects_blessing"))), 1);
        lore(List.of(Component.text("Structure: NONE"), Component.text("Corner 1: NONE"), Component.text("Corner 2: NONE")));
    }
}
