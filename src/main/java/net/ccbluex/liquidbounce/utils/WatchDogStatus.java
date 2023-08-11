package net.ccbluex.liquidbounce.utils;

import com.google.common.base.Charsets;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class WatchDogStatus {
    public static String DEFAULT_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36";

    public static String getStatus() throws IOException, InterruptedException, RequestFailedException {
        URL targetUrl = new URL("https://api.plancke.io/hypixel/v1/punishmentStats");
        URLConnection openConnection = targetUrl.openConnection();
        HttpURLConnection connection = (HttpURLConnection)openConnection;
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", WatchDogStatus.DEFAULT_UA);
        connection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty("Referer", "https://plancke.io/");
        connection.connect();
        InputStream inputStream = connection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        int c = 0;
        while ((c = inputStreamReader.read()) != -1) {
            stringBuilder.append((char) c);
        }
        String raw = stringBuilder.toString();
        JSONObject jsonObject = new JSONObject(raw);

        if (!jsonObject.getBoolean("success")) {
            throw new RequestFailedException("Value success is false");
        }

        JSONObject jsonObject1 = jsonObject.getJSONObject("record");
        return "Staff\nTotal: " + jsonObject1.getInt("staff_total") + "\nRollingDaily: " + jsonObject1.getInt("staff_rollingDaily")
                + "\n\nWatchdog:\nTotal: " + jsonObject1.getInt("watchdog_total") + "\nRollingDaily: " + jsonObject1.getInt("watchdog_rollingDaily")
                + "\n LastMinute: " + jsonObject1.getInt("watchdog_lastMinute");
    }

    public static class RequestFailedException extends Throwable {
        public RequestFailedException(String string) {
            super(string);
        }
    }
}
