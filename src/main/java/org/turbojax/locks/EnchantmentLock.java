package org.turbojax.locks;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.turbojax.ItemLocker;

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
    public List<String> getSuggestions(List<String> args) {
        // First arg is the Enchantment
        if (args.isEmpty()) {
            return enchantmentRegistry.stream().map(Enchantment::getKey).map(NamespacedKey::toString).toList();
        }

        // Second arg is the level
        if (args.size() == 1) {
            if (NamespacedKey.fromString(args.getFirst()) == null) {}

            List<String> suggestions = new ArrayList<>();
            for (int i = 1; i <= enchantmentRegistry.get(NamespacedKey.fromString(args.getFirst())).getMaxLevel(); i++) {
                suggestions.add(String.valueOf(i));
            }

            return suggestions;
        }

        return List.of();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EnchantmentLock lock)) return false;

        return lock.enchantment == this.enchantment && lock.level == this.level;
    }
}