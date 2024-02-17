package me.phuongaz.thesieure.driver.response;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import me.phuongaz.thesieure.card.Card;

@Setter
@Getter
abstract public class Response {

    private String response;
    private int responseCode;
    private String responseMessage;
    private Card card;

    public Response(String response, Card card) {
        this.response = response;
        this.card = card;
        parseResponse();
    }

    public void parseResponse() {
        Gson gson = new Gson();
        ResponseParser parser = gson.fromJson(response, ResponseParser.class);
        this.setResponseCode(parser.status);
        this.setResponseMessage(parser.message);
    }

    public static class ResponseParser {
        public int status;
        public String message;
    }

    abstract public String errorToString();
    abstract public boolean isSuccess();
    abstract public boolean isPending();
}
