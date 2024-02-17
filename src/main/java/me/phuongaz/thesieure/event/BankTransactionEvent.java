package me.phuongaz.thesieure.event;

import lombok.Getter;
import me.phuongaz.thesieure.api.type.HistoryTransaction;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BankTransactionEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final HistoryTransaction historyTransaction;

    @Getter
    private final String playerName;

    public BankTransactionEvent(HistoryTransaction historyTransaction, String playerName) {
        this.historyTransaction = historyTransaction;
        this.playerName = playerName;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return  handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
