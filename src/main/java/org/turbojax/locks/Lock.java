package org.turbojax.locks;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.event.Listener;
import org.turbojax.LockManager;

public interface Lock extends ConfigurationSerializable, Listener {
    /**
     * This function is repeatedly executed by the plugin.<br>
     * The time between loops can be changed, but the default is every 20 seconds.
     */
    void loop();

    /**
     * Returns a string showing the correct way to use this lock in the lock/unlock commands.<br>
     * Example: <code>ItemLock &lt;material&gt; &lt;amount&gt;</code>
     *
     * @return A string showing the correct way to use this lock.
     */
    String getUsage();

    /**
     * This is the logic for when a player adds a new lock with commands.
     *
     * @param sender The sender of the command.
     * @param command The command instance.
     * @param label The string being used to run the command.  Can be an alias.
     * @param args The arguments passed into the command.
     *
     * @return Whether or not the lock was added successfully.
     */
    boolean onLock(CommandSender sender, Command command, String label, String[] args);

    /**
     * This is the logic for when a player removes an existing lock with commands.
     *
     * @param sender The sender of the command.
     * @param command The command instance.
     * @param label The string being used to run the command.  Can be an alias.
     * @param args The arguments passed into the command.
     *
     * @return Whether or not the lock was removed successfully.
     */
    boolean onUnlock(CommandSender sender, Command command, String label, String[] args);

    /**
     * This is the logic to suggest values for the different parameters of a lock.
     *
     * @param sender The sender of the command.
     * @param command The command instance.
     * @param label The string being used to run the command.  Can be an alias.
     * @param args The arguments passed into the command.
     *
     * @return A list of strings to suggest to the user.
     */
    List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args);

    /**
     * Checks if this lock matches the other lock.
     * This is mostly just for determining which lock to remove when running {@link LockManager#removeLock}.
     * It is recommended to allow for some pattern a user could input that allows for the removal of multiple locks at once.
     * For example, <code>EnchantmentLock lock = new EnchantmentLock(Enchantment.FIRE_ASPECT, -1);</code>
     * Could be used to remove all locks for the fire aspect enchantment, including these:
     * <code>
     * EnchantmentLock lock1 = new EnchantmentLock(Enchantment.FIRE_ASPECT, 2);
     * EnchantmentLock lock2 = new EnchantmentLock(Enchantment.FIRE_ASPECT, 3);
     * </code>
     *
     * @param other The lock to compare itself to.
     *
     * @return Whether or not the locks match.
     */
    boolean matches(Lock other);
}