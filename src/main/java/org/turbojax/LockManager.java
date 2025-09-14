package org.turbojax;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.turbojax.locks.Lock;

public class LockManager {
    private static final File lockFile = new File("plugins/ItemLocker/locks.yml");
    private static final FileConfiguration lockConfig = new YamlConfiguration();

    public static void createLockFile() {
        // Creating the lock file if it doesn't exist
        if (!lockFile.exists()) {
            lockFile.getParentFile().mkdirs();

            try {
                lockFile.createNewFile();
            } catch (IOException err) {
                // TODO: Implement messages.yml
                ItemLocker.getInstance().getSLF4JLogger().warn("Could not create locks.yml", err);
            }
        }
    }
    /** Reads locks from the lock file. */
    public static void loadLocks() {
        createLockFile();

        // Loading data from the config
        try {
            lockConfig.load(lockFile);
        } catch (IOException err) {
            // TODO: Implement messages.yml
            ItemLocker.getInstance().getSLF4JLogger().warn("Cannot find locks.yml, check folder permissions.", err);
        } catch (InvalidConfigurationException err) {
            // TODO: Implement messages.yml
            ItemLocker.getInstance().getSLF4JLogger().warn("locks.yml does not have a valid YAML configuration.", err);
        }
    }

    /** Writes locks to the lock file. */
    public static void saveLocks() {
        createLockFile();

        try {
            lockConfig.save(lockFile);
        } catch (IOException err) {
            // TODO: Implement messages.yml
            ItemLocker.getInstance().getSLF4JLogger().warn("Cannot find locks.yml, check folder permissions.", err);
        }
    }

    /**
     * Adds a lock to the config.
     *
     * @param lock The new lock to add.
     */
    public static void addLock(Lock lock) {
        // Getting the locks
        List<Lock> locks = getLocks();

        // Adding the new lock
        locks.add(lock);

        // Putting the locks back into the config
        lockConfig.set("locks", locks);

        // Writing the locks to disk
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
    public static <T extends Lock> List<T> getLocks(Class<T> clazz) {
        // Filtering the locks
        return getLocks().stream().filter(clazz::isInstance).map(clazz::cast).toList();
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
            // TODO: Implement messages.yml
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
     * @param toRemove The lock to remove.
     */
    public static void removeLock(Lock toRemove) {
        // Getting the locks
        List<Lock> locks = getLocks();

        // Removing the lock
        locks.removeIf(lock -> lock.matches(toRemove));


        // Putting the locks back into the config
        lockConfig.set("locks", locks);

        // Writing the locks to disk
        saveLocks();
    }
}