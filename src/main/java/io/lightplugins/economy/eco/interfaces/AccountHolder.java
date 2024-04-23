package io.lightplugins.economy.eco.interfaces;

import io.lightplugins.economy.util.NumberFormatter;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountHolder {

    private BigDecimal balance;
    private UUID uuid;

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public double getFormattedBalance() {
        return NumberFormatter.formatDouble(balance);
    }

}
