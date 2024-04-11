package io.lightplugins.economy.util.handler;

import io.lightplugins.economy.LightEconomy;
import org.bukkit.entity.Player;

import java.util.List;

public class ActionHandler {

    private final List<String> actions;
    private final Player player;

    public ActionHandler(List<String> actions, Player player) {
        this.actions = actions;
        this.player = player;
    }

    private void performActions() {

        for(String singleAction : actions) {

            String[] splitAction = singleAction.split(";");

            switch (splitAction[0]) {
                case "message" ->
                        player.sendMessage(LightEconomy.instance.colorTranslation.convertToString(splitAction[1], player));
                case "close_inventory" ->
                        player.closeInventory();
            }
        }
    }
}
