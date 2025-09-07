package org.turbojax;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import org.turbojax.locks.ItemLock;
import org.turbojax.locks.Lock;

public final class ItemLocker extends JavaPlugin {
    private static ItemLocker instance;

    public static ItemLocker getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Getting the instance for getInstance() calls
        instance = this;

        // Registering the locks
        Lock.findImplementations().forEach(ConfigurationSerialization::registerClass);

        // Saving the config
        saveResource("config.yml", false);

        // Loading the config
        LockManager.loadLocks();

        Bukkit.getPluginManager().registerEvents(new ItemLock(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        // Unregistering the locks
        Lock.findImplementations().forEach(ConfigurationSerialization::registerClass);

        // Saving to the config
        LockManager.saveLocks();
    }
}