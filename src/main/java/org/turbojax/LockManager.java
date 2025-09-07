package org.turbojax;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.turbojax.locks.Lock;

public class LockManager {
    private static final File lockFile = new File("plugins/ItemLocker/locks.yml");
    private static final FileConfiguration lockConfig = new YamlConfiguration();

    /** Reads data from the lock file. */
    public static void loadLocks() {
        // Creating the config file if it doesn't exist
        if (!lockFile.exists()) {
            lockFile.mkdirs();

            try {
                lockFile.createNewFile();
            } catch (IOException err) {
                ItemLocker.getInstance().getSLF4JLogger().warn("Could not create config.yml", err);
                return;
            }
        }

        // Loading data from the config
        try {
            lockConfig.load(lockFile);
        } catch (IOException err) {
            ItemLocker.getInstance().getSLF4JLogger().warn("Cannot find config.yml, check folder permissions.", err);
        } catch (InvalidConfigurationException err) {
            ItemLocker.getInstance().getSLF4JLogger().warn("config.yml does not have a valid YAML configuration.", err);
        }
    }

    /** Writes data to the config file. */
    public static void saveLocks() {
        // Creating the config file if it doesn't exist
        if (!lockFile.exists()) {
            lockFile.mkdirs();

            try {
                lockFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            lockConfig.save(lockFile);
        } catch (IOException e) {}
    }

    /**
     * Adds a lock to the config.
     *
     * @param lock The new lock to add.
     */
    public static void lock(Lock lock) {
        // Getting the locks
        List<Lock> locks = getLocks();

        // Adding the new lock
        locks.add(lock);

        // Putting the locks back into the config
        lockConfig.set("locks", locks);

        // Writing the config to disk
        saveLocks();
    }

    /**
     * Returns all the locks of a certain lock type.
     *
     * @param clazz The type of lock to return.
     *
     * @return All the locks of type clazz.
     */
    @NotNull
    public static List<? extends Lock> getLocks(Class<? extends Lock> clazz) {
        // Filtering the locks
        return getLocks().stream().filter(clazz::isInstance).toList();
    }

    /**
     * Returns all the locks.
     *
     * @return All the locks.
     */
    @NotNull
    public static List<Lock> getLocks() {
        // Getting the array from the config
        ArrayList<Lock> arr = null;
        try {
            arr = (ArrayList<Lock>) lockConfig.get("locks");
        } catch (ClassCastException err) {
            ItemLocker.getInstance().getSLF4JLogger().warn("Cannot deserialize the locks.  Check for errors in the config.", err);
        }

        // Returning an empty list if the config is null
        if (arr == null) return new ArrayList<>();

        // Returning the locks
        return arr;
    }

    /**
     * Removes a lock from the config.
     *
     * @param lock The lock to remove.
     */
    public static void unlock(Lock lock) {
        // Getting the locks
        List<Lock> locks = getLocks();

        // Removing the lock
        locks.remove(lock);

        // Putting the locks back into the config
        lockConfig.set("locks", locks);

        // Writing the config to disk
        saveLocks();
    }

    /**
     * Registers a new lock with the plugin.
     *
     * @param lock The lock class to register.
     */
    public static void registerLock(Class<? extends Lock> lock) {
        try {
            Bukkit.getPluginManager().registerEvents(lock.getConstructor().newInstance(), ItemLocker.getInstance());
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException err) {
            // Ignore registering this lock because it doesn't meet the requirements.
            ItemLocker.getInstance().getSLF4JLogger().warn("Cannot load Lock \"" + lock.getClass().getSimpleName() + "\": Cannot find and/or invoke a public default constructor.", err);
        }
    }

    /**
     * Unregisters a lock.
     *
     * @param lock The lock class to unregister.
     */
    public static void unregisterLock(Class<? extends Lock> lock) {
        try {
            HandlerList.unregisterAll(lock.getConstructor().newInstance());
        } catch (InstantiationException e) {
            // TODO: Implement better
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}