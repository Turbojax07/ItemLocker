package org.turbojax.locks;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.turbojax.ItemLocker;
import org.turbojax.LockManager;

public class EnchantmentLock implements Lock {
    public static Registry<Enchantment> enchantmentRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
    public Enchantment enchantment;
    public int level;

    public EnchantmentLock() {}

    public EnchantmentLock(Enchantment enchantment, int level) {
        this.enchantment = enchantment;
        this.level = level;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("enchantment", enchantment.getKey().toString());
        map.put("level", level);

        return map;
    }

    public static EnchantmentLock deserialize(Map<String, Object> map) {
        NamespacedKey enchantKey = NamespacedKey.fromString((String) map.get("enchantment"));

        if (enchantKey == null) {
            ItemLocker.getInstance().getSLF4JLogger().warn("Could not find the enchantment specified");
            return null;
        }

        Enchantment enchant = enchantmentRegistry.get(enchantKey);
        int level = (Integer) map.get("level");

        return new EnchantmentLock(enchant, level);
    }

    @Override
    public void loop() {}

    @Override
    public String getUsage() {
        return "EnchantmentLock <enchantment> <level>";
    }

    @Override
    public boolean onLock(CommandSender sender, Command command, String label, String[] args) {
        // Exiting if the minimum amount of arguments has not been reached.
        if (args.length < 3) return false;

        NamespacedKey key = NamespacedKey.fromString(args[2]);

        // Handling when the config does not have a valid key
        if (key == null) {
            // TODO: Log not valid key
            return false;
        }

        // Creating the lock
        EnchantmentLock lock = new EnchantmentLock(enchantmentRegistry.get(key), 0);

        // Handling when the key does not have an enchantment.
        if (lock.enchantment == null) {
            // TODO: Log no enchantment found
            return false;
        }

        // Determining the level of enchantment to limit the players to.
        if (args.length == 4) {
            lock.level = Integer.parseInt(args[3]);
        }

        // Applying the lock
        LockManager.addLock(lock);

        return false;
    }

    // TODO: Implement
    @Override
    public boolean onUnlock(CommandSender sender, Command command, String label, String[] args) {
        // Exiting if the minimum amount of arguments has not been reached.
        if (args.length < 3) return false;

        NamespacedKey key = NamespacedKey.fromString(args[2]);

        // Handling when the config does not have a valid key
        if (key == null) {
            // TODO: Log not valid key
            return false;
        }

        // Creating the lock
        EnchantmentLock lock = new EnchantmentLock(enchantmentRegistry.get(key), 0);

        // Handling when the key does not have an enchantment.
        if (lock.enchantment == null) {
            // TODO: Log no enchantment found
            return false;
        }

        // Determining the level of enchantment to limit the players to.
        if (args.length == 4) {
            lock.level = Integer.parseInt(args[3]);
        }

        // Applying the lock
        LockManager.removeLock(lock);

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        // First arg is the Enchantment
        if (args.length == 0) {
            return enchantmentRegistry.stream().map(Enchantment::getKey).map(NamespacedKey::toString).toList();
        }

        // Second arg is the level
        if (args.length == 1) {
            NamespacedKey key = NamespacedKey.fromString(args[0]);

            if (key == null) return List.of();
            if (enchantmentRegistry.get(key) == null) return List.of();

            List<String> suggestions = new ArrayList<>();
            for (int i = 1; i <= enchantmentRegistry.get(key).getMaxLevel(); i++) {
                suggestions.add(String.valueOf(i));
            }

            return suggestions;
        }

        return List.of();
    }

    // Event Handlers

    @EventHandler
    public void onEnchant(EnchantItemEvent enchantItemEvent) {
        List<EnchantmentLock> enchantLocks = LockManager.getLocks(EnchantmentLock.class);
        Map<Enchantment,Integer> toAdd = enchantItemEvent.getEnchantsToAdd();

        for (EnchantmentLock lock : enchantLocks) {
            // Ignoring if the enchantment is not being applied.
            if (!toAdd.containsKey(lock.enchantment)) continue;

            // Ignoring if the enchantment is within the acceptable range.
            if (toAdd.get(lock.enchantment) <= lock.level) continue;

            // Asserting the enchantment lock.
            toAdd.put(lock.enchantment, lock.level);
        }
    }

    // TODO: Check how this affects performance.  Constantly running this when a player moves stuff might be an expensive task.
    @EventHandler
    public void onTakeItem(InventoryMoveItemEvent inventoryMoveItemEvent) {
        List<EnchantmentLock> enchantLocks = LockManager.getLocks(EnchantmentLock.class);
        ItemStack item = inventoryMoveItemEvent.getItem();

        for (EnchantmentLock enchantLock : enchantLocks) {
            // Ignoring if the item does not have the enchantment.
            if (!item.containsEnchantment(enchantLock.enchantment)) continue;

            // Ignoring if the enchantment is within the acceptable range.
            if (item.getEnchantmentLevel(enchantLock.enchantment) <= enchantLock.level) continue;

            // Asserting the enchantment lock.
            item.addEnchantment(enchantLock.enchantment, enchantLock.level);
        }
    }
}