package io.lightplugins.economy.util.content.models;

import java.util.ArrayList;
import java.util.List;

public class GuiItemModel {

    private String[] itemData;
    private String headData;
    private String displayName;
    private List<String> lore = new ArrayList<>();
    private int itemPosX;
    private int itemPosY;

    public String[] getItemData() {
        return itemData;
    }

    public void setItemData(String itemData) {
        this.itemData = itemData.split(" ");
    }

    public void setItem(String[] item) {
        this.itemData = item;
    }

    public String getHeadData() {
        return headData;
    }

    public void setHeadData(String headData) {
        this.headData = headData;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public int getItemPosX() {
        return itemPosX;
    }

    public void setItemPosX(int itemPosX) {
        this.itemPosX = itemPosX;
    }

    public int getItemPosY() {
        return itemPosY;
    }

    public void setItemPosY(int itemPosY) {
        this.itemPosY = itemPosY;
    }
}
