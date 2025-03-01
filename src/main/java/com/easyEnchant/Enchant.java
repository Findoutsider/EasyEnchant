package com.easyEnchant;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static com.easyEnchant.EasyEnchant.configGetString;
import static com.easyEnchant.EasyEnchant.log;

public class Enchant {
    public static void enchant(Player player, ItemStack item, int level, String enchantmentName) {
        if (item == null || item.getType().isAir()) {
            player.sendMessage("§c" + configGetString("messages.noItemInHand"));
            return;
        }

        Enchantment enchantment = getEnchantmentByName(enchantmentName);
        if (enchantment == null) {
            player.sendMessage("§c" + configGetString("messages.invalidEnchantment") + ": " + enchantmentName);
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            player.sendMessage("§c" + configGetString("messages.failedToEnchant_getMeta"));
            return;
        }
        if (level == 0) {
            if (meta.hasEnchant(enchantment)) {
                meta.removeEnchant(enchantment);
                item.setItemMeta(meta);
                player.sendMessage("§a" + configGetString("messages.clearEnchantment") + ":§a " + enchantmentName);
            } else {
                player.sendMessage("§c" + configGetString("messages.failedToEnchant_errorLevel")
                        + ":§a " + enchantment.getStartLevel() + " - " + 32767);
            }
            return;
        }

        if (level < enchantment.getStartLevel() || level > 32767) {
            player.sendMessage("§c" + configGetString("messages.failedToEnchant_errorLevel")
                    + ":§a " + enchantment.getStartLevel() + " - " + 32767);
            return;
        }

        meta.addEnchant(enchantment, level, true);
        item.setItemMeta(meta);
        player.sendMessage("§a" + configGetString("messages.successEnchant") + ":§a " + enchantmentName);
    }

    private static Enchantment getEnchantmentByName(String name) {
        for (Enchantment enchantment : Enchantment.values()) {
            if (enchantment.getKey().getKey().equalsIgnoreCase(name)) {
                return enchantment;
            }
        }
        return null;
    }
}