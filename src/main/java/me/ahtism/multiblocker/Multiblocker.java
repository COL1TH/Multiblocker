package me.ahtism.multiblocker;

import me.ahtism.multiblocker.commands.AddCommand;
import me.ahtism.multiblocker.commands.RemoveCommand;
import me.ahtism.multiblocker.commands.ResetCommand;
import me.ahtism.multiblocker.commands.SaveCommand;
import me.ahtism.multiblocker.handlers.StructureHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class Multiblocker extends JavaPlugin {
    @Override
    public void onEnable() {
        StructureSettings.getInstance().load();
        new StructureHandler(this);
        getCommand("save-structure").setExecutor(new SaveCommand());
        getCommand("remove-structure").setExecutor(new RemoveCommand());
        getCommand("add-structure-task").setExecutor(new AddCommand());
        getCommand("reset-structure-tasks").setExecutor(new ResetCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Multiblocker getInstance() {
        return getPlugin(Multiblocker.class);
    }
}
