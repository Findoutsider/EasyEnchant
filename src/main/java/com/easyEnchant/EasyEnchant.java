package com.easyEnchant;

import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class EasyEnchant extends JavaPlugin {

    public static final String VERSION = "1.0.1";
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

        checkForUpdates();

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

    private void checkForUpdates() {
        new Thread(() -> {
            try {
                URL url = new URL("https://api.github.com/repos/Findoutsider/EasyEnchant/releases/latest");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    JSONObject jsonObject = getJsonObject(connection);
                    String latestVersion = (String) jsonObject.get("tag_name");
                    String info = (String) jsonObject.get("body");

                    if (latestVersion != null && !latestVersion.equalsIgnoreCase("v" + VERSION)) {
                        log.info(configGetString("update_available1") + latestVersion
                                + configGetString("update_available2"));
                        if (!info.equalsIgnoreCase("")) {
                            log.info(configGetString("update_info") + info);
                        }
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.isOp()) {
                                player.sendMessage("§8[§bDeathPunish§8] §r" + configGetString("update_available1")
                                        + " §a" + latestVersion
                                        + configGetString("update_available2"));
                                if (!info.equalsIgnoreCase("")) {
                                    player.sendMessage("§8[§bDeathPunish§8] §r" + configGetString("update_info")
                                            + "§a" + info);
                                }
                            }
                        }
                    } else {
                        log.info(configGetString("current_version") + VERSION);
                    }
                } else {
                    log.err(configGetString("update_error") + responseCode);
                }
            } catch (IOException | org.json.simple.parser.ParseException e) {
                log.err(configGetString("update_exception") + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }


    private static JSONObject getJsonObject(HttpURLConnection connection) throws IOException, org.json.simple.parser.ParseException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(content.toString());
    }
}
