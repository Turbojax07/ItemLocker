package org.turbojax.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.turbojax.LockManager;
import org.turbojax.locks.ItemLock;
import org.turbojax.locks.Lock;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ItemLockCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            // TODO: Add help msg implementation.
        }

        List<Class<? extends Lock>> locks = Lock.findImplementations();

        if (args[0].equalsIgnoreCase("help")) {
            if (args.length == 2) {
                StringBuilder helpMsg = new StringBuilder();

                // Adding the class name to the help message.
                Class<? extends Lock> clazz = locks.stream().filter(c -> c.getName().equals(args[1])).toList().getFirst();
                helpMsg.append(clazz.getName());
                helpMsg.append(" parameters: ");

                // Adding the fields and their types to the help message.
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    helpMsg.append(field.getType().getName()).append(" ").append(field.getName()).append(", ");
                }

                // Removing the trailing comma and space.
                helpMsg.delete(helpMsg.length() - 2, helpMsg.length());

                sender.sendMessage(helpMsg.toString());

                return true;
            }

            // TODO: Print whole help message

        }

        if (args[0].equalsIgnoreCase("reload")) {
            LockManager.loadLocks();
            // TODO: Maybe add a message output here?
            return true;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        // TODO: Finish tab completion for "list", "lock", and "unlock"

        if (args.length == 1) {
            return Stream.of("help", "list", "lock", "reload", "unlock").filter(entry -> entry.startsWith(args[0])).toList();
        }

        // Handling help
        if (args[0].equalsIgnoreCase("help")) {
            List<Class<? extends Lock>> locks = Lock.findImplementations();

            // Suggesting Lock classes
            if (args.length == 2) {
                return locks.stream().map(Class::getName).filter(s -> s.startsWith(args[1])).toList();
            }
        }

        // Handling reload
        if (args[0].equalsIgnoreCase("reload")) return List.of();

        List<Class<? extends Lock>> locks = Lock.findImplementations();
        try {
            Method method = locks.getFirst().getMethod("getSuggestions", List.class);
            List<String> argList = Arrays.asList(args);
            argList.removeFirst();
            argList.removeFirst();
            method.invoke(null, Arrays.asList(args));
            // TODO: Find out how to run getSuggestions.
            // It could be defined in the interface, forcing implementations to override it, but it'd require an instance to operate.
            // Going with this ^.  In order for the locks to be registered listeners, you need to make an instance of them.
            // I have to ask for a constructor with no params bc i cant force a static method in an interface...
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (args.length == 2) {
            // At this point, no matter the args, the parameters are the same.
            // Returning a list of all the locks.
            return locks.stream().map(Class::getName).map(String::toLowerCase).toList();
        }

        // Returning empty lists for "list".
        if (args[0].equalsIgnoreCase("list")) return List.of();

        if (args.length == 3) {
            Field[] fields = locks.getFirst().getDeclaredFields();
            fields[0].getType();
        }

        return List.of();
    }
}
