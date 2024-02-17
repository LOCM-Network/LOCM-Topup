package me.phuongaz.thesieure.command;

import me.phuongaz.thesieure.card.Card;
import me.phuongaz.thesieure.card.type.CardType;
import me.phuongaz.thesieure.driver.Driver;
import me.phuongaz.thesieure.driver.response.Response;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class NapTheCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(args.length == 0) {
            sendHelp(sender);
            return true;
        }

        if(args.length < 4) {
            sender.sendMessage("§cVui lòng nhập đúng cú pháp!");
            sendHelp(sender);
            return true;
        }

        String type = args[0];
        String serial = args[1];
        String pin = args[2];
        String amount = args[3];

        if(!CardType.isCardType(type)) {
            sender.sendMessage("§cLoại thẻ không hợp lệ!");
            String[] cardTypes = CardType.getCardTypes();
            sender.sendMessage("§eCác loại thẻ hợp lệ: " + String.join(", ", cardTypes));
            sendHelp(sender);
            return true;
        }

        if(!amount.matches("\\d+")) {
            sender.sendMessage("§cTrị giá thẻ §e"+ amount +"§c không hợp lệ!");
            sendHelp(sender);
            return true;
        }

        Card card = Card.makeCard(serial, pin, Integer.parseInt(amount), CardType.fromString(type), sender.getName());
        Driver driver = card.getDriver();
        if(driver == null) {
            sender.sendMessage("§cHệ thống đang bảo trì!");
            return true;
        }

        sender.sendMessage("§eĐang xử lý thẻ vui lòng chờ giây lát");
        CompletableFuture<Response> response = driver.request();

        response.thenAccept(res -> {
            if(res.isSuccess()) {
                sender.sendMessage(res.errorToString());
            } else if(res.isPending()) {
                sender.sendMessage("§eThẻ đang xử lý vui lòng chờ ít phút");
            } else {
                sender.sendMessage(res.errorToString());
            }
        });
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§e/napthe <loại thẻ> <serial> <mã thẻ> <trị giá>");
    }
}
