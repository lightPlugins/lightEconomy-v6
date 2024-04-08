package io.lightplugins.economy.eco.inventories;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.context.OpenContext;
import me.devnatan.inventoryframework.context.RenderContext;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BaltopInventory extends View {

    @Override
    public void onInit(ViewConfigBuilder config) {
        config.title("Pet Shop").size(4).cancelOnClick();
    }

    @Override
    public void onOpen(OpenContext open) {
        final Player player = open.getPlayer();
        open.modifyConfig()
                .title("Hi, " + player.getName() + "!");
    }

    @Override
    public void onFirstRender(RenderContext render) {
        //render.slot(4, 3, new ItemStack(Material.GOLD_INGOT)).closeOnClick();
        render.firstSlot().renderWith(() -> new ItemStack(Material.DIAMOND, 1));
        //render.availableSlot(new ItemStack(Material.GOLDEN_AXE));
    }

}
