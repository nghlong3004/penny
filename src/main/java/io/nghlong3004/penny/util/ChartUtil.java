package io.nghlong3004.penny.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChartUtil {
    private static final String QUICK_CHART_API_URL = "https://quickchart.io/chart/create";
    private static final String JSON_PAY = """
            {
              "version": "4",
              "backgroundColor": "#FFFFFF",
              "chart": {
                "type": "pie",
                "data": {
                  "labels": [%s],
                  "datasets": [{
                    "data": [%s],
                    "backgroundColor": [
                        "#FF6384",
                        "#36A2EB",
                        "#FFCE56",
                        "#4BC0C0",
                        "#9966FF",
                        "#FF9F40"
                    ]
                  }]
                },
                "options": {
                  "title": {
                    "display": true,
                    "text": "%s"
                  },
                  "plugins": {
                    "legend": {
                        "position": "bottom"
                    }
                  }
                }
              }
            }
            """;

    public static String getChartUrl(String title, Map<String, Double> data) throws Exception {

        String labels = data.keySet().stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", "));

        String values = data.values().stream().map(Object::toString).collect(Collectors.joining(", "));

        String jsonPayload = JSON_PAY.formatted(labels, values, title);
        String responseBody = "";
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                                             .uri(URI.create(QUICK_CHART_API_URL))
                                             .header("Content-Type", "application/json")
                                             .POST(HttpRequest.BodyPublishers.ofString(jsonPayload,
                                                                                       StandardCharsets.UTF_8))
                                             .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "Error call QuickChart API. Status: " + response.statusCode() + ". Body: " + response.body());
            }

            responseBody = response.body();
        }

        if (!responseBody.contains("\"success\":true")) {
            throw new RuntimeException("QuickChart API returned success=false. Body: " + responseBody);
        }

        Pattern p = Pattern.compile("\"url\":\"(.*?)\"");
        Matcher m = p.matcher(responseBody);

        if (m.find()) {
            return m.group(1);
        }
        else {
            throw new RuntimeException("Could not parse URL from QuickChart response. Body: " + responseBody);
        }
    }
}
