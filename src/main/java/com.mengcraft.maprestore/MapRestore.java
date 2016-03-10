package com.mengcraft.maprestore;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Created on 16-2-20.
 */
public class MapRestore extends JavaPlugin {

    @Override
    public void onEnable() {
        for (String line : getConfig().getStringList("restore-on-startup")) {
            restore(line);
        }
        getDataFolder().mkdir();
    }

    private void restore(String line) {
        File source = new File(getDataFolder(), line);
        if (valid(source)) {
            restore(source, new File(getServer().getWorldContainer(), line));
        }
    }

    private void restore(File source, File target) {
        try {
            if (target.exists()) {
                drop(target);
            }
            copy(source, target);
        } catch (IOException e) {
            throw new RuntimeException("Restore error!", e);
        }
    }

    private static void copy(File source, File target) throws IOException {
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

    private static void drop(File file) throws IOException {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                drop(f);
            }
            Files.delete(file.toPath());
        } else {
            Files.delete(file.toPath());
        }
    }

    public void addMap(World world) {
        try {
            copy(world.getWorldFolder(), new File(getDataFolder(), world.getName()));
            List<String> list = getConfig().getStringList("restore-on-startup");
            list.add(world.getName());
            getConfig().set("restore-on-startup", list);
            saveConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean valid(File file) {
        return file.isDirectory() && new File(file, "level.dat").isFile();
    }

}
