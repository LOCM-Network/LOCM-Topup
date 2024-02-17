package me.phuongaz.thesieure;

import lombok.Getter;
import lombok.Setter;
import me.phuongaz.thesieure.api.TPBankAPI;
import me.phuongaz.thesieure.api.runnable.TPBankRunnable;
import me.phuongaz.thesieure.command.NapTheCommand;
import me.phuongaz.thesieure.provider.MongoDBProvider;
import me.phuongaz.thesieure.provider.Provider;
import me.phuongaz.thesieure.provider.SQLiteProvider;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Loader extends JavaPlugin {

    private @Getter
    static Loader instance;

    private @Getter @Setter
    String partnerID;

    private @Getter @Setter
    String partnerKey;

    private @Getter @Setter
    Provider provider;

    private @Getter String driver;

    private @Getter SQLiteProvider sqLiteProvider;
    private @Getter MongoDBProvider mongoDBProvider;

    private TPBankAPI tpBankAPI;

    @Override
    public void onLoad() {
        saveDefaultConfig();
        instance = this;
    }

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig();
        partnerID = config.getString("partnerID");
        partnerKey = config.getString("partnerKey");
        driver = config.getString("driver");
        tpBankAPI = new TPBankAPI("", "", "", 200);

        if(Objects.equals(config.getString("provider"), "mongodb")) {
            mongoDBProvider = new MongoDBProvider();
            mongoDBProvider.connect();
            provider = mongoDBProvider;
        } else {
            sqLiteProvider = new SQLiteProvider();
            sqLiteProvider.connect();
            provider = sqLiteProvider;
        }

        Objects.requireNonNull(getCommand("napthe")).setExecutor(new NapTheCommand());
        getServer().getPluginManager().registerEvents(new EventListener(), this);

        int periodTicks = 20 * 60 * 5; // 5 minutes
        if(config.getBoolean("bank_transaction")) {
            getServer().getScheduler().runTaskTimerAsynchronously(this, new TPBankRunnable(this), 0, periodTicks);
        }
    }

    public TPBankAPI getTpBankAPI() {
        return tpBankAPI;
    }

    @Override
    public void onDisable() {
        if(provider != null) {
            provider.close();
        }
    }


}
