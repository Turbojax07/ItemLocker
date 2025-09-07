package org.turbojax.locks;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.turbojax.ItemLocker;
import org.turbojax.LockManager;

public class ItemLock implements Lock {
    private ItemType itemType;
    private int amount;

    public ItemLock() {}

    public ItemLock(ItemType itemType, int amount) {
        this.itemType = itemType;
        this.amount = amount;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("material", itemType.getKey().toString());
        map.put("amount", amount);

        return map;
    }

    public static ItemLock deserialize(Map<String, Object> map) {
        Registry<ItemType> materialRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM);

        NamespacedKey materialKey = NamespacedKey.fromString((String) map.get("material"));

        if (materialKey == null) {
            ItemLocker.getInstance().getSLF4JLogger().warn("Could not find the material specified");
            return null;
        }

        ItemType material = materialRegistry.get(materialKey);
        int amount = (Integer) map.get("amount");

        return new ItemLock(material, amount);
    }

    @Override
    public List<String> getSuggestions(List<String> args) {
        // First arg is the ItemType
        if (args.isEmpty()) {
            return RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM).stream().map(ItemType::getKey).map(NamespacedKey::toString).toList();
        }

        return List.of();
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemLock lock)) return false;

        return lock.itemType == this.itemType && lock.amount == this.amount;
    }

    public void onPlayerPickup(EntityPickupItemEvent event) {
        // Ignoring non-players
        if (!(event.getEntity() instanceof Player player)) return;

        LockManager.getLocks(ItemLock.class);
    }
}