package me.phuongaz.thesieure.provider;

import java.util.List;

abstract public class Provider {

    abstract public void connect();

    abstract public void insertCard(String player, String serial, String pin, int amount);

    abstract public void insertPending(String player, String serial, String pin, int amount);

    abstract public void insertBank(String player, int amount);

    abstract public List<String> getPending(String player);

    abstract public void removePending(String serial, String pin);

    abstract public void close();
}
