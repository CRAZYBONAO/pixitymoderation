package org.howie.pixity.moderation.neoforge.skills;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.howie.pixity.moderation.PixityModerationNeoForge;
import org.howie.pixity.moderation.chat.TextFormatter;

public class AbilityListener {

    private static final String PREFIX =
            "<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7&l➤ ";

    private boolean meetsRequirement(ServerPlayer player, AbilityType ability) {

        var skills = PixityModerationNeoForge.SKILL_SERVICE;

        int required = AbilityRequirements.getRequiredLevel(ability);
        SkillType skill = AbilityRequirements.getSkill(ability);

        int level = skills.get(player.getUUID()).getLevel(skill);

        if (level < required) {

            player.sendSystemMessage(TextFormatter.parse(
                    PREFIX + "&cRequires level &e" + required + " &cin " + skill.name()
            ));

            return false;
        }

        return true;
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickItem event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        var item = player.getMainHandItem();

        var abilities = PixityModerationNeoForge.ACTIVE_ABILITIES;
        var skills = PixityModerationNeoForge.SKILL_SERVICE;

        boolean shift = player.isShiftKeyDown();




        if (player.isShiftKeyDown() && player.getMainHandItem().isEmpty()) {

            int level = skills.get(player.getUUID()).getLevel(SkillType.TRAINER);

            if (abilities.isOnCooldown(player, AbilityType.ADRENALINE_RUSH)) {
                abilities.sendCooldownMessage(player, AbilityType.ADRENALINE_RUSH);
                return;
            }

            if (!PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(player, AbilityType.ADRENALINE_RUSH)) return;

            long duration = 10000 + (level * 100);
            long cooldown = 300_000;

            abilities.activate(player, AbilityType.ADRENALINE_RUSH, duration, cooldown);

            player.sendSystemMessage(TextFormatter.parse(
                    PREFIX + "<gradient:#EE8D8D:#FFFFFF:#EE8D8D>Adrenaline Rush</gradient> &aactivated!"
            ));
        }




        if (player.getMainHandItem().toString().contains("_ball")) {

            int level = skills.get(player.getUUID()).getLevel(SkillType.PROFESSOR);

            if (abilities.isOnCooldown(player, AbilityType.CAPTURE_AURA)) {
                abilities.sendCooldownMessage(player, AbilityType.CAPTURE_AURA);
                return;
            }

            if (!PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(player, AbilityType.CAPTURE_AURA)) return;

            long duration = 15000 + (level * 100);
            long cooldown = 600_000;

            abilities.activate(player, AbilityType.CAPTURE_AURA, duration, cooldown);

            player.sendSystemMessage(TextFormatter.parse(
                    PREFIX + "<gradient:#8DD6EE:#FFFFFF:#8DD6EE>Capture Aura</gradient> &aactivated!"
            ));
        }




        if (item.is(ItemTags.PICKAXES)) {

            int level = skills.get(player.getUUID()).getLevel(SkillType.MINER);




            if (shift) {

                if (abilities.isOnCooldown(player, AbilityType.DRILL)) {
                    abilities.sendCooldownMessage(player, AbilityType.DRILL);
                    return;
                }

                if (!PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(player, AbilityType.DRILL)) return;

                if (!meetsRequirement(player, AbilityType.DRILL)) return;

                long duration = 5000 + (level / 2) * 1000;
                long cooldown = 1_800_000;

                abilities.activate(player, AbilityType.DRILL, duration, cooldown);

                player.sendSystemMessage(TextFormatter.parse(
                        PREFIX + "<gradient:#5D5D5D:#FFFFFF:#777777>Drill</gradient> &ahas been activated!"
                ));
                return;
            }




            if (abilities.isOnCooldown(player, AbilityType.MINERS_FRENZY)) {
                abilities.sendCooldownMessage(player, AbilityType.MINERS_FRENZY);
                return;
            }

            if (!PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(player, AbilityType.MINERS_FRENZY)) return;

            if (!meetsRequirement(player, AbilityType.MINERS_FRENZY)) return;

            long duration = 5000 + (level / 2) * 1000;
            long cooldown = 600_000;

            abilities.activate(player, AbilityType.MINERS_FRENZY, duration, cooldown);

            player.sendSystemMessage(TextFormatter.parse(
                    PREFIX + "<gradient:#5D5D5D:#FFFFFF:#777777>Miner's Frenzy</gradient> &ahas activated!"
            ));
        }




        if (item.is(ItemTags.SHOVELS)) {

            int level = skills.get(player.getUUID()).getLevel(SkillType.EXCAVATION);

            if (shift) {

                if (abilities.isOnCooldown(player, AbilityType.QUICK_DIG)) {
                    abilities.sendCooldownMessage(player, AbilityType.QUICK_DIG);
                    return;
                }

                if (!PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(player, AbilityType.QUICK_DIG)) return;

                if (!meetsRequirement(player, AbilityType.QUICK_DIG)) return;

                long duration = 5000 + (level / 2) * 1000;
                long cooldown = 1_800_000;

                abilities.activate(player, AbilityType.QUICK_DIG, duration, cooldown);

                player.sendSystemMessage(TextFormatter.parse(
                        PREFIX + "<gradient:#584212:#F1EF8D:#473308>Quick Dig</gradient> &ahas been activated!"
                ));
                return;
            }

            if (abilities.isOnCooldown(player, AbilityType.DIGGING_FRENZY)) {
                abilities.sendCooldownMessage(player, AbilityType.DIGGING_FRENZY);
                return;
            }

            if (!PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(player, AbilityType.DIGGING_FRENZY)) return;

            if (!meetsRequirement(player, AbilityType.DIGGING_FRENZY)) return;

            long duration = 5000 + (level / 2) * 1000;
            long cooldown = 600_000;

            abilities.activate(player, AbilityType.DIGGING_FRENZY, duration, cooldown);

            player.sendSystemMessage(TextFormatter.parse(
                    PREFIX + "<gradient:#584212:#F1EF8D:#473308>Digging Frenzy</gradient> &ahas activated!"
            ));
        }




        if (item.is(ItemTags.AXES)) {

            int level = skills.get(player.getUUID()).getLevel(SkillType.WOODCUTTER);

            if (shift) {

                if (abilities.isOnCooldown(player, AbilityType.TREECAPITATOR)) {
                    abilities.sendCooldownMessage(player, AbilityType.TREECAPITATOR);
                    return;
                }

                if (!PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(player, AbilityType.TREECAPITATOR)) return;

                if (!meetsRequirement(player, AbilityType.TREECAPITATOR)) return;

                long duration = 5000 + (level / 2) * 1000;
                long cooldown = 1_800_000;

                abilities.activate(player, AbilityType.TREECAPITATOR, duration, cooldown);

                player.sendSystemMessage(TextFormatter.parse(
                        PREFIX + "<gradient:#584212:#2BCB12:#473308>Treecapitator</gradient> &ahas been activated!"
                ));
                return;
            }

            if (abilities.isOnCooldown(player, AbilityType.WOODCUTTER_FRENZY)) {
                abilities.sendCooldownMessage(player, AbilityType.WOODCUTTER_FRENZY);
                return;
            }

            if (!PixityModerationNeoForge.ABILITY_ENGINE.isEnabled(player, AbilityType.WOODCUTTER_FRENZY)) return;

            if (!meetsRequirement(player, AbilityType.WOODCUTTER_FRENZY)) return;

            long duration = 5000 + (level / 2) * 1000;
            long cooldown = 600_000;

            abilities.activate(player, AbilityType.WOODCUTTER_FRENZY, duration, cooldown);

            player.sendSystemMessage(TextFormatter.parse(
                    PREFIX + "<gradient:#584212:#2BCB12:#473308>Woodcutter Frenzy</gradient> &ahas activated!"
            ));
        }
    }


}