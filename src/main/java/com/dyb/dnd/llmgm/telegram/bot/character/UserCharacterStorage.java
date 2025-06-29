package com.dyb.dnd.llmgm.telegram.bot.character;

import com.dyb.dnd.llmgm.telegram.bot.model.GameCharacter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserCharacterStorage {
    private static final String CHARACTER_DIR = "src/main/resources/characters/playable";

    private static final ObjectMapper mapper = new ObjectMapper();

    private final Map<Long, GameCharacter> characterMap = new ConcurrentHashMap<Long, GameCharacter>();

    public void init() {
        File folder = new File(CHARACTER_DIR);
        if (!folder.exists()) {
            folder.mkdirs();
            return;
        }


        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

        if (files == null) return;

        for (File file : files) {
            load(file);
        }
    }

    private void load(File file) {
        try {
            GameCharacter character = mapper.readValue(file, GameCharacter.class);
            characterMap.put(character.getId(), character);
        } catch (IOException e) {
            System.err.println("Failed to load character from: " + file.getName());
            e.printStackTrace();
        }
    }

    public void saveCharacter(GameCharacter character) {
        Long id = character.getId(); // this should be userId for players, or generated for NPCs
        File dir = new File(CHARACTER_DIR);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                System.out.println("Created character directory: " + dir.getAbsolutePath());
            }
        }

        File file = new File(dir, id + ".json");
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, character);
            characterMap.put(id, character);
            System.out.println("Character saved and registered: " + character.getName() + " (id=" + id + ")");
        } catch (IOException e) {
            System.err.println("Failed to save character " + character.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public GameCharacter getCharacter(Long userId) {
        return characterMap.get(userId);
    }

    public boolean hasCharacter(Long userId) {
        return characterMap.containsKey(userId);
    }
}
