package com.mengcraft.maprestore;

import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
        try {
            if (target.exists()) {
                delete(target);
            }
            copy(source, target);
        } catch (IOException e) {
            throw new RuntimeException("Restore error!", e);
        }
    }

    public static void copy(File source, File target) throws IOException {
        if (source.isDirectory()) {
            if (!target.isDirectory()) {
                target.mkdir();
            }
            for (File f : source.listFiles()) {
                copy(f, new File(target, f.getName()));
            }
        } else {
            Files.copy(source.toPath(), target.toPath());
        }
    }

    public static void delete(File file) throws IOException {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                delete(f);
            }
            Files.delete(file.toPath());
        } else {
            Files.delete(file.toPath());
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
