package com.easyEnchant;

import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.bukkit.command.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.easyEnchant.EasyEnchant.ENCHANTMENTS;
import static com.easyEnchant.EasyEnchant.config;

public class Commands implements CommandExecutor, TabCompleter {

    private int level = -1;

    @Override
    public boolean onCommand(CommandSender sender, Command command,  String s,  String[] args) {
        if (((sender instanceof Player && sender.hasPermission("easyenchant.command")) || sender instanceof ConsoleCommandSender)
                && s.equalsIgnoreCase("easyenchant") || s.equalsIgnoreCase("ee")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    EasyEnchant.getPlugin(EasyEnchant.class).reloadConfig();
                    config = EasyEnchant.getPlugin(EasyEnchant.class).getConfig();
                    sender.sendMessage("§a√");
                    return true;
                }
                if (ENCHANTMENTS.contains(args[0])) {
                    for (Enchantment enchantment : Registry.ENCHANTMENT) {
                        if (enchantment.getKey().getKey().equalsIgnoreCase(args[0])) {
                            sender.sendMessage("§a[EasyEnchant]");
                            sender.sendMessage("§7Enchantment: §a" + enchantment.getKey().getKey());
                        }
                    }
                    return true;
                }
                help(sender);
                return false;
            }
            if (args.length == 2) {
                if (sender instanceof Player) {
                    for (Enchantment enchantment : Registry.ENCHANTMENT) {
                        if (enchantment.getKey().getKey().equalsIgnoreCase(args[0])) {
                                Enchant.enchant((Player) sender, ((Player) sender).getInventory().getItemInMainHand(), Integer.parseInt(args[1]), args[0]);
                                return true;
                        }
                    }
                }
            }

            if (args.length == 3) {
                Player player = Bukkit.getPlayer(args[2]);
                if (player != null) {
                    for (Enchantment enchantment : Registry.ENCHANTMENT) {
                        if (enchantment.getKey().getKey().equalsIgnoreCase(args[0])) {
                                Enchant.enchant(player, player.getInventory().getItemInMainHand(), Integer.parseInt(args[1]), args[0]);
                                return true;
                        }
                    }
                }
            }
            help(sender);
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (sender.hasPermission("easyenchant.use") || sender instanceof ConsoleCommandSender) {
            if (args.length == 1) {
                return ENCHANTMENTS;
            } else if (args.length == 2) {
                return IntStream.rangeClosed(1, 100)
                        .mapToObj(Integer::toString)
                        .collect(Collectors.toList());
            } else if (args.length > 3) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            }
        }
        return null;
    }

    public void help(CommandSender sender) {
        sender.sendMessage("§a[EasyEnchant]");
        sender.sendMessage("§7/ee <enchantment> <level> <player>");
        if (sender instanceof Player) {
            sender.sendMessage("§7/ee <enchantment> <level>");
        }

        sender.sendMessage("§7/ee help");
        sender.sendMessage("§7/ee reload");
    }
}