package com.dyb.dnd.llmgm.telegram.bot;

import com.dyb.dnd.llmgm.telegram.bot.model.GameCharacter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class GameMasterInteractionService {
    private static final String API_KEY = System.getenv("TAVERN_SCRIBE_BOT_OPENAI_API_KEY");
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();


    public String processPlayerMessage(long userId, String username, String message, GameCharacter character) {
        System.out.println("LLMService: Requesting OpenAI for @" + username);

        String characterSummary = character.summary();

        String systemPrompt = """
        Ты — мастер настольной ролевой игры Dungeons & Dragons 5e. Игровой стиль: кинематографичный, насыщенный описаниями, допускается юмор и дикая магия.Ты не подсказываешь игрокам действия и не бросаешь за них кубики — они делают это сами.После каждого действия выдавай: 1. Художественное описание последствий 2.

        Игрок который к тебе обращается:
        %s
        """.formatted(characterSummary);

        String json = buildRequestJson(message);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("OpenAI request failed: " + response.code());
                return "⚠️ Failed to get response from LLM.";
            }

            String responseBody = response.body().string();
            JsonNode root = mapper.readTree(responseBody);
            return root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

        } catch (IOException e) {
            e.printStackTrace();
            return "⚠️ Error while contacting the LLM.";
        }
    }

    private String buildRequestJson(String prompt) {
        return """
                {
                  "model": "gpt-3.5-turbo",
                  "messages": [
                    {
                      "role": "system",
                      "content": "Ты — мастер настольной ролевой игры Dungeons & Dragons 5e. Игровой стиль: кинематографичный, насыщенный описаниями, допускается юмор и дикая магия.Ты не подсказываешь игрокам действия и не бросаешь за них кубики — они делают это сами.После каждого действия выдавай: 1. Художественное описание последствий 2. Обновлённый game_state в JSON"
                    },
                    {
                      "role": "user",
                      "content": "%s"
                    }
                  ],
                  "temperature": 0.9
                }
                """.formatted(prompt);
    }
}
