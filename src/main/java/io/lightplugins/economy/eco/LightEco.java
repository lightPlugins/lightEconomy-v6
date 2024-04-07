package io.lightplugins.economy.eco;

import io.lightplugins.economy.LightEconomy;
import io.lightplugins.economy.eco.commands.eco.EcoGiveCommand;
import io.lightplugins.economy.eco.commands.eco.EcoRemoveCommand;
import io.lightplugins.economy.eco.commands.eco.EcoSetCommand;
import io.lightplugins.economy.eco.commands.eco.EcoTopCommand;
import io.lightplugins.economy.eco.commands.main.ReloadCommand;
import io.lightplugins.economy.eco.config.MessageParams;
import io.lightplugins.economy.eco.config.SettingParams;
import io.lightplugins.economy.eco.implementer.events.CreatePlayerOnJoin;
import io.lightplugins.economy.eco.implementer.vault.VaultImplementer;
import io.lightplugins.economy.eco.implementer.vaulty.VaultyImplementer;
import io.lightplugins.economy.eco.manager.QueryManager;
import io.lightplugins.economy.util.SubCommand;
import io.lightplugins.economy.util.interfaces.LightModule;
import io.lightplugins.economy.util.manager.CommandManager;
import io.lightplugins.economy.util.manager.FileManager;
import io.lightplugins.economy.util.manager.MultiFileManager;
import io.lightplugins.vaulty.api.economy.VaultyEconomy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LightEco implements LightModule {

    public static LightEco instance;
    public boolean isModuleEnabled = false;
    public Economy economy = null;
    private QueryManager queryManager;
    public static VaultyEconomy economyVaultyService;
    public static Economy economyVaultService;

    public final String moduleName = "eco";
    public final String adminPerm = "lighteconomy." + moduleName + ".admin";
    public final static String tablePrefix = "lighteco_";
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();
    public static List<File> inventoriesFiles = new ArrayList<>();

    private SettingParams settingParams;
    private static MessageParams messageParams;

    public VaultyEconomy vaultyProvider;
    public VaultyImplementer vaultyImplementer;

    private net.milkbowl.vault.economy.Economy vaultProvider;
    private VaultImplementer vaultImplementer;

    private FileManager settings;
    private FileManager language;
    private FileManager baltop;
    private MultiFileManager multiFileManager;

    @Override
    public void enable() {
        LightEconomy.getDebugPrinting().print(
                "Starting the core module " + this.moduleName);
        instance = this;
        LightEconomy.getDebugPrinting().print(
                "Creating default files for core module " + this.moduleName);
        initFiles();
        this.settingParams = new SettingParams(this);
        LightEconomy.getDebugPrinting().print(
                "Selecting module language for core module " + this.moduleName);
        selectLanguage();
        messageParams = new MessageParams(language);
        LightEconomy.getDebugPrinting().print(
                "Registering subcommands for core module " + this.moduleName + "...");
        initSubCommands();
        this.isModuleEnabled = true;
        LightEconomy.getDebugPrinting().print(
                "Successfully started core module " + this.moduleName + "!");
        vaultyImplementer = new VaultyImplementer();
        vaultImplementer = new VaultImplementer();
        hookVault();

        if(!initDatabase()) {
            LightEconomy.getDebugPrinting().print("§4Failed to initialize start sequence while enabling module §c" + this.moduleName);
            disable();
        }


        registerEvents();
        RegisteredServiceProvider<VaultyEconomy> vaultyRSP =
                Bukkit.getServer().getServicesManager().getRegistration(VaultyEconomy.class);
        RegisteredServiceProvider<Economy> vaultRSP =
                Bukkit.getServer().getServicesManager().getRegistration(Economy.class);

        economyVaultyService = vaultyRSP.getProvider();
        economyVaultService = vaultRSP.getProvider();

        //getQueryManager().createEcoTable();

    }

    @Override
    public void disable() {
        this.isModuleEnabled = false;
        unhookVault();
        LightEconomy.getDebugPrinting().print("Disabled module " + this.moduleName);
    }

    @Override
    public void reload() {
        //initFiles();
        getSettings().reloadConfig(moduleName + "/settings.yml");
        LightEconomy.getDebugPrinting().print(moduleName + "/settings.yml");
        selectLanguage();
        LightEconomy.getDebugPrinting().print(moduleName + "/language/" + settingParams.getModuleLanguage() + ".yml");
        getLanguage().reloadConfig(moduleName + "/language/" + settingParams.getModuleLanguage() + ".yml");
        
        messageParams = new MessageParams(language);
        settingParams = new SettingParams(this);

    }

    @Override
    public boolean isEnabled() {
        return this.isModuleEnabled;
    }

    @Override
    public String getName() {
        return moduleName;
    }

    public FileManager getSettings() { return settings; }

    public FileManager getLanguage() { return language; }
    public FileManager getBaltop() { return baltop; }

    private void selectLanguage() {
        this.language = LightEconomy.instance.selectLanguage(settingParams.getModuleLanguage(), moduleName);
    }

    private void initFiles() {

        //  generate and load settings.yml from core module ECO

        this.settings = new FileManager(
                LightEconomy.instance, moduleName + "/settings.yml", true);

        //  generate and load /inventories/baltop.yml from core module ECO

        this.baltop = new FileManager(
                LightEconomy.instance, moduleName + "/inventories/baltop.yml", true);


        //  generate and load inventories from inventories folder

        try {
            this.multiFileManager = new MultiFileManager("plugins/lightEconomy/" + moduleName + "/inventories/");
            inventoriesFiles = multiFileManager.getFiles();
            LightEconomy.getDebugPrinting().print("Found §e" + inventoriesFiles.size() + "§f inventory file(s)");
            if(inventoriesFiles.size() > 0) {
                inventoriesFiles.forEach(singleFile -> {
                    LightEconomy.getDebugPrinting().print("- §e" + singleFile.getName());
                });
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to initialize inventory configs!", ex);
        }
    }

    private void initSubCommands() {
        PluginCommand ecoCommand = Bukkit.getPluginCommand("eco");
        subCommands.add(new EcoGiveCommand());
        subCommands.add(new EcoRemoveCommand());
        subCommands.add(new EcoSetCommand());
        subCommands.add(new ReloadCommand());
        subCommands.add(new EcoTopCommand());
        new CommandManager(ecoCommand, subCommands);

    }

    public QueryManager getQueryManager() { return this.queryManager; }

    private void hookVault() {
        this.vaultyProvider = vaultyImplementer;
        this.vaultProvider = vaultImplementer;
        Bukkit.getServicesManager().register(VaultyEconomy.class, vaultyProvider, LightEconomy.instance, ServicePriority.Highest);
        LightEconomy.getDebugPrinting().print(
                "Vaulty successfully hooked with highest priority into " + LightEconomy.instance.getName());
        Bukkit.getServicesManager().register(Economy.class, this.vaultProvider, LightEconomy.instance, ServicePriority.Highest);
        LightEconomy.getDebugPrinting().print(
                "Vault successfully hooked with highest priority into " + LightEconomy.instance.getName());


    }

    private void unhookVault() {
        Bukkit.getServicesManager().unregister(VaultyEconomy.class, this.vaultyProvider);
        Bukkit.getConsoleSender().sendMessage(
                "Vaulty successfully unhooked from " + LightEconomy.instance.getName());
        Bukkit.getServicesManager().unregister(Economy.class, this.vaultProvider);
        Bukkit.getConsoleSender().sendMessage(
                "Vault successfully unhooked from " + LightEconomy.instance.getName());
    }

    public void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new CreatePlayerOnJoin(), LightEconomy.instance);
    }

    public SettingParams getSettingParams() {
        return settingParams;
    }

    public static MessageParams getMessageParams() {
        return messageParams;
    }

    private boolean initDatabase() {
        this.queryManager = new QueryManager(LightEconomy.instance.getConnection());
        queryManager.createEcoTable();
        return true;
    }

    public MultiFileManager getMultiFileManager() {
        return multiFileManager;
    }

    public List<File> getInventoryFiles() {
        return multiFileManager.getFiles();
    }
}
