package com.easyEnchant;

import org.bukkit.Registry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class EasyEnchant extends JavaPlugin {

    public static final String version = "1.0";
    public static EasyEnchant instance;
    public static LoggerUtils log;
    public static List<String> ENCHANTMENTS = new ArrayList<>();
    public static FileConfiguration config;

    public EasyEnchant() {
        instance = this;
        log = new LoggerUtils();
    }

    @Override
    public void onEnable() {
        int pluginId = 24684; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);
        this.getCommand("easyenchant").setExecutor(new Commands());
        this.getCommand("easyenchant").setTabCompleter(new Commands());
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            ENCHANTMENTS.add(enchantment.getKey().getKey());
        }

        saveDefaultConfig();
        config = getConfig();

        log.info("EasyEnchant 已加载");

    }

    @Override
    public void onDisable() {
        log.info("EasyEnchant 已卸载");
    }

    @Override
    public void saveDefaultConfig() {
        File configFile = new File(getDataFolder(), "config.yml");

        // 检查配置文件是否存在
        if (!configFile.exists()) {
            // 如果文件不存在，写入默认配置
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            try {
                config = getConfig();
            } catch (Exception e) {
                // 如果配置文件读取失败，删除文件并写入默认配置
                configFile.delete();
                getConfig().options().copyDefaults(true);
                saveConfig();
                log.err("配置文件读取失败，已恢复默认配置 Failed to read the configuration file and the default configuration has been restored");
            }
        }
    }

    public static String configGetString(String s) {
        return config.getString(s);
    }
}
