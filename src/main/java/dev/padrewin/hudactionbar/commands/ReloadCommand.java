package dev.padrewin.hudactionbar.commands;

import java.util.Collections;
import java.util.List;
import dev.padrewin.hudactionbar.HudActionbar;
import dev.padrewin.hudactionbar.manager.CommandManager;
import dev.padrewin.hudactionbar.manager.LocaleManager;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends BaseCommand {

    public ReloadCommand() {
        super("reload", CommandManager.CommandAliases.RELOAD);
    }

    @Override
    public void execute(HudActionbar plugin, CommandSender sender, String[] args) {
        if (!sender.hasPermission("hudactionbar.reload")) {
            plugin.getManager(LocaleManager.class).sendMessage(sender, "no-permission");
            return;
        }

        if (args.length > 0) {
            plugin.getManager(LocaleManager.class).sendMessage(sender, "command-reload-usage");
            return;
        }

        plugin.reloadConfig();
        plugin.loadConfig();

        plugin.getManager(LocaleManager.class).sendMessage(sender, "command-reload-success");
    }

    @Override
    public List<String> tabComplete(HudActionbar plugin, CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}