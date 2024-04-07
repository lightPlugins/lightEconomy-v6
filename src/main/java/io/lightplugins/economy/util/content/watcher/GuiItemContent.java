package io.lightplugins.economy.util.content.watcher;

import io.lightplugins.economy.LightEconomy;
import io.lightplugins.economy.util.NumberFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GuiItemContent {

    private Material material;
    private final String displayName;
    private final int amount;
    private int modelData;
    private boolean glow = false;
    private boolean hideEnchants = false;
    private boolean hideFlags = false;
    private final int posX;
    private final int posY;
    private final List<String> lore = new ArrayList<>();

    public int getPosX() {
        return this.posX;
    }

    public int getPosY() {
        return this.posY;
    }

    public ItemStack getGuiItem() {
        ItemStack itemStack = new ItemStack(this.material, this.amount);

        if(itemStack.getItemMeta() == null) {
            itemStack = new ItemStack(Material.STONE, 1);
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta == null) {
            throw new RuntimeException("Provided GuiITEM has no ItemMeta! Check your inventory config.");
        }

        itemMeta.setDisplayName(this.displayName);
        itemMeta.setLore(this.lore);
        itemMeta.setCustomModelData(this.modelData);

        itemMeta.setCustomModelData(this.modelData);
        if(this.glow) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        }

        if(this.hideEnchants) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        if(this.hideFlags) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


    public GuiItemContent(Map<String, Object> section) {

        String id = (String) section.get("id");
        Map<String, Object> args = (Map<String, Object>) section.get("args");
        String data = (String) args.get("data");
        String item = (String) args.get("item");
        String displayName = (String) args.get("display-name");
        int posX = (int) args.get("item-pos-x");
        int posY = (int) args.get("item-pos-y");
        List<String> lore = (List<String>) args.get("lore");

        this.posX = posX;
        this.posY = posY;

        //  handle the LORE param

        for(String loreLine : lore) {
            Component text = LightEconomy.instance.colorTranslation.universalColor(loreLine);
            String translated = LegacyComponentSerializer.legacySection().serialize(text);
            this.lore.add(translated);
        }

        //  handle the DISPLAY-NAME param
        Component text = LightEconomy.instance.colorTranslation.miniMessage(displayName);
        this.displayName = LegacyComponentSerializer.legacySection().serialize(text);


        String[] itemArray = item.split(" ");

        //  handle the item data input for material, amount, model-data, glow hide_flags and hide_enchants

        //  handle the MATERIAL param

        try {
            this.material = Material.valueOf(itemArray[0].toUpperCase());
        } catch (IllegalStateException ignored) {
            this.material = Material.STONE;
        }

        //  handle the AMOUNT param

        if(NumberFormatter.isNumber(itemArray[1])) {
            this.amount = Integer.parseInt(itemArray[1]);
        } else {
            this.amount = 1;
        }


        for(String split : itemArray) {

            //  handle the MODEL-DATA

            if(split.contains("model-data")) {
                String [] modelSplit = split.split(":");
                if(NumberFormatter.isNumber(modelSplit[1])) {
                    this.modelData = Integer.parseInt(modelSplit[1]);
                } else {
                    this.modelData = 0;
                }
            }

            //  handle the GLOW param

            if(split.equalsIgnoreCase("glow")) {
                this.glow = true;
            }

            //  handle the HIDE_FLAGS param

            if(split.equalsIgnoreCase("hide_flags")) {
                this.hideFlags = true;
            }

            //  handle the HIDE_ENCHANTS param

            if(split.equalsIgnoreCase("hide_enchants")) {
                this.hideEnchants = true;
            }
        }
    }
}
