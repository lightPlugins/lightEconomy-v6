package io.lightplugins.economy.eco.api;

import io.lightplugins.economy.eco.LightEco;
import io.lightplugins.vaulty.api.economy.VaultyEconomy;

public class LightEcoAPI {

    private final LightEco economy;

    public LightEcoAPI(LightEco economy) {
        this.economy = economy;
    }

    public VaultyEconomy economy() {
        return LightEco.economyVaultyService;
    }

}
