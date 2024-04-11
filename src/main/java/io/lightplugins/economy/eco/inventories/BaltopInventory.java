package io.lightplugins.economy.eco.inventories;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import io.lightplugins.economy.LightEconomy;
import io.lightplugins.economy.util.handler.ClickItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.net.URI;
import java.util.Objects;

public class BaltopInventory {

    private final ChestGui gui = new ChestGui(6, "Init");
    private final Player player;
    private final File file;
    private BukkitTask bukkitTask;

    public BaltopInventory(Player player, File file) {
        this.player = player;
        this.file = file;
    }

    public void openInventory() {

        FileConfiguration conf = YamlConfiguration.loadConfiguration(file);

        String title = LightEconomy.instance.colorTranslation.convertToString(conf.getString("gui-title"), player);
        int rows = conf.getInt("rows");

        String[] patternList = conf.getStringList("pattern").toArray(new String[0]);

        gui.setRows(rows);
        gui.setTitle(title);
        gui.setOnGlobalClick(event -> event.setCancelled(true));

        Pattern pattern = new Pattern(patternList);
        PatternPane pane = new PatternPane(0, 0, 9, rows, pattern);

        for(String patternIdentifier : conf.getConfigurationSection("contents").getKeys(false)) {
            ClickItemHandler clickItemHandler = new ClickItemHandler(
                    Objects.requireNonNull(conf.getConfigurationSection(
                            "contents." + patternIdentifier)), null);

            ItemStack itemStack = clickItemHandler.getGuiItem();

            if (clickItemHandler.getGuiItem().getItemMeta() instanceof SkullMeta skullMeta) {
                PlayerProfile playerProfile = Bukkit.createPlayerProfile("Beampur");
                skullMeta.setOwnerProfile(playerProfile);
                clickItemHandler.getGuiItem().setItemMeta(skullMeta);
                itemStack.setItemMeta(skullMeta);
            }


            pane.bindItem(patternIdentifier.charAt(0), new GuiItem(itemStack, inventoryClickEvent -> {
                player.sendMessage("clicked on " + patternIdentifier + " with " + clickItemHandler.getActions());

            }));
        }

        gui.setOnClose(event -> {
            if(bukkitTask != null) {
                bukkitTask.cancel();
            }
        });

        gui.addPane(pane);
        gui.show(player);
        startUpdater();


    }

    private void startUpdater() {

        final int[] timer = {0};

        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                timer[0]++;
                gui.setTitle("Updating... " + timer[0]);
                gui.update();

            }
        }.runTaskTimer(LightEconomy.instance, 20, 20);

    }
}
