package org.turbojax;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.turbojax.locks.EnchantmentLock;
import org.turbojax.locks.ItemLock;
import org.turbojax.locks.Lock;
import org.turbojax.locks.PotionLock;

import java.util.ArrayList;
import java.util.List;

public final class ItemLocker extends JavaPlugin {
    private static ItemLocker instance;

    private List<Lock> registeredLocks = new ArrayList<>();

    public static ItemLocker getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Getting the instance for getInstance() calls
        instance = this;

        // Registering the locks
        registerLock(new EnchantmentLock());
        registerLock(new ItemLock());
        registerLock(new PotionLock());

        // Loading the locks
        getSLF4JLogger().info("Loading Locks");
        LockManager.loadLocks();
        getSLF4JLogger().info("Loaded Locks");

        // Adding locks
        LockManager.addLock(new ItemLock(ItemType.MACE, 1));
        LockManager.addLock(new PotionLock(PotionEffectType.ABSORPTION, "te", 1, 2));
        LockManager.addLock(new EnchantmentLock(Enchantment.BREACH, 3));
        LockManager.addLock(new EnchantmentLock(Enchantment.DENSITY, 4));

        // Saving the config
        saveResource("config.yml", false);

        // Loading the config
        LockManager.loadLocks();

        Bukkit.getPluginManager().registerEvents(new ItemLock(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        // Saving to the config
        LockManager.saveLocks();

        // Unregistering the locks
        registeredLocks.forEach(lock -> ConfigurationSerialization.unregisterClass(lock.getClass()));
    }

    /**
     * Registers a new lock with the plugin.
     *
     * @param lock The lock class to register.
     */
    public void registerLock(Lock lock) {
        Bukkit.getPluginManager().registerEvents(lock, ItemLocker.getInstance());
        ConfigurationSerialization.registerClass(lock.getClass());
        registeredLocks.add(lock);
    }

    /**
     * Unregisters a lock.
     *
     * @param lock The lock class to unregister.
     */
    public void unregisterLock(Lock lock) {
        // Ignoring locks that are not registered.
        if (!registeredLocks.contains(lock)) return;

        //
        HandlerList.unregisterAll(lock);
        ConfigurationSerialization.unregisterClass(lock.getClass());
    }
}