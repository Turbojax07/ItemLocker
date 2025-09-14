package org.turbojax.locks;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.turbojax.ItemLocker;

public class PotionLock implements Lock {
    public static Registry<PotionEffectType> effectRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.MOB_EFFECT);
    public PotionEffectType effect;
    public String potionType; // TODO: Try using this a bit
    public int level;
    public int amount;

    public PotionLock() {}

    public PotionLock(PotionEffectType effect, String potionType, int amount, int level) {
        this.effect = effect;
        this.potionType = potionType;
        this.level = level;
        this.amount = amount;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("effect", effect.getKey().getKey());
        map.put("potionType", potionType);
        map.put("level", level);
        map.put("amount", amount);

        return map;
    }

    // CONFIRMED THIS IS WHAT RUNS
    public static PotionLock deserialize(Map<String, Object> map) {
        NamespacedKey potionEffectKey = NamespacedKey.fromString((String) map.get("effect"));

        if (potionEffectKey == null) {
            ItemLocker.getInstance().getSLF4JLogger().warn("Could not find the potion effect specified");
            return null;
        }

        PotionEffectType potionEffect = effectRegistry.get(potionEffectKey);
        String potionType = (String) map.get("potionType");
        int level = (Integer) map.get("level");
        int amount = (Integer) map.get("amount");

        return new PotionLock(potionEffect, potionType, level, amount);
    }

    @Override
    public List<String> getSuggestions(List<String> args) {
        // First arg is the PotionEffectType
        if (args.isEmpty()) {
            return RegistryAccess.registryAccess().getRegistry(RegistryKey.MOB_EFFECT).stream().map(PotionEffectType::getKey).map(NamespacedKey::toString).toList();
        }

        // Second arg is the level
        if (args.size() == 1) {
            if (NamespacedKey.fromString(args.getFirst()) == null) {
                return List.of();
            }


//            effectRegistry.get(NamespacedKey.fromString(args.getFirst())).get;
        }
//        if (fieldNum == 1) return List.of("1", "2");

        return List.of();
    }

    public static PotionLock fromCommand(String[] args) {
        if (args.length != 4) {
            ItemLocker.getInstance().getSLF4JLogger().warn("Cannot create instance of PotionLock.  ");
            return null;
        }

        NamespacedKey potionEffectKey = NamespacedKey.fromString(args[0]);

        if (potionEffectKey == null) {
            ItemLocker.getInstance().getSLF4JLogger().warn("Could not find the potion effect specified");
            return null;
        }

        PotionEffectType potionEffect = effectRegistry.get(potionEffectKey);
        String potionType = args[1];
        int level = Integer.parseInt(args[2]);
        int amount = Integer.parseInt(args[3]);

        return new PotionLock(potionEffect, potionType, level, amount);
    }
}