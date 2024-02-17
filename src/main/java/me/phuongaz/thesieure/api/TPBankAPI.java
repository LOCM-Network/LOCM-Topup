package me.phuongaz.thesieure.api;

import lombok.Getter;
import lombok.Setter;
import me.phuongaz.thesieure.api.type.HistoryTransaction;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Getter
public class TPBankAPI {

    private String accountNumber;
    private String password;
    private String account;
    @Setter
    private String accessToken = "";
    private long tickInterval;
    private boolean running;
    private static List<HistoryTransaction> historyTransactions;

    public TPBankAPI(String accountNumber, String account, String password, long tickInterval) {
        this.accountNumber = accountNumber;
        this.account = account;
        this.password = password;
        this.tickInterval = tickInterval;
    }

    public String getToken() {
        try {
            String url = "https://ebank.tpb.vn/gateway/api/auth/login";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");

            con.setRequestProperty("DEVICE_ID", "LYjkjqGZ3HhGP5520GxPP2j94RDMC7Xje77MI7" + (int)(Math.random() * 999999999999L));
            con.setRequestProperty("PLATFORM_VERSION", "91");
            con.setRequestProperty("DEVICE_NAME", "Chrome");
            con.setRequestProperty("SOURCE_APP", "HYDRO");
            con.setRequestProperty("Authorization", "Bearer");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json, text/plain, */*");
            con.setRequestProperty("Referer", "https://ebank.tpb.vn/retail/vX/login?returnUrl=%2Fmain");
            con.setRequestProperty("sec-ch-ua-mobile", "?0");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.164 Safari/537.36");
            con.setRequestProperty("PLATFORM_NAME", "WEB");
            con.setRequestProperty("APP_VERSION", "1.3");

            String postData = "{\"username\":\"" + getAccount() + "\",\"password\":\"" + getPassword() + "\"}";
            con.setDoOutput(true);
            con.getOutputStream().write(postData.getBytes(StandardCharsets.UTF_8));

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                return response.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getHistoryRaw(String token, String stk_tpbank) {
        try {
            //now - 7 days
            String start_day = Date.from(new Date().toInstant().minusSeconds(60 * 60 * 24 * 7)).toString().substring(0, 10).replace("-", "");
            String end_day = new Date().toString().substring(0, 10).replace("-", "");
            String url = "https://ebank.tpb.vn/gateway/api/smart-search-presentation-service/v1/account-transactions/find";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");

            con.setRequestProperty("Connection", "keep-alive");
            con.setRequestProperty("DEVICE_ID", "LYjkjqGZ3HhGP5520GxPP2j94RDMC7Xje77MI7" + new Random().nextLong());
            con.setRequestProperty("PLATFORM_VERSION", "91");
            con.setRequestProperty("DEVICE_NAME", "Chrome");
            con.setRequestProperty("SOURCE_APP", "HYDRO");
            con.setRequestProperty("Authorization", "Bearer " + token);
            con.setRequestProperty("XSRF-TOKEN", "3229191c-b7ce-4772-ab93-55a" + new Random().nextLong());
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json, text/plain, */*");
            con.setRequestProperty("sec-ch-ua-mobile", "?0");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.164 Safari/537.36");
            con.setRequestProperty("PLATFORM_NAME", "WEB");
            con.setRequestProperty("APP_VERSION", "1.3");
            con.setRequestProperty("Origin", "https://ebank.tpb.vn");
            con.setRequestProperty("Sec-Fetch-Site", "same-origin");
            con.setRequestProperty("Sec-Fetch-Mode", "cors");
            con.setRequestProperty("Sec-Fetch-Dest", "empty");
            con.setRequestProperty("Referer", "https://ebank.tpb.vn/retail/vX/main/inquiry/account/transaction?id=" + stk_tpbank);
            con.setRequestProperty("Accept-Language", "vi-VN,vi;q=0.9,fr-FR;q=0.8,fr;q=0.7,en-US;q=0.6,en;q=0.5");
            con.setRequestProperty("Cookie", "_ga=GA1.2.1679888794.1623516" + new Random().nextLong() + "; _gid=GA1.2.580582711.162" + new Random().nextLong() + "; _gcl_au=1.1.756417552.162" + new Random().nextLong() + "; Authorization=" + token);

            String data = "{\"accountNo\":\"" + stk_tpbank + "\",\"currency\":\"VND\",\"fromDate\":\"" + start_day + "\",\"toDate\":\"" + end_day + "\",\"keyword\":\"\"}";
            con.setDoOutput(true);
            con.getOutputStream().write(data.getBytes(StandardCharsets.UTF_8));

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                return response.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
