package me.phuongaz.thesieure.driver;

import lombok.Getter;
import me.phuongaz.thesieure.Loader;
import me.phuongaz.thesieure.card.Card;
import me.phuongaz.thesieure.driver.response.Response;
import me.phuongaz.thesieure.event.DonateEvent;
import me.phuongaz.thesieure.utils.ParseUtils;
import me.phuongaz.thesieure.utils.RequestUtils;

import java.util.Map;
import java.util.concurrent.*;

abstract public class Driver {

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long INITIAL_DELAY = 0;
    private static final long DELAY_BETWEEN_REQUESTS = 2;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    private final String CHECK_COMMAND = "check";
    private final String CHARGING_COMMAND = "charging";

    @Getter
    private final Card card;

    abstract public String getDriverUrl();
    abstract public Response createResponse(String response);

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    public Driver(Card card) {
        this.card = card;
    }

    public CompletableFuture<Response> request() {
        CompletableFuture<Response> future = new CompletableFuture<>();

        scheduler.schedule(() -> {
            Map<String, String> headers = this.getCard().createDataPost(CHARGING_COMMAND);
            String stringQuery = RequestUtils.createRequests(headers);
            Response response = this.createResponse(RequestUtils.post(getDriverUrl(), stringQuery));
            if (response.getResponseCode() == 99) {
                retryRequest(future, 1);
            } else {
                this.callEvent(response);
                future.complete(response);
            }
        }, INITIAL_DELAY, TIME_UNIT);

        return future;
    }

    private void retryRequest(CompletableFuture<Response> future, int retryCount) {
        if (retryCount > MAX_RETRY_ATTEMPTS) {
            future.completeExceptionally(new RuntimeException("Exceeded maximum retry attempts"));
            return;
        }
        scheduler.schedule(() -> {
            Map<String, String> headers = this.getCard().createDataPost(CHECK_COMMAND);
            String stringQuery = RequestUtils.createRequests(headers);
            Response response = this.createResponse(RequestUtils.post(getDriverUrl(), stringQuery));
            if (response.getResponseCode() == 99) {

                retryRequest(future, retryCount + 1);
            } else {

                this.callEvent(response);
                future.complete(response);
            }
        }, DELAY_BETWEEN_REQUESTS, TIME_UNIT);
    }

    private void callEvent(Response response) {
        DonateEvent event = new DonateEvent(ParseUtils.parseNameFromRequestID(card.getRequestID()), response);
        event.callEvent();
    }
}
