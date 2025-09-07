package org.turbojax.locks;

import java.util.*;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionEffectType;

public interface Lock extends ConfigurationSerializable, Listener {
    static Lock deserialize(Map<String, Object> map) {
        // Getting all the parameters for every lock type
        PotionEffectType effectType = (PotionEffectType) map.get("effect");
        ItemType itemType = (ItemType) map.get("material");
        Enchantment enchantment = (Enchantment) map.get("enchantment");
        int level = (Integer) map.get("level");
        int amount = (Integer) map.get("amount");

        // Checking if the potion effect type is defined, signaling a PotionLock
        if (effectType != null) {
            return new PotionLock(effectType, level, amount);
        }

        // Checking if the item type is defined, signaling a MaterialLock
        if (itemType != null) {
            return new ItemLock(itemType, amount);
        }

        // Checking if the enchantment is defined, signaling an EnchantmentLock
        if (enchantment != null) {
            return new EnchantmentLock(enchantment, level);
        }

        return null;
    }

    static List<Class<? extends Lock>> findImplementations() {
        List<Class<? extends Lock>> implementations = new ArrayList<>();

        ServiceLoader<Lock> itemLocks = ServiceLoader.load(Lock.class);
        for (Lock itemLock : itemLocks) {
            implementations.add(itemLock.getClass());
        }

        return implementations;
    }

    /**
     * Gets a list of strings to suggest to the player when they are using the lock or unlock commands.
     *
     * @param args The current arguments to the command.
     *
     * @return A list of strings that will be suggested to the player attempting to add or remove a lock.
     */
    List<String> getSuggestions(List<String> args);
}