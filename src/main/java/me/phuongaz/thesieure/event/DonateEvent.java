package me.phuongaz.thesieure.event;

import lombok.Getter;
import me.phuongaz.thesieure.driver.response.Response;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DonateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final String playerName;
    @Getter
    private final Response response;

    public DonateEvent(String playerName, Response response) {
        this.playerName = playerName;
        this.response = response;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
