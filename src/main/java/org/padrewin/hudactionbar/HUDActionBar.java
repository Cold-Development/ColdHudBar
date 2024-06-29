package org.padrewin.hudactionbar;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HUDActionBar extends JavaPlugin implements Listener {
    private String actionBarMessage;
    private String pluginTag;
    private String noPermissionMessage;
    private String reloadSuccessMessage;
    private String version;
    private String developer;
    private String github;

    @Override
    public void onEnable() {
        saveDefaultConfig(); // Salvează config.yml dacă nu există deja

        // Încarcă configurația
        loadConfig();

        // Înregistrează comanda /hudactionbar reload și /hudactionbar info
        getCommand("hudactionbar").setExecutor(this);

        // Înregistrează evenimentele și pornește task-ul pentru Action Bar
        getServer().getPluginManager().registerEvents(this, this);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : getServer().getOnlinePlayers()) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionBarMessage));
                }
            }
        }.runTaskTimer(this, 0L, 20L); // Task care rulează la fiecare secundă
    }

    @Override
    public void onDisable() {
        // Logică pentru dezactivarea plugin-ului
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionBarMessage));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("hudactionbar")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("hudactionbar.reload")) {
                        reloadConfig();
                        loadConfig();
                        sender.sendMessage(formatMessage(pluginTag + " " + reloadSuccessMessage));
                    } else {
                        sender.sendMessage(formatMessage(pluginTag + " " + noPermissionMessage));
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("info")) {
                    sender.sendMessage(ChatColor.DARK_GRAY + "Version: v1.0");
                    sender.sendMessage(ChatColor.DARK_GRAY + "Developer: padrewin");
                    sender.sendMessage(ChatColor.DARK_GRAY + "GitHub: https://github.com/padrewin");
                    return true;
                }
            }
        }
        return false;
    }

    private void loadConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        // Încarcă mesajele din config.yml
        pluginTag = config.getString("pluginTag");
        actionBarMessage = config.getString("actionBarMessage");
        noPermissionMessage = config.getString("messages.noPermission");
        reloadSuccessMessage = config.getString("messages.reloadSuccess");
        version = config.getString("info.version");
        developer = config.getString("info.developer");
        github = config.getString("info.github");

        // Aplică formatarea de culori pentru actionBarMessage
        actionBarMessage = translateHexColorCodes(actionBarMessage);
        actionBarMessage = ChatColor.translateAlternateColorCodes('&', actionBarMessage);
    }

    private String formatMessage(String message) {
        // Expresie regulată pentru a găsi coduri de culoare HEX
        Pattern pattern = Pattern.compile("#[A-Fa-f0-9]{6}");

        Matcher matcher = pattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length());

        // Înlocuiește fiecare cod de culoare HEX cu formatul ChatColor corespunzător
        while (matcher.find()) {
            String color = matcher.group(); // Grupa care conține codul de culoare HEX
            ChatColor chatColor = ChatColor.of(new java.awt.Color(
                    Integer.valueOf(color.substring(1, 3), 16),
                    Integer.valueOf(color.substring(3, 5), 16),
                    Integer.valueOf(color.substring(5, 7), 16))); // Convertirea HEX în Color și apoi în ChatColor
            matcher.appendReplacement(buffer, chatColor.toString());
        }

        matcher.appendTail(buffer);
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    private String translateHexColorCodes(String message) {
        // Expresie regulată pentru a găsi coduri de culoare HEX
        Pattern pattern = Pattern.compile("#[A-Fa-f0-9]{6}");

        Matcher matcher = pattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length());

        // Înlocuiește fiecare cod de culoare HEX cu formatul ChatColor corespunzător
        while (matcher.find()) {
            String color = matcher.group(); // Grupa care conține codul de culoare HEX
            ChatColor chatColor = ChatColor.of(new java.awt.Color(
                    Integer.valueOf(color.substring(1, 3), 16),
                    Integer.valueOf(color.substring(3, 5), 16),
                    Integer.valueOf(color.substring(5, 7), 16))); // Convertirea HEX în Color și apoi în ChatColor
            matcher.appendReplacement(buffer, chatColor.toString());
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
