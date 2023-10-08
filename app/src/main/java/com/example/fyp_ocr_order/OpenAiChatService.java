package com.example.fyp_ocr_order;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OpenAiChatService {
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/completions";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    private static final JsonParser jsonParser = new JsonParser();

    public static String chat(String prompt) throws IOException {
        // Prepare the request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("prompt", prompt);
        requestBody.put("max_tokens", 60);

        // Prepare the request
        RequestBody body = RequestBody.create(gson.toJson(requestBody), MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .addHeader("Authorization", "Bearer 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")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Parse the response
            JsonElement responseJson = jsonParser.parse(response.body().string());
            return responseJson.getAsJsonObject().get("choices").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString();
        }
    }
}
