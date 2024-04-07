package io.lightplugins.economy.eco.commands.eco;

import io.lightplugins.economy.eco.LightEco;
import io.lightplugins.economy.eco.inventories.BaltopGUI;
import io.lightplugins.economy.util.SubCommand;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class EcoTopCommand extends SubCommand {
    @Override
    public List<String> getName() {
        return List.of("top");
    }

    @Override
    public String getDescription() {
        return "shows the richest players";
    }

    @Override
    public String getSyntax() {
        return "/eco top";
    }

    @Override
    public int maxArgs() {
        return 1;
    }

    @Override
    public String getPermission() {
        return "lighteconomy.eco.top";
    }

    @Override
    public TabCompleter registerTabCompleter() {
        return ((commandSender, command, s, args) -> {

            if(!commandSender.hasPermission(getPermission())) {
                return null;
            }

            if(args.length == 1) {
                return List.of("top");
            }

            return null;
        });
    }

    @Override
    public boolean performAsPlayer(Player player, String[] args) throws ExecutionException, InterruptedException {

        for(File file : LightEco.inventoriesFiles) {

            String cfgName = LightEco.instance.getMultiFileManager().getFileNameWithoutExtension(file);

            if(cfgName.equalsIgnoreCase("baltop")) {
                BaltopGUI baltopGUI = new BaltopGUI(file);
                baltopGUI.openBaltopGUI(player);
                player.sendMessage("opened baltop gui");
                return false;
            }
            player.sendMessage("gui does not match: " + cfgName);
            return true;
        }
        player.sendMessage("Gui not found: size -> 0");
        return false;
    }

    @Override
    public boolean performAsConsole(ConsoleCommandSender sender, String[] args) throws ExecutionException, InterruptedException {
        return false;
    }
}
