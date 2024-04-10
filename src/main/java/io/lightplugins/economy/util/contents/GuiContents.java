package io.lightplugins.economy.util.contents;

import io.lightplugins.economy.LightEconomy;
import io.lightplugins.economy.util.NumberFormatter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GuiContents {

    private final ConfigurationSection GUI_ITEM_ARGS;
    private final ConfigurationSection ACTION_SECTION;
    private final ConfigurationSection PLACEHOLDERS;
    private final Player player;
    private String item;
    private String displayName;
    private List<String> lore = new ArrayList<>();
    private Map<String, String> placeholders = new HashMap<>();
    private int itemPosX;
    private int itemPosY;
    private int modelData;
    private boolean playerHead = false;


    public GuiContents(ConfigurationSection section, Player player) {
        this.GUI_ITEM_ARGS = section.getConfigurationSection("args");
        this.ACTION_SECTION = section.getConfigurationSection("args.actions");
        this.PLACEHOLDERS = section.getConfigurationSection("args.placeholders");
        this.player = player;

        applyPlaceholders();
        guiItemContent();
        translatePlaceholders();

    }

    private void applyPlaceholders() {
        for(String placeholder : PLACEHOLDERS.getKeys(true)) {
            placeholders.put(
                    placeholder,
                    PlaceholderAPI.setPlaceholders(
                            player,
                            Objects.requireNonNull(PLACEHOLDERS.getString(placeholder))
                    ));
        }
    }

    private void translatePlaceholders() {

        for(String key : placeholders.keySet()) {
            displayName = LightEconomy.instance.colorTranslation.convertToString(
                    displayName.replace("#" + key + "#", placeholders.get(key)));

            List<String> translatedLore = new ArrayList<>();
            for(String line : lore) {
                Component text = LightEconomy.instance.colorTranslation.universalColor(line, player);
                String translated = LegacyComponentSerializer.legacySection().serialize(text);
                translatedLore.add(translated.replace("#" + key + "#", placeholders.get(key)));
            }

            this.lore = translatedLore;
        }

    }

    private void guiItemContent() {

        item = GUI_ITEM_ARGS.getString("item");
        displayName = GUI_ITEM_ARGS.getString("display-name");
        lore = GUI_ITEM_ARGS.getStringList("lore");
        itemPosX = GUI_ITEM_ARGS.getInt("item-pos-x");
        itemPosY = GUI_ITEM_ARGS.getInt("item-pos-y");

    }

    public ItemStack getGuiItem() {

        ItemStack itemStack = new ItemStack(Material.STONE, 1);

        String[] splitItem = item.split(" ");
        Material material = Material.getMaterial(splitItem[0].toUpperCase());

        if(NumberFormatter.isNumber(splitItem[1])) {
            itemStack.setAmount(Integer.parseInt(splitItem[1]));
        }

        if(material != null) {
            itemStack.setType(material);

            if(material.equals(Material.PLAYER_HEAD)) {
                playerHead = true;
            }

        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta == null) {
            return null;
        }

        itemMeta.setDisplayName(LightEconomy.instance.colorTranslation.convertToString(displayName));

        for(String split : splitItem) {

            if(split.equalsIgnoreCase("model-data")) {
                String[] splitModelData = split.split(":");
                if(NumberFormatter.isNumber(splitModelData[1])) {
                    itemMeta.setCustomModelData(Integer.parseInt(splitModelData[1]));
                }
            }

            if(split.equalsIgnoreCase("hide_enchants")) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            if(split.equalsIgnoreCase("hide_attributes")) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }

        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public boolean isPlayerHead() {
        return playerHead;
    }
    public int getItemPosX() {
        return itemPosX;
    }
    public int getItemPosY() {
        return itemPosY;
    }
}
