package io.lightplugins.economy.eco.models;

import java.math.BigDecimal;
import java.util.UUID;

public class EconomyProfile {

    private final BigDecimal balance;
    private final UUID uuid;

    public EconomyProfile(UUID uuid, BigDecimal balance) {
        this.uuid = uuid;
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public UUID getUuid() {
        return uuid;
    }
}
