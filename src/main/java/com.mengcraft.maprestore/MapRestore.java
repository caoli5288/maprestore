package com.mengcraft.maprestore;

import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Created on 16-2-20.
 */
public class MapRestore extends JavaPlugin {

    private static MapRestore instance;

    public static void restoreMap(String name) {
        if (instance == null) {
            throw new RuntimeException("DEBUG #3");
        }
        if (instance.getServer().unloadWorld(name, false) && DEBUG) {
            instance.getLogger().info("Unloaded " + name + '!');
        }
        instance.restore(name);

        if (DEBUG) {
            instance.getLogger().info("Loading " + name + '!');
        }
        instance.getServer().createWorld(new WorldCreator(name));
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        for (String line : getConfig().getStringList("restore-on-startup")) {
            restore(line);
        }
        setInstance(this);
    }

    private void restore(String line) {
        File source = new File(getDataFolder(), line);
        if (valid(source)) {
            restore(source, new File(getServer().getWorldContainer(), line));
        } else if (DEBUG) {
            getLogger().warning("Not found " + line + '!');
        }
    }

    private void restore(File source, File target) {
        if (DEBUG) {
            getLogger().info("Restoring " + source.getName() + "...");
        }
        if (target.exists()) {
            delete(target);
        }
        copy(source, target);
    }

    private static void copy(File source, File target) {
        try {
            int result = new ProcessBuilder("cp", "-a", source.getAbsolutePath(), target.getAbsolutePath())
                    .start()
                    .waitFor();
            if (result != 0) {
                throw new RuntimeException("DEBUG #2");
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void delete(File file) {
        try {
            int result = new ProcessBuilder("rm", "-r", "-f", file.getAbsolutePath())
                    .start()
                    .waitFor();
            if (result != 0) {
                throw new RuntimeException("DEBUG #1");
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean valid(File file) {
        return file.isDirectory() && new File(file, "level.dat").isFile();
    }

    public static void setInstance(MapRestore instance) {
        MapRestore.instance = instance;
    }

    public static final boolean DEBUG = true;

}
