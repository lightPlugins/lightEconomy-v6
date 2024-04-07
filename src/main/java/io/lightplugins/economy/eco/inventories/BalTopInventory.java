package io.lightplugins.economy.eco.inventories;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import io.lightplugins.economy.LightEconomy;
import io.lightplugins.economy.util.content.watcher.GuiItemContent;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;

public class BalTopInventory {

    public ChestGui createGui(File file) {

        FileConfiguration conf = YamlConfiguration.loadConfiguration(file);

        String title = LightEconomy.instance.colorTranslation.convertToString(conf.getString("gui-title"));
        int rows = conf.getInt("rows");

        ChestGui gui = new ChestGui(rows, title);

        //  disable the click-event for ALL Items in this gui

        gui.setOnGlobalClick(event -> event.setCancelled(true));

        OutlinePane background = new OutlinePane(0, 0, 9, rows, Pane.Priority.LOWEST);
        background.addItem(new GuiItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)));
        background.setRepeat(true);

        gui.addPane(background);

        StaticPane topPane = new StaticPane(0, 0, 9, rows);

        int size = conf.getConfigurationSection("contents").getKeys(false).size();

        for(int i = 0; i < size; i++) {

        }

        for(String path : conf.getConfigurationSection("contents").getKeys(false)) {



            if(!conf.isConfigurationSection("contents." + path)) {
                return null;
            }

            GuiItemContent guiItemContent = new GuiItemContent(conf.getConfigurationSection("contents." + path));
            ItemStack item = guiItemContent.getGuiItem();

            if(item == null) {
                item = new ItemStack(Material.BEDROCK, 1);
            }

            topPane.addItem(new GuiItem(item, event -> {

            }), 1, 1);


        }

        gui.addPane(topPane);
        return gui;
    }
}
