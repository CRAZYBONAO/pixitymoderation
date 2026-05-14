package org.howie.pixity.moderation.neoforge.skills;

import net.minecraft.server.level.ServerPlayer;

public class StatEngine {

    private final SkillService skills;

    public StatEngine(SkillService skills) {
        this.skills = skills;
    }

    public double getStrength(ServerPlayer player) {
        var data = skills.get(player.getUUID());
        return data.getLevel(SkillType.HUNTER) * 1.0
                + data.getLevel(SkillType.KILLER) * 0.5
                + data.getLevel(SkillType.WOODCUTTER) * 0.5;
    }

    public double getDefense(ServerPlayer player) {
        var data = skills.get(player.getUUID());
        return data.getLevel(SkillType.HUNTER) * 1.0
                + data.getLevel(SkillType.KILLER) * 0.5
                + data.getLevel(SkillType.MINER) * 0.05;
    }

    public double getCritChance(ServerPlayer player) {
        var data = skills.get(player.getUUID());
        return data.getLevel(SkillType.HUNTER) * 0.5
                + data.getLevel(SkillType.KILLER) * 1.0;
    }

    public double getCritDamage(ServerPlayer player) {
        var data = skills.get(player.getUUID());
        return data.getLevel(SkillType.HUNTER) * 0.5
                + data.getLevel(SkillType.KILLER) * 1.0;
    }

    public double getSpeed(ServerPlayer player) {
        var data = skills.get(player.getUUID());
        return data.getLevel(SkillType.BUILDER) * 0.5
                + data.getLevel(SkillType.FARMER) * 0.5;
    }

    public double getAgility(ServerPlayer player) {
        var data = skills.get(player.getUUID());
        return data.getLevel(SkillType.EXCAVATION) * 0.25
                + data.getLevel(SkillType.FARMER) * 0.5;
    }

    public double getHealth(ServerPlayer player) {
        var data = skills.get(player.getUUID());

        return data.getLevel(SkillType.WOODCUTTER) * 0.5
                + data.getLevel(SkillType.ENCHANTER) * 0.5;
    }

    public double getWisdom(ServerPlayer player) {
        var data = skills.get(player.getUUID());
        return data.getLevel(SkillType.BREWER) * 0.5
                + data.getLevel(SkillType.CRAFTER) * 0.5
                + data.getLevel(SkillType.ENCHANTER) * 0.5
                + data.getLevel(SkillType.EXCAVATION) * 0.25
                + data.getLevel(SkillType.PROFESSOR) * 0.5;
    }

    public double getLuck(ServerPlayer player) {
        var data = skills.get(player.getUUID());
        return data.getLevel(SkillType.MINER) * 0.5
                + data.getLevel(SkillType.TRAINER) * 0.5
                + data.getLevel(SkillType.ENCHANTER) * 0.5;
    }
}