package me.phuongaz.thesieure.driver;

import me.phuongaz.thesieure.card.Card;
import me.phuongaz.thesieure.driver.response.Response;
import me.phuongaz.thesieure.driver.response.ThesieureResponse;

public class ThesieureDriver extends Driver{

    public ThesieureDriver(Card card) {
        super(card);
    }

    @Override
    public String getDriverUrl() {
        return "https://thesieure.com/chargingws/v2";
    }

    @Override
    public Response createResponse(String response) {
        return new ThesieureResponse(response, getCard());
    }
}
