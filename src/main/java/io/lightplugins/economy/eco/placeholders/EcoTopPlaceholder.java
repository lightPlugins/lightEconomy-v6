package io.lightplugins.economy.eco.placeholders;

import io.lightplugins.economy.eco.LightEco;
import io.lightplugins.economy.util.NumberFormatter;
import io.lightplugins.economy.util.SubPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EcoTopPlaceholder extends SubPlaceholder {

    // Placeholder for the top list
    //  %lighteconomy_eco_top_<number>_<name>%
    //  %lighteconomy_eco_top_<number>_<value>%

    @Override
    public String onRequest(OfflinePlayer player, String params) {

        String[] parts = params.split("_");
        if (parts.length != 4) {
            return "error";
        }

        String module = parts[0];
        String subModule = parts[1];
        String place = parts[2];
        String param = parts[3];

        if(!module.equalsIgnoreCase("eco")) {
            return "error1";
        }

        if(!subModule.equalsIgnoreCase("top")) {
            return "error2";
        }

        if(!NumberFormatter.isNumber(place)) {
            return "error3";
        }

        int placeInt = Integer.parseInt(place);
        if(placeInt < 1 || placeInt > 10) {
            return "error4";
        }

        // Get the top list from the economy
        CompletableFuture<Map<UUID, BigDecimal>> topListFuture = LightEco.instance.getQueryManager().getTopList();
        Map<UUID, BigDecimal> topList = topListFuture.join(); // TODO: Cache these values in a ConcurrentHashMap

        // Get the list of entries sorted by value
        List<Map.Entry<UUID, BigDecimal>> sortedEntries = new ArrayList<>(topList.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue());

        // Check if the requested place is valid
        if (placeInt > sortedEntries.size()) {
            return "empty place";
        }

        // Get the requested entry
        Map.Entry<UUID, BigDecimal> entry = sortedEntries.get(placeInt - 1);

        // Return the requested value
        if (param.equalsIgnoreCase("name")) {
            return Bukkit.getOfflinePlayer(entry.getKey()).getName();
        } else if (param.equalsIgnoreCase("value")) {
            return NumberFormatter.formatBigDecimal(entry.getValue()).toString();
        } else {
            return "error5";
        }
    }
}
