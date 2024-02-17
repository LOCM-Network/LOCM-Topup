package me.phuongaz.thesieure.card;

import lombok.Getter;
import me.phuongaz.thesieure.Loader;
import me.phuongaz.thesieure.card.type.CardType;
import me.phuongaz.thesieure.driver.Driver;
import me.phuongaz.thesieure.driver.ThesieureDriver;

import java.security.MessageDigest;
import java.util.Map;

@Getter
public class Card {

    private final String serial;
    private final String pin;

    private final CardType type;

    private final String player;
    private final String requestID;
    private final int amount;

    public Card(String serial, String pin, int amount, CardType type, String player) {
        this.serial = serial;
        this.pin = pin;
        this.amount = amount;
        this.type = type;
        this.player = player;
        this.requestID = System.currentTimeMillis() + "-" + player;
    }

    /*	public Map<String, String> createDataPost(String command) {
		Map<String, String> data = new HashMap<>();
		data.put("telco",getTelco());
		data.put("code", getPin());
		data.put("serial", getSerial());
		data.put("amount", getAmount());
		data.put("request_id", getRequestId());
		data.put("partner_id", getPartnerId());
		data.put("sign", createSign());
		data.put("command", command);
		return data;
	}*/
    public Map<String, String> createDataPost(String command) {
        return Map.of(
                "telco", type.toString(),
                "code", pin,
                "serial", serial,
                "amount", String.valueOf(amount),
                "request_id", requestID,
                "partner_id", Loader.getInstance().getPartnerID(),
                "sign", createSignature(),
                "command", command
        );
    }

    public String createSignature() {
        String partnerKey = Loader.getInstance().getPartnerKey();

        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest((partnerKey + getPin() + getSerial()).getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toString((0xff & b) + 0x100, 16).substring(1);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Driver getDriver() {
        String driverName = Loader.getInstance().getDriver();
        Driver driver = null;
        switch (driverName) {
            case "thesieure":
                driver = new ThesieureDriver(this);
                break;
            //TODO: Add more drivers here
        }
        return driver;
    }

    public static Card makeCard(String serial, String pin, int amount, CardType type, String player) {
        return new Card(serial, pin, amount, type, player);
    }
}
