package io.lightplugins.economy.util;

import org.bukkit.OfflinePlayer;

public abstract class SubPlaceholder {

    public abstract String onRequest(OfflinePlayer player, String params);
}
