package io.lightplugins.economy.eco.inventories;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import io.lightplugins.economy.LightEconomy;
import io.lightplugins.economy.util.content.watcher.GuiItemContent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class BaltopGUI {

    private final File file;

    public BaltopGUI(File file) {
        this.file = file;
    }

    public void openBaltopGUI(Player player) {

        String skinURL = "https://textures.minecraft.net/texture/";
        UUID testUUID = UUID.fromString("4e2080e7-104d-45f5-a97f-3d969805a1d7");

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

        List<?> contentsList = conf.getList("contents");
        if (contentsList != null) {
            for (Object obj : contentsList) {
                if (obj instanceof Map<?, ?>) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> contentMap = (Map<String, Object>) obj;

                    // Übergebe die ConfigurationSection an die andere Methode
                    GuiItemContent guiItemContent = new GuiItemContent(contentMap);
                    ItemStack item = guiItemContent.getGuiItem();

                    LightEconomy.getDebugPrinting().print("Updating item " + item);
                    if (item.getItemMeta() instanceof SkullMeta skullMeta) {
                        CompletableFuture.runAsync(() -> {
                            LightEconomy.getDebugPrinting().print("Updating is skull meta: " + item.getItemMeta());
                            PlayerProfile playerProfile = Bukkit.createPlayerProfile(testUUID);
                            try {
                                playerProfile.getTextures().setSkin(URI.create(skinURL + testUUID).toURL());
                            } catch (MalformedURLException e) {
                                throw new RuntimeException(e);
                            }

                            skullMeta.setOwnerProfile(playerProfile);
                            item.setItemMeta(skullMeta);
                            gui.update();
                        });
                    } else {
                        LightEconomy.getDebugPrinting().print("Item is not a Skull " + item);
                    }
                    topPane.addItem(new GuiItem(item, event -> {

                    }), guiItemContent.getPosX(), guiItemContent.getPosY());
                } else {
                    // Debug-Ausgabe für den Fall, dass das Element kein Map-Objekt ist
                    LightEconomy.getDebugPrinting().print("Inhalt ist kein Map-Objekt: " + obj);
                }
            }
        } else {
            // Debug-Ausgabe für den Fall, dass die Liste "contents" leer oder nicht vorhanden ist
            System.out.println("Die Liste 'contents' ist leer oder nicht vorhanden.");
        }

        gui.addPane(topPane);
        gui.show(player);
    }
}
