package io.lightplugins.economy.util;
import io.lightplugins.economy.LightEconomy;
import io.lightplugins.economy.eco.LightEco;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MessageSender {

    public void sendPlayerMessage(String message, Player player) {
        Bukkit.getScheduler().runTask(LightEconomy.instance, () -> {
            Audience audience = (Audience) player;
            Component component = LightEconomy.instance.colorTranslation.universalColor(
                    LightEco.getMessageParams().prefix() + message, player);
            audience.sendMessage(component);
        });
    }
}
