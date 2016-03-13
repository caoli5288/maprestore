package com.mengcraft.maprestore;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created on 16-2-20.
 */
public class MapRestore extends JavaPlugin {

    @Override
    public void onEnable() {
        getDataFolder().mkdir();
        for (File f : getDataFolder().listFiles()) {
            if (valid(f)) {
                restore(f);
            }
        }
    }

    private void restore(File source) {
        if (valid(source)) {
            restore(source, new File(getServer().getWorldContainer(), source.getName()));
        }
    }

    private void restore(File source, File target) {
        try {
            if (target.exists()) {
                rm(target);
            }
            cp(source, target);
        } catch (IOException e) {
            throw new RuntimeException("Restore error!", e);
        }
    }

    private static void cp(File source, File target) throws IOException {
        if (source.isDirectory()) {
            if (!target.isDirectory()) {
                target.mkdir();
            }
            for (File f : source.listFiles()) {
                cp(f, new File(target, f.getName()));
            }
        } else {
            Files.copy(source.toPath(), target.toPath());
        }
    }

    private static void rm(File file) throws IOException {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                rm(f);
            }
            Files.delete(file.toPath());
        } else {
            Files.delete(file.toPath());
        }
    }

    public void removeMap(World world) {
        removeMap(world.getName());
    }

    public void removeMap(String name) {
        try {
            File target = new File(getDataFolder(), name);
            if (target.exists()) {
                rm(target);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addMap(World world) {
        try {
            File target = new File(getDataFolder(), world.getName());
            if (target.exists()) {
                rm(target);
            }
            cp(world.getWorldFolder(), target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean valid(File file) {
        if (file.isDirectory() && new File(file, "level.dat").isFile()) {
            return true;
        } else {
            getLogger().warning(file + " not a valid world container!");
        }
        return false;
    }

}
