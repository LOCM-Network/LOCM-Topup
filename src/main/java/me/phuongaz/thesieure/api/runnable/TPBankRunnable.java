package me.phuongaz.thesieure.api.runnable;


import com.google.gson.Gson;
import me.phuongaz.thesieure.Loader;
import me.phuongaz.thesieure.api.TPBankAPI;
import me.phuongaz.thesieure.api.type.HistoryTransaction;
import me.phuongaz.thesieure.event.BankTransactionEvent;
import me.phuongaz.thesieure.utils.ParseUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class TPBankRunnable implements Runnable {

    private HistoryTransaction[] cached_data = new HistoryTransaction[0];
    private final TPBankAPI api;

    public TPBankRunnable(Plugin plugin) {
        this.api = Loader.getInstance().getTpBankAPI();
    }

    @Override
    public void run() {
        String token = loadAccessToken(false);
        if (token == null) {
            Loader.getInstance().getLogger().warning("Failed to load access token");
            return;
        }

        String historyRaw = api.getHistoryRaw(token, api.getAccountNumber());
        if (historyRaw != null) {
            Gson gson = new Gson();
            HistoryTransaction[] historyData = gson.fromJson(historyRaw, HistoryTransaction[].class);
            if (historyData == null || historyData.length == 0) return;

            if (cached_data.length != 0) {
                for (HistoryTransaction newData : historyData) {
                    boolean isNew = true;
                    for (HistoryTransaction cachedDatum : cached_data) {
                        if (newData.getId().equals(cachedDatum.getId())) {
                            isNew = false;
                            break;
                        }
                    }
                    if (isNew) {
                        if (ParseUtils.compareTransactionFormat(newData.getDescription())) {
                            String name = ParseUtils.parseNameFromDescription(newData.getDescription());
                            if (name != null) {
                                String playerName = ParseUtils.parseNameFromDescription(newData.getDescription());
                                new BankTransactionEvent(newData, playerName).callEvent();
                            }
                        }
                    }
                }
            }
            cached_data = historyData;
        } else {
            loadAccessToken(true); // Token has expired
        }
    }

    private String loadAccessToken(boolean expired) {
        String token = api.getAccessToken();

        if (token.isEmpty() || expired) {
            String tokenRaw = Loader.getInstance().getTpBankAPI().getToken();
            if (tokenRaw != null) {
                Gson gson = new Gson();
                String accessToken = gson.fromJson(tokenRaw, AccessToken.class).getAccessToken();
                api.setAccessToken(accessToken);
                return accessToken;
            } else {
                return null;
            }
        }
        return token;
    }

    private static class AccessToken {
        private String access_token;

        public String getAccessToken() {
            return access_token;
        }
    }
}