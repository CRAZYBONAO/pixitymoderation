package org.howie.pixity.moderation.neoforge.placeholder;

import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.PixityModerationNeoForge;

import org.howie.pixity.moderation.neoforge.skills.SkillData;
import org.howie.pixity.moderation.neoforge.skills.SkillService;
import org.howie.pixity.moderation.neoforge.skills.SkillType;

public class SkillPlaceholders {

    public static void register() {

        registerSkillLevel();

        registerSkillXp();

        registerSkillXpNeeded();
    }

    private static void registerSkillLevel() {

        PlaceholderRegistry.registerRegex(

                "%skill_level_(.+?)%",

                (context, match) -> {

                    ServerPlayer player =
                            context.player();

                    if (player == null)
                        return "0";

                    SkillType type =
                            skill(
                                    match.group(1)
                            );

                    if (type == null)
                        return "0";

                    SkillData data =
                            PixityModerationNeoForge
                                    .SKILL_SERVICE
                                    .get(
                                            player.getUUID()
                                    );

                    return String.valueOf(
                            data.getLevel(type)
                    );
                }
        );
    }

    private static void registerSkillXp() {

        PlaceholderRegistry.registerRegex(

                "%skill_xp_(.+?)%",

                (context, match) -> {

                    ServerPlayer player =
                            context.player();

                    if (player == null)
                        return "0";

                    SkillType type =
                            skill(
                                    match.group(1)
                            );

                    if (type == null)
                        return "0";

                    SkillData data =
                            PixityModerationNeoForge
                                    .SKILL_SERVICE
                                    .get(
                                            player.getUUID()
                                    );

                    return String.valueOf(

                            (int) data.getXp(type)
                    );
                }
        );
    }

    private static void registerSkillXpNeeded() {

        PlaceholderRegistry.registerRegex(

                "%skill_xp_needed_(.+?)%",

                (context, match) -> {

                    ServerPlayer player =
                            context.player();

                    if (player == null)
                        return "0";

                    SkillType type =
                            skill(
                                    match.group(1)
                            );

                    if (type == null)
                        return "0";

                    SkillData data =
                            PixityModerationNeoForge
                                    .SKILL_SERVICE
                                    .get(
                                            player.getUUID()
                                    );

                    int level =
                            data.getLevel(type);

                    double needed =
                            PixityModerationNeoForge
                                    .SKILL_SERVICE
                                    .getXpForLevel(level);

                    return String.valueOf(
                            (int) needed
                    );
                }
        );
    }

    private static SkillType skill(
            String input
    ) {

        try {

            return SkillType.valueOf(
                    input.toUpperCase()
            );

        } catch (Exception e) {

            return null;
        }
    }
}