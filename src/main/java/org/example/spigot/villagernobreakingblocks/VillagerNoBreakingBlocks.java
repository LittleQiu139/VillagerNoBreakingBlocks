package org.example.spigot.villagernobreakingblocks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VillagerNoBreakingBlocks extends JavaPlugin implements Listener, CommandExecutor {
    private Set<Material> crops;

    @Override
    public void onEnable() {
        // 保存默认配置文件
        saveDefaultConfig();

        // 加载配置文件
        loadConfig();

        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("vnb").setExecutor(this);
        getLogger().info("VillagerNoBreakingBlocks plugin enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("VillagerNoBreakingBlocks plugin disabled!");
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity().getType() == EntityType.VILLAGER) {
            Material material = event.getBlock().getType();

            if (crops.contains(material)) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("vnb")) {
            if (args.length > 0) {
                String subCommand = args[0].toLowerCase();

                if (subCommand.equals("reload") || subCommand.equals("r")) {
                    // 重新加载配置文件
                    reloadConfig();

                    // 获取配置文件对象
                    FileConfiguration config = getConfig();

                    // 打印配置文件中的农作物列表
                    List<String> cropNames = config.getStringList("crops");
                    getLogger().info("Crops from config: " + cropNames);

                    // 发送消息给命令发送者
                    sender.sendMessage("重载完成.");
                    sender.sendMessage("禁止村民破坏的农作物种类如下: " + cropNames);

                    return true;
                }
            }

            // 如果命令格式不正确，发送帮助信息
            sender.sendMessage("使用方法:");
            sender.sendMessage("/vnb reload 或 /vnb r - 重载配置文件");
            return true;
        }
        return false;
    }

    private void loadConfig() {
        // 从配置文件中加载农作物列表
        List<String> cropNames = getConfig().getStringList("crops");

        crops = new HashSet<>();
        for (String cropName : cropNames) {
            try {
                Material material = Material.valueOf(cropName);
                crops.add(material);
                getLogger().info("Loaded crop: " + cropName);
            } catch (IllegalArgumentException e) {
                getLogger().warning("Unknown material: " + cropName);
            }
        }

    }

}