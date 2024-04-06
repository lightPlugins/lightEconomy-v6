package io.lightplugins.economy.util.content.watcher;

import io.lightplugins.economy.util.NumberFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class GuiItem {

    private Material material;
    private String displayName;
    private int amount;
    private int modelData;
    private boolean glow = false;
    private boolean hideEnchants = false;
    private boolean hideFlags = false;
    private int posX;
    private int posY;
    private List<String> lore = new ArrayList<>();


    public GuiItem(ConfigurationSection section) {

        this.posX = section.getInt("args.item-pos-x");
        this.posY = section.getInt("args.item-pos-y");

        //  handle the LORE param

        for(String lore : section.getStringList("args.lore")) {
            TextComponent textComponent = Component.text(lore);
            this.lore.add(textComponent.content());
        }


        String itemData = section.getString("args.item");
        String[] item = itemData.split(" ");

        //  handle the item data input for material, amount, model-data, glow hide_flags and hide_enchants

        //  handle the MATERIAL param

        try {
            this.material = Material.valueOf(item[0]);
        } catch (IllegalStateException ignored) {
            this.material = Material.STONE;
        }

        //  handle the AMOUNT param

        if(NumberFormatter.isNumber(item[1])) {
            this.amount = Integer.parseInt(item[1]);
        } else {
            this.amount = 1;
        }


        for(String split : item) {

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
