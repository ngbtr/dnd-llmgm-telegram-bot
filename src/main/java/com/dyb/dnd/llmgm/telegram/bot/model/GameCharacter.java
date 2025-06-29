package com.dyb.dnd.llmgm.telegram.bot.model;

import java.util.List;
import java.util.Map;

public class GameCharacter {
    private Long id;
    private String name;
    private String race;
    private String characterClass;
    private int level;
    private String background;
    private String alignment;

    private int hp;
    private int ac;
    private int speed;

    private Map<String, Integer> abilities; // strength, dexterity, etc.
    private List<String> savingThrows;
    private List<String> skills;

    private List<String> features;  // расширяемый список способностей/чертов
    private List<String> equipment; // снаряжение
    private List<String> notes;     // особые свойства, нестандартные эффекты

    private String status;          // боевое или RP-состояние
    private String location;        // где находится
    private String portraitUrl;     // опционально — ссылка на аватар/портрет

    private String summary;

    // Конструкторы, геттеры и сеттеры


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRace() { return race; }
    public void setRace(String race) { this.race = race; }

    public String getCharacterClass() { return characterClass; }
    public void setCharacterClass(String characterClass) { this.characterClass = characterClass; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public String getBackground() { return background; }
    public void setBackground(String background) { this.background = background; }

    public String getAlignment() { return alignment; }
    public void setAlignment(String alignment) { this.alignment = alignment; }

    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }

    public int getAc() { return ac; }
    public void setAc(int ac) { this.ac = ac; }

    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }

    public Map<String, Integer> getAbilities() { return abilities; }
    public void setAbilities(Map<String, Integer> abilities) { this.abilities = abilities; }

    public List<String> getSavingThrows() { return savingThrows; }
    public void setSavingThrows(List<String> savingThrows) { this.savingThrows = savingThrows; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }

    public List<String> getEquipment() { return equipment; }
    public void setEquipment(List<String> equipment) { this.equipment = equipment; }

    public List<String> getNotes() { return notes; }
    public void setNotes(List<String> notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPortraitUrl() { return portraitUrl; }
    public void setPortraitUrl(String portraitUrl) { this.portraitUrl = portraitUrl; }

    public String summary() {
        if(this.summary == null) {
            String summary = String.format(
                    "%s is a level %d %s %s. HP: %d, AC: %d. Alignment: %s. Background: %s.\n" +
                            "Saving throws: %s. Skills: %s.\n" +
                            "Features: %s.\n" +
                            "Traits: %s.\n" +
                            "Equipment: %s.",
                    name, level, race, characterClass, hp, ac, alignment, background,
                    String.join(", ", savingThrows),
                    String.join(", ", skills),
                    String.join(", ", features),
                    String.join("; ", notes),
                    String.join(", ", equipment)
            );

            this.summary = summary;
        }

        return this.summary;
    }
}
