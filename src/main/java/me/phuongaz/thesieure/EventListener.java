package me.phuongaz.thesieure;

import me.phuongaz.thesieure.api.type.HistoryTransaction;
import me.phuongaz.thesieure.card.Card;
import me.phuongaz.thesieure.event.BankTransactionEvent;
import me.phuongaz.thesieure.event.DonateEvent;
import me.phuongaz.thesieure.provider.Provider;
import me.phuongaz.thesieure.utils.ParseUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class EventListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        String name = event.getPlayer().getName();
        Provider provider = Loader.getInstance().getProvider();
        List<String> pending = provider.getPending(name);
        if(pending == null) return;
        if(pending.size() > 0) {
            for(String card : pending) {
                String[] data = card.split(":");
                provider.insertCard(name, data[0], data[1], Integer.parseInt(data[2]));
                provider.removePending(data[0], data[1]);
                int ratio = ParseUtils.parseAmountByRatio(Integer.parseInt(data[2]), "card");
                handleReward(event.getPlayer(), ratio);
                Bukkit.broadcast(
                        Component.text("§a[Donate] §f" + name + " §7đã nạp §f" + data[2] + " VND§7 bằng thẻ cào!")
                );
            }
        }
    }

    @EventHandler
    public void onCardDonate(DonateEvent event) {
        String name = event.getPlayerName();
        Card card = event.getResponse().getCard();
        Provider provider = Loader.getInstance().getProvider();
        if(Loader.getInstance().getServer().getPlayerExact(name) != null) {
            Player player = Loader.getInstance().getServer().getPlayerExact(name);
            int ratio = ParseUtils.parseAmountByRatio(card.getAmount(), "card");
            handleReward(player, ratio);
            provider.insertCard(
                    card.getPlayer(),
                    card.getSerial(),
                    card.getPin(),
                    card.getAmount()
            );
            Bukkit.broadcast(
                    Component.text("§a[Donate] §f" + name + " §7đã nạp §f" + card.getAmount() + " VND§7 bằng thẻ cào!")
            );
        }else{
            provider.insertPending(
                    card.getPlayer(),
                    card.getSerial(),
                    card.getPin(),
                    card.getAmount());
        }
    }

    @EventHandler
    public void onBankTransaction(BankTransactionEvent event) {
        String name = event.getPlayerName();
        HistoryTransaction transaction = event.getHistoryTransaction();
        if(Loader.getInstance().getServer().getPlayerExact(name) != null) {
            Player player = Loader.getInstance().getServer().getPlayerExact(name);
            int ratio = ParseUtils.parseAmountByRatio(transaction.getAmount(), "bank");
            handleReward(player, ratio);
        }

        Loader.getInstance().getProvider().insertBank(
                name,
                transaction.getAmount()
        );

        Bukkit.broadcast(
                Component.text("§a[Donate] §f" + name + " §7đã nạp §f" + transaction.getAmount() + " VND§7 bằng phương thức chuyển khoản!")
        );
    }

    private void handleReward(Player player, int amount) {
        FileConfiguration config = Loader.getInstance().getConfig();
        List<String> commands = config.getStringList("reward-commands");
        for (String command : commands) {
            command = command.replace("%player%", player.getName());
            command = command.replace("%amount%", String.valueOf(amount));
            Loader.getInstance().getServer().dispatchCommand(Loader.getInstance().getServer().getConsoleSender(), command);
        }
    }
}
