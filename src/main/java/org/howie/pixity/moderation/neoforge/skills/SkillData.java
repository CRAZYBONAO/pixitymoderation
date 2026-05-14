package org.howie.pixity.moderation.neoforge.skills;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class SkillData {

    private final Map<SkillType, Integer> levels = new EnumMap<>(SkillType.class);
    private final Map<SkillType, Double> xp = new EnumMap<>(SkillType.class);

    private final Map<String, Boolean> toggles = new HashMap<>();

    public SkillData() {
        for (SkillType type : SkillType.values()) {
            levels.put(type, 0);
            xp.put(type, 0.0);
        }
    }

    public int getLevel(SkillType type) {
        return levels.getOrDefault(type, 0);
    }

    public double getXp(SkillType type) {
        return xp.getOrDefault(type, 0.0);
    }

    public void setLevel(SkillType type, int level) {
        levels.put(type, level);
    }

    public void setXp(SkillType type, double amount) {
        xp.put(type, amount);
    }

    public boolean isToggled(String ability) {
        return toggles.getOrDefault(ability, true);
    }

    public void toggle(String ability) {
        toggles.put(ability, !isToggled(ability));
    }

    public Map<SkillType, Integer> getLevels() {
        return levels;
    }

    public Map<SkillType, Double> getXpMap() {
        return xp;
    }

    public Map<String, Boolean> getToggles() {
        return toggles;
    }
}