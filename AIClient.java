// AIClient.java
// Wrapper around the OpenAI Chat Completions API.

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AIClient {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL_NAME = "gpt-4o-mini";

    public String askGpt(String prompt) throws Exception {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("OPENAI_API_KEY environment variable not set.");
        }

        String jsonBody = "{"
                + "\"model\":\"" + MODEL_NAME + "\","
                + "\"messages\":[{\"role\":\"user\",\"content\":\"" + escapeJson(prompt) + "\"}],"
                + "\"max_tokens\":256"
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENAI_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("OpenAI API error: HTTP " + response.statusCode() + " - " + response.body());
        }

        String body = response.body();
        return extractFirstContent(body);
    }

    // Escape characters that would break JSON syntax.
    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    // Robust-ish extractor for the first "content" field, respecting escaped quotes.
    private String extractFirstContent(String json) {
        String marker = "\"content\":\"";
        int idx = json.indexOf(marker);
        if (idx == -1) {
            throw new RuntimeException("Could not find content field in OpenAI response.");
        }
        int start = idx + marker.length();

        StringBuilder sb = new StringBuilder();
        boolean escaping = false;

        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);

            if (escaping) {
                // Handle common escapes
                switch (c) {
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    default: sb.append(c); break;
                }
                escaping = false;
            } else {
                if (c == '\\') {
                    escaping = true;
                } else if (c == '"') {
                    // End of content string
                    break;
                } else {
                    sb.append(c);
                }
            }
        }

        return sb.toString();
    }
}
