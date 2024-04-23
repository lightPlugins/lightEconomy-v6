package io.lightplugins.economy;

import com.zaxxer.hikari.HikariDataSource;
import io.lightplugins.economy.bank.LightBank;
import io.lightplugins.economy.eco.LightEco;
import io.lightplugins.economy.eco.api.LightEcoAPI;
import io.lightplugins.economy.eco.placeholders.EcoTopPlaceholder;
import io.lightplugins.economy.util.ColorTranslation;
import io.lightplugins.economy.util.DebugPrinting;
import io.lightplugins.economy.util.MessageSender;
import io.lightplugins.economy.util.SubPlaceholder;
import io.lightplugins.economy.util.database.SQLDatabase;
import io.lightplugins.economy.util.database.impl.MySQLDatabase;
import io.lightplugins.economy.util.database.impl.SQLiteDatabase;
import io.lightplugins.economy.util.database.model.ConnectionProperties;
import io.lightplugins.economy.util.database.model.DatabaseCredentials;
import io.lightplugins.economy.util.interfaces.LightModule;
import io.lightplugins.economy.util.manager.FileManager;
import io.lightplugins.economy.util.manager.MultiFileManager;
import io.lightplugins.economy.util.manager.PlaceholderManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LightEconomy extends JavaPlugin {


    public static LightEconomy instance;

    private LightEco lightEco;
    private LightBank lightBank;

    private LightEcoAPI lightEcoAPI;

    private Map<String, LightModule> modules = new HashMap<>();
    private final ArrayList<SubPlaceholder> subPlaceholders = new ArrayList<>();

    private static MessageSender messageSender;
    public static FileManager database;
    public ColorTranslation colorTranslation;
    private static DebugPrinting debugPrinting;
    private SQLDatabase pluginDatabase;

    public static boolean isTowny = false;
    public static boolean isPlaceholderAPI = false;

    public HikariDataSource ds;

    public final static String consolePrefix = "§r[light§eEconomy§r] §r";

    private FileManager settings;


    public void onLoad() {

        instance = this;
        debugPrinting = new DebugPrinting();
        colorTranslation = new ColorTranslation();
        checkForHooks();
        this.modules = new LinkedHashMap<>();
        database = createNewFile("settings.yml", true);

        if(!this.initDatabase()) {
            getDebugPrinting().print("§4Could not connect to the database. Please check your database.yml");
            getDebugPrinting().print("§4LightEconomy is shutting down... ");
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    public void onEnable() {

        Bukkit.getConsoleSender().sendMessage("\n " +
                " §r_      _____ _____ _    _ _______ §e______ _____ ____  _   _  ____  __  ____     __\n" +
                " §r| |    |_   _/ ____| |  | |__   __§e|  ____/ ____/ __ \\| \\ | |/ __ \\|  \\/  \\ \\   / /\n" +
                " §r| |      | || |  __| |__| |  | |  §e| |__ | |   | |  | |  \\| | |  | | \\  / |\\ \\_/ / \n" +
                " §r| |      | || | |_ |  __  |  | |  §e|  __|| |   | |  | | . ` | |  | | |\\/| | \\   /  \n" +
                " §r| |____ _| || |__| | |  | |  | |  §e| |___| |___| |__| | |\\  | |__| | |  | |  | |   \n" +
                " §r|______|_____\\_____|_|  |_|  |_|  §e|______\\_____\\____/|_| \\_|\\____/|_|  |_|  |_|" +
                "\n\n" + ChatColor.RESET +
                "      Version: §e6.0.0   §rAuthor: §elightPlugins\n" +
                "      §rThank you for using lightEconomy. If you came in trouble feel free to join\n" +
                "      my §eDiscord §rserver: https://discord.gg/G2EuzmSW\n");

        debugPrinting.print("Loading lightEconomy modules...");
        messageSender = new MessageSender();
        initModules();
        loadModules();
        initSubPlaceHolders();
        registerPlaceHolders();

        this.lightEcoAPI = new LightEcoAPI(LightEco.instance);

    }

    public void onDisable() {

        Iterator<LightModule> iterator = this.modules.values().iterator();

        while(iterator.hasNext()) {
            this.unloadModule(iterator.next());
            iterator.remove();
        }

        if(this.pluginDatabase != null) {
            SQLDatabase sqlDatabase = this.pluginDatabase;
            sqlDatabase.close();
            getDebugPrinting().print("§4Database is closed");
        }

        super.onDisable();

    }

    private void loadModules() {

        this.loadModule(lightEco, true);
        this.loadModule(lightBank, false);

    }

    private void loadModule(LightModule lightModule, boolean enable) {
        if(lightModule.isEnabled()) {
            debugPrinting.print("Module §e" + lightModule.getName() + "§r already loaded.");
            return;
        }
        debugPrinting.print("Module §e" + lightModule.getName() + "§r is" +
                (enable ? "§a activated" : "§c deactivated"));
        if(enable) { lightModule.enable();  }

    }

    private void unloadModule(LightModule lightModule) {
        if(!lightModule.isEnabled()) {
            return;
        }
        lightModule.disable();
        debugPrinting.print("Successfully unloaded module: §e" + lightModule.getName());
    }

    private void initModules() {

        this.lightEco = new LightEco();
        this.lightBank = new LightBank();

        this.modules.put(this.lightEco.getName(), this.lightEco);
        this.modules.put(this.lightBank.getName(), this.lightBank);
    }

    public FileManager selectLanguage(String languageName, String moduleName) {

        return switch (languageName) {
            case "de" -> {
                debugPrinting.print(
                        "Selected language for module " + moduleName + ": " + languageName);
                yield new FileManager(LightEconomy.instance, moduleName +
                        "/language/de.yml", true);
            }
            case "pl" -> {
                debugPrinting.print(
                        "Selected language for module " + moduleName + ": " + languageName);
                yield new FileManager(LightEconomy.instance, moduleName +
                        "/language/pl.yml", true);
            }
            default -> {
                debugPrinting.print(
                        "Selected language for module " + moduleName + ": " + languageName);
                yield new FileManager(LightEconomy.instance, moduleName +
                        "/language/en.yml", true);
            }
        };
    }

    private void checkForHooks() {
        debugPrinting.print("Checks for third party hooks");

        Plugin plugin = Bukkit.getPluginManager().getPlugin("Towny");
        if (plugin != null) {
            if(!plugin.getDescription().getVersion().equals("0.100.1.0")) {
                Bukkit.getConsoleSender().sendMessage(
                        "Towny is present but the version §e"
                                + plugin.getDescription().getVersion() +
                                "§r is not supported. Please update to version §e0.100.1.0 or higher");
            } else {
                getDebugPrinting().print("Towny is present and supported. Hooking into Towny");
                isTowny = true;
            }

        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getDebugPrinting().print("PlaceholderAPI is present. Hooking into PlaceholderAPI");
            isPlaceholderAPI = true;
        }

    }

    private boolean initDatabase() {
        try {
            String databaseType = database.getConfig().getString("storage.type");
            ConnectionProperties connectionProperties = ConnectionProperties.fromConfig(database.getConfig());

            if ("sqlite".equalsIgnoreCase(databaseType)) {
                this.pluginDatabase = new SQLiteDatabase(this, connectionProperties);
                getDebugPrinting().print("Using SQLite (local) database.");
            } else if ("mysql".equalsIgnoreCase(databaseType)) {
                DatabaseCredentials credentials = DatabaseCredentials.fromConfig(database.getConfig());
                this.pluginDatabase = new MySQLDatabase(this, credentials, connectionProperties);
                getDebugPrinting().print("Using MySQL (remote*) database.");
            } else {
                this.getLogger().warning(String.format("Error! Unknown database type: %s. Disabling plugin.", databaseType));
                this.getServer().getPluginManager().disablePlugin(this);
                return false;
            }

            this.pluginDatabase.connect();
        } catch (Exception e) {
            this.getLogger().warning("Could not maintain Database Connection. Disabling plugin.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @NotNull
    public List<FileConfiguration> getFileConfigs(String path) throws IOException {

        final List<FileConfiguration> fileConfigs = new ArrayList<>();

        getMultiFiles(path).forEach(singleFile -> {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(singleFile);
            fileConfigs.add(cfg);
        });

        return fileConfigs;
    }

    @NotNull
    public List<File> getMultiFiles(String path) throws IOException {
        // Add files from the MultiFileManager to the existing files list
        return new ArrayList<>(readMultiFiles(path).getFiles());
    }

    @NotNull
    public MultiFileManager readMultiFiles(String directoryPath) throws IOException {
        return new MultiFileManager(directoryPath);
    }

    @NotNull
    public FileManager createNewFile(String fileName, boolean loadDefaultsOneReload) {
        return new FileManager(this, fileName, loadDefaultsOneReload);
    }

    public static MessageSender getMessageSender() {
        return messageSender;
    }

    public static DebugPrinting getDebugPrinting() {
        return debugPrinting;
    }

    public SQLDatabase getConnection() {
        return this.pluginDatabase;
    }

    private void initSubPlaceHolders() {
        this.subPlaceholders.add(new EcoTopPlaceholder());
    }

    private void registerPlaceHolders() {
        PlaceholderManager placeholderManager = new PlaceholderManager(this.subPlaceholders);
        placeholderManager.register();
    }

    public LightEcoAPI getLightEcoAPI() {
        return this.lightEcoAPI;
    }
}