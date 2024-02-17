package me.phuongaz.thesieure.utils;

import org.bukkit.entity.Player;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class RequestUtils {

    public static void installAllTrustManager() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection
                    .setDefaultHostnameVerifier(new HostnameVerifier() {
                        public boolean verify(String urlHostname,
                                              SSLSession _session) {
                            return true;
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String createRequests(Map<String, String> map) {
        StringBuilder url_params = new StringBuilder();
        for (Map.Entry entry : map.entrySet()) {
            url_params.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        return url_params.substring(0, url_params.length() - 1);
    }

    public static String get(String link) {
        try {
            RequestUtils.installAllTrustManager();
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setUseCaches(false);
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            if (conn.getResponseCode() != 200) {
                return null;
            }
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String post(String POST_URL, String data) {
        try {
            URL obj = new URL(POST_URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            httpURLConnection.setDoOutput(true);
            OutputStream os = httpURLConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            os.close();

            int responseCode = httpURLConnection.getResponseCode(); //RequestUtils.java#88

            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            } else {
                System.out.println("POST request not worked. Response Code: " + responseCode);
                System.out.println("Response Message: " + httpURLConnection.getResponseMessage());

                try (InputStream errorStream = httpURLConnection.getErrorStream()) {
                    if (errorStream != null) {
                        System.out.println("Error Response: " + readInputStream(errorStream));
                    }
                }
            }
        } catch (Exception sslException) {
            sslException.printStackTrace();
        }
        return null;
    }

    private static String readInputStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

    public static Player getPlayerFromRequestID(String requestID) {
        String name = requestID.split("-")[1];
        return org.bukkit.Bukkit.getPlayer(name);
    }
}