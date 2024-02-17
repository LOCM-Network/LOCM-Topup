package me.phuongaz.thesieure.utils;

import me.phuongaz.thesieure.Loader;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ParseUtils {

    public static String parseNameFromRequestID(String requestID) {
        int p = requestID.indexOf("-");
        return requestID.substring(0, p);
    }

    public static boolean compareTransactionFormat(String description) {
        FileConfiguration config = Loader.getInstance().getConfig();
        String transactionFormat = config.getString("transaction-format");

        if(transactionFormat == null) return false;

        String[] descriptionSplit = description.split(" ");
        String[] transactionFormatSplit = transactionFormat.split(" ");

        if(descriptionSplit.length != transactionFormatSplit.length) return false;

        return descriptionSplit[0].equals(transactionFormatSplit[0]);
    }

    public static String parseNameFromDescription(String description) {
        FileConfiguration config = Loader.getInstance().getConfig();
        String transactionFormat = config.getString("transaction-format");

        if (transactionFormat == null) return null;

        String[] descriptionSplit = description.split(" ");
        String[] transactionFormatSplit = transactionFormat.split(" ");

        if (descriptionSplit.length != transactionFormatSplit.length) return null;
        for (int i = 0; i < transactionFormatSplit.length; i++) {
            if (transactionFormatSplit[i].equals("{name}")) {
                return descriptionSplit[i];
            }
        }
        return null;
    }

    public static int parseAmountByRatio(int amount, String type) {
        FileConfiguration config = Loader.getInstance().getConfig();
        try {
            double amountRatio = config.getDouble("ratio." + type);
            return (int) (amount * amountRatio);
        } catch (NullPointerException e) {
            return (int) (amount * 1.5);
        }
    }
}
