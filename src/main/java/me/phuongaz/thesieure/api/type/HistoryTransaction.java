package me.phuongaz.thesieure.api.type;

import com.google.gson.Gson;
import lombok.Data;

@Data
public class HistoryTransaction {

    private String raw_data;
    private String id;
    private String arrangementId;
    private String reference;
    private String description;
    private String bookingDate;
    private String valueDate;
    private int amount;
    private String currency;
    private String creditDebitIndicator;
    private String runningBalance;

    public HistoryTransaction(String raw_data) {
        this.raw_data = raw_data;
        this.parseHistory();
    }

    public void parseHistory() {
        Gson gson = new Gson();
        HistoryData data = gson.fromJson(raw_data, HistoryData.class);
        this.id = data.getId();
        this.arrangementId = data.getArrangementId();
        this.reference = data.getReference();
        this.description = data.getDescription();
        this.bookingDate = data.getBookingDate();
        this.valueDate = data.getValueDate();
        this.amount = Integer.parseInt(data.getAmount());
        this.currency = data.getCurrency();
        this.creditDebitIndicator = data.getCreditDebitIndicator();
        this.runningBalance = data.getRunningBalance();
    }

    public HistoryData getHistoryData() {
        Gson gson = new Gson();
        return gson.fromJson(raw_data, HistoryData.class);
    }

    @Data
    private static class HistoryData {
        private String id;
        private String arrangementId;
        private String reference;
        private String description;
        private String bookingDate;
        private String valueDate;
        private String amount;
        private String currency;
        private String creditDebitIndicator;
        private String runningBalance;
    }
}