package io.lightplugins.economy.eco.config;

import io.lightplugins.economy.eco.LightEco;
import io.lightplugins.economy.util.NumberFormatter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

public class SettingParams {

    private LightEco lightEco;
    private final String defaultCurrency = "default-currency.";

    public SettingParams(LightEco lightEco) {
        this.lightEco = lightEco;
    }


    public String getModuleLanguage() {
        return lightEco.getSettings().getConfig().getString("module-language");
    }


    public DefaultCurrency defaultCurrency() {
        return new DefaultCurrency();
    }
    public SettingWrapper mainSettings() { return new SettingWrapper(); }


    public class SettingWrapper {
        public SimpleDateFormat getDateFormat() {
            String result = lightEco.getSettings().getConfig().getString("module-language");
            return result != null ? new SimpleDateFormat(result) : new SimpleDateFormat("dd:MM:yyyy");
        }
    }


    public class DefaultCurrency {
        public BigDecimal getStartBalance() {
            double startBalance = lightEco.getSettings().getConfig().getDouble(defaultCurrency + "start-balance");
            return NumberFormatter.formatBigDecimal(BigDecimal.valueOf(startBalance));
        }
        public boolean firstJoinMessageEnabled() {
            return lightEco.getSettings().getConfig().getBoolean(defaultCurrency + "enable-first-join-message");
        }
        public String currencyPluralName() {
            return lightEco.getSettings().getConfig().getString(defaultCurrency + "currency-name-plural");
        }
        public String currencySingularName() {
            return lightEco.getSettings().getConfig().getString(defaultCurrency + "currency-name-singular");
        }
        public Integer fractionalDigits() {
            return lightEco.getSettings().getConfig().getInt(defaultCurrency + "fractional-digits");
        }
        public double maxPocketBalance() {
            return lightEco.getSettings().getConfig().getDouble(defaultCurrency + "max-pocket-balance");
        }
        public List<String> getExcludeTop() {
            return lightEco.getSettings().getConfig().getStringList("exclude-from-top-list");
        }
    }
}
