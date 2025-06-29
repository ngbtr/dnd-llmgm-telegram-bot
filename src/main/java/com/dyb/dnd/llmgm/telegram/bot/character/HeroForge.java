package com.dyb.dnd.llmgm.telegram.bot.character;

import com.dyb.dnd.llmgm.telegram.bot.model.GameCharacter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HeroForge {

    private static class State {
        int step = 0;
        GameCharacter gameCharacter = new GameCharacter();
    }

    private static final Map<Long, State> activeCreations = new HashMap<>();

    public static boolean isCreating(long userId) {
        return activeCreations.containsKey(userId);
    }

    public static void startCreation(long userId) {
        activeCreations.put(userId, new State());
    }

    public static String handleStep(long userId, String input) {
        State state = activeCreations.get(userId);
        if (state == null) {
            return "Чтобы начать создание персонажа, введите: создать персонажа";
        }

        switch (state.step) {
            case 0 -> {
                state.gameCharacter.setName(input);
                state.step++;
                return "Выбери расу (например: Орк, Эльф, Человек):";
            }
            case 1 -> {
                state.gameCharacter.setRace(input);
                state.step++;
                return "Укажи класс (например: Маг, Варвар, Плут):";
            }
            case 2 -> {
                state.gameCharacter.setCharacterClass(input);
                state.step++;
                return "На каком ты уровне?";
            }
            case 3 -> {
                try {
                    state.gameCharacter.setLevel(Integer.parseInt(input));
                } catch (NumberFormatException e) {
                    return "Уровень должен быть числом. Попробуй ещё раз:";
                }
                state.step++;
                return "Каков твой предысторий (background)?";
            }
            case 4 -> {
                state.gameCharacter.setBackground(input);
                state.step++;
                return "Каков твой мировоззрение (alignment)?";
            }
            case 5 -> {
                state.gameCharacter.setAlignment(input);
                state.step++;
                return "Введи характеристики (str,dex,con,int,wis,cha). Например: 16,14,14,8,10,16";
            }
            case 6 -> {
                String[] parts = input.split(",");
                if (parts.length != 6) return "Нужно 6 чисел, разделённых запятой.";
                Map<String, Integer> abilities = new LinkedHashMap<>();
                String[] keys = {"strength", "dexterity", "constitution", "intelligence", "wisdom", "charisma"};
                try {
                    for (int i = 0; i < 6; i++) {
                        abilities.put(keys[i], Integer.parseInt(parts[i].trim()));
                    }
                } catch (NumberFormatException e) {
                    return "Все значения должны быть числами.";
                }
                state.gameCharacter.setAbilities(abilities);
                state.step++;
                return "Какие у тебя спасброски (saving throws)? Укажи через запятую.";
            }
            case 7 -> {
                state.gameCharacter.setSavingThrows(toList(input));
                state.step++;
                return "Какие навыки (skills) у тебя есть? Через запятую.";
            }
            case 8 -> {
                state.gameCharacter.setSkills(toList(input));
                state.step++;
                return "Перечисли свои особенности (features):";
            }
            case 9 -> {
                state.gameCharacter.setFeatures(toList(input));
                state.step++;
                return "Что у тебя в инвентаре?";
            }
            case 10 -> {
                state.gameCharacter.setEquipment(toList(input));
                state.step++;
                return "Есть ли какие-то заметки или странности персонажа?";
            }
            case 11 -> {
                state.gameCharacter.setNotes(toList(input));
                state.step++;
                return "Сколько у тебя HP?";
            }
            case 12 -> {
                try {
                    state.gameCharacter.setHp(Integer.parseInt(input));
                } catch (NumberFormatException e) {
                    return "HP должно быть числом.";
                }
                state.step++;
                return "Каков твой класс брони (AC)?";
            }
            case 13 -> {
                try {
                    state.gameCharacter.setAc(Integer.parseInt(input));
                } catch (NumberFormatException e) {
                    return "AC должно быть числом.";
                }
                state.step++;
                return "Какова твоя скорость (speed)?";
            }
            case 14 -> {
                try {
                    state.gameCharacter.setSpeed(Integer.parseInt(input));
                } catch (NumberFormatException e) {
                    return "Speed должно быть числом.";
                }
                state.step++;
                return "Где ты находишься?";
            }
            case 15 -> {
                state.gameCharacter.setLocation(input);
                state.step++;
                return "Наконец, в каком ты состоянии? (Например: бодр, ранен, в ярости)";
            }
            case 16 -> {
                state.gameCharacter.setStatus(input);
                saveCharacter(userId, state.gameCharacter);
                activeCreations.remove(userId);
                return "✅ Персонаж создан! Добро пожаловать, " + state.gameCharacter.getName() + "!";
            }
            default -> {
                return "Что-то пошло не так. Попробуй снова.";
            }
        }
    }

    private static List<String> toList(String input) {
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private static void saveCharacter(long userId, GameCharacter gameCharacter) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File dir = new File("src/main/resources/characters/");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, userId + ".json");
            mapper.writeValue(file, gameCharacter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
