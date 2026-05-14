package org.howie.pixity.moderation.neoforge.skills.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.PixityModerationNeoForge;
import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.economy.EconomyService;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.skills.*;
import org.howie.pixity.moderation.neoforge.skills.gui.*;
import org.w3c.dom.Text;

public class PixitySkillsCommand {

    private final RankService ranks;

    public PixitySkillsCommand(RankService ranks) {
        this.ranks = ranks;
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("pixityskills")




                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();

                            SkillMainMenu.open(player);

                            player.sendSystemMessage(TextFormatter.parse(
                                    "<gradient:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7➤ &aOpening menu..."
                            ));
                            return 1;
                        })




                        .then(Commands.literal("toggleability")
                                .then(Commands.argument("ability", StringArgumentType.word())
                                        .executes(ctx -> {

                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            String input = StringArgumentType.getString(ctx, "ability").toUpperCase();

                                            try {
                                                AbilityType ability = AbilityType.valueOf(input);

                                                PixityModerationNeoForge.SKILL_SERVICE.toggle(
                                                        player.getUUID(),
                                                        ability.name().toLowerCase()
                                                );

                                                player.sendSystemMessage(TextFormatter.parse(
                                                        "<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7&l➤ Toggled &e" + ability.name()
                                                ));

                                            } catch (Exception e) {
                                                player.sendSystemMessage(TextFormatter.parse("<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7&l➤ &cError! Invalid ability!"));
                                            }

                                            return 1;
                                        })
                                )
                        )




                        .then(Commands.literal("info")
                                .then(Commands.argument("skill", StringArgumentType.word())
                                        .executes(ctx -> {

                                            ServerPlayer player = ctx.getSource().getPlayerOrException();

                                            try {
                                                SkillType skill = SkillType.valueOf(
                                                        StringArgumentType.getString(ctx, "skill").toUpperCase()
                                                );

                                                sendSkillInfo(player, skill);

                                            } catch (Exception e) {
                                                player.sendSystemMessage(TextFormatter.parse("<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7&l➤ &cError! Invalid skill!"));
                                            }

                                            return 1;
                                        })
                                )
                        )




                        .then(Commands.literal("abilitys")
                                .then(Commands.argument("skill", StringArgumentType.word())
                                        .executes(ctx -> {

                                            ServerPlayer player = ctx.getSource().getPlayerOrException();

                                            try {
                                                SkillType skill = SkillType.valueOf(
                                                        StringArgumentType.getString(ctx, "skill").toUpperCase()
                                                );

                                                sendAbilities(player, skill);

                                            } catch (Exception e) {
                                                player.sendSystemMessage(TextFormatter.parse("<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7&l➤ &cError! Invalid skill!"));
                                            }

                                            return 1;
                                        })
                                )
                        )




                        .then(Commands.literal("top")
                                .then(Commands.argument("skill", StringArgumentType.word())
                                        .executes(ctx -> {

                                            ServerPlayer player = ctx.getSource().getPlayerOrException();

                                            try {
                                                SkillType skill = SkillType.valueOf(
                                                        StringArgumentType.getString(ctx, "skill").toUpperCase()
                                                );

                                                player.sendSystemMessage(TextFormatter.parse("<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7&l➤ &aOpening " + SkillColor.getPlain(skill) + " &aLeaderboard"));

                                                SkillTopMenu.open(player, skill);

                                            } catch (Exception e) {
                                                player.sendSystemMessage(TextFormatter.parse("<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7&l➤ &cError! >Invalid skill!"));
                                            }

                                            return 1;
                                        })
                                )
                        )




                        .then(Commands.literal("gtop")
                                .executes(ctx -> {

                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    player.sendSystemMessage(TextFormatter.parse("<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7&l➤ &aOpening global leaderboard"));
                                    GlobalTopMenu.open(player);

                                    return 1;
                                })
                        )





                                .then(Commands.literal("addxp")
                                        .requires(src -> {


                                            if (src.getEntity() == null) return true;

                                            if (!(src.getEntity() instanceof ServerPlayer player)) return false;

                                            return PixityModerationNeoForge.RANK_SERVICE
                                                    .hasPerm(player, "pixity.skills.admin");
                                        })
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .then(Commands.argument("skill", StringArgumentType.word())
                                                        .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0))
                                                                .executes(ctx -> {

                                                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                                                    double amount = DoubleArgumentType.getDouble(ctx, "amount");

                                                                    try {
                                                                        SkillType skill = SkillType.valueOf(
                                                                                StringArgumentType.getString(ctx, "skill").toUpperCase()
                                                                        );

                                                                        PixityModerationNeoForge.SKILL_SERVICE.addXp(target, skill, amount);

                                                                        ctx.getSource().sendSuccess(() ->
                                                                                        TextFormatter.parse("<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7&l➤ &aAdded &e" + amount + " &7XP to &c" + target.getName().getString()),
                                                                                true
                                                                        );

                                                                        target.sendSystemMessage(TextFormatter.parse("<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7&l➤ &aYou have had &e" + amount + " XP &aadded to your " + SkillColor.getPlain(skill) + " Skill by " + ctx.getSource().getPlayer().getDisplayName() ));

                                                                    } catch (Exception e) {
                                                                        ctx.getSource().sendFailure(TextFormatter.parse("<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7&l➤ &cError! Invalid skill!"));
                                                                    }

                                                                    return 1;
                                                                })
                                                        )
                                                )
                                        )
                                )







                                .then(Commands.literal("removexp")
                                        .requires(src -> {

                                            if (src.getEntity() == null) return true;

                                            if (!(src.getEntity() instanceof ServerPlayer player)) return false;

                                            return PixityModerationNeoForge.RANK_SERVICE
                                                    .hasPerm(player, "pixity.skills.admin");
                                        })
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .then(Commands.argument("skill", StringArgumentType.word())
                                                        .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0))
                                                                .executes(ctx -> {

                                                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                                                    double amount = DoubleArgumentType.getDouble(ctx, "amount");

                                                                    try {
                                                                        SkillType skill = SkillType.valueOf(
                                                                                StringArgumentType.getString(ctx, "skill").toUpperCase()
                                                                        );

                                                                        var data = PixityModerationNeoForge.SKILL_SERVICE.get(target.getUUID());

                                                                        double newXp = Math.max(0, data.getXp(skill) - amount);
                                                                        data.setXp(skill, newXp);

                                                                        ctx.getSource().sendSuccess(() ->
                                                                                        TextFormatter.parse("<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7&l➤ &cRemoved &e" + amount + " XP &cto &e" + target.getName().getString()),
                                                                                true
                                                                        );

                                                                        target.sendSystemMessage(TextFormatter.parse("<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7&l➤ &cYou have had &e" + amount + " XP &cremoved from your " + SkillColor.getPlain(skill) + " Skill by " + ctx.getSource().getPlayer().getDisplayName()));

                                                                    } catch (Exception e) {
                                                                        ctx.getSource().sendFailure(TextFormatter.parse("<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000> &cError! Invalid skill!"));
                                                                    }

                                                                    return 1;
                                                                })
                                                        )
                                                )
                                        )
                                )







                                .then(Commands.literal("reset")
                                        .requires(src -> {

                                            if (src.getEntity() == null) return true;

                                            if (!(src.getEntity() instanceof ServerPlayer player)) return false;

                                            return PixityModerationNeoForge.RANK_SERVICE
                                                    .hasPerm(player, "pixity.skills.reset");
                                        })
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(ctx -> {

                                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "player");

                                                    var data = PixityModerationNeoForge.SKILL_SERVICE.get(target.getUUID());

                                                    data.getLevels().replaceAll((k, v) -> 0);
                                                    data.getXpMap().replaceAll((k, v) -> 0.0);

                                                    ctx.getSource().sendSuccess(() ->
                                                                    TextFormatter.parse("<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7&l➤ &aReset skills for &c" + target.getName().getString()),
                                                            true
                                                    );

                                                    target.sendSystemMessage(TextFormatter.parse("<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7&l➤ &cYou have had your skills reset by " + ctx.getSource().getPlayer().getDisplayName()));


                                                    return 1;
                                                })
                                        )
                                )







                        .then(Commands.literal("resetall")
                                .requires(src -> {

                                    if (src.getEntity() == null) return true;

                                    if (!(src.getEntity() instanceof ServerPlayer player)) return false;

                                    return PixityModerationNeoForge.RANK_SERVICE
                                            .hasPerm(player, "pixity.skills.resetall");
                                })
                                .executes(ctx -> {

                                    PixityModerationNeoForge.SKILL_SERVICE.getAll()
                                            .values()
                                            .forEach(data -> {
                                                data.getLevels().replaceAll((k, v) -> 0);
                                                data.getXpMap().replaceAll((k, v) -> 0.0);
                                            });

                                    ctx.getSource().sendSuccess(() ->
                                                    TextFormatter.parse("<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7&l➤ &aAll skill data wiped."),
                                            true
                                    );

                                    return 1;
                                })
                        )


        );
    }

    private static void sendSkillInfo(ServerPlayer player, SkillType skill) {
        player.sendSystemMessage(TextFormatter.parse(
                "<gradient:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7➤ &7XP info for &e" + skill.name()
        ));
    }

    private static void sendAbilities(ServerPlayer player, SkillType skill) {
        player.sendSystemMessage(TextFormatter.parse(
                "<gradient:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7➤ &7Abilities for &e" + skill.name()
        ));
    }
}

