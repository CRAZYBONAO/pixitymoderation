package org.howie.pixity.moderation.neoforge.tab;

import net.minecraft.server.level.ServerPlayer;
import org.howie.pixity.moderation.neoforge.rank.RankHolder;
import org.howie.pixity.moderation.neoforge.rank.RankService;

public class TabFormatting {

    public static String buildPrefix(ServerPlayer p) {

        RankService ranks = RankHolder.INSTANCE;

        if (ranks == null)
            return glyph(p);

        String prefix = ranks.prefix(p);

        return glyph(p) + " " + prefix + "§r";
    }

    private static String glyph(ServerPlayer p) {

        RankService ranks = RankHolder.INSTANCE;

        if (ranks == null) return "";

        if (ranks.hasPerm(p, "pixity.owner")) return "\uE000";
        if (ranks.hasPerm(p, "pixity.manager")) return "\uE001";
        if (ranks.hasPerm(p, "pixity.developer")) return "\uE002";
        if (ranks.hasPerm(p, "pixity.headadmin")) return "\uE003";
        if (ranks.hasPerm(p, "pixity.admin")) return "\uE004";
        if (ranks.hasPerm(p, "pixity.trialadmin")) return "\uE005";
        if (ranks.hasPerm(p, "pixity.mod")) return "\uE006";
        if (ranks.hasPerm(p, "pixity.donator.support")) return "\uE009";
        if (ranks.hasPerm(p, "pixity.donator.legendary")) return "\uE007";
        if (ranks.hasPerm(p, "pixity.donator.master")) return "\uE008";
        if (ranks.hasPerm(p, "pixity.donator.mystic")) return "\uE009";
        if (ranks.hasPerm(p, "pixity.donator.elite")) return "\uE00A";
        if (ranks.hasPerm(p, "pixity.donator.shiny")) return "\uE00B";
        if (ranks.hasPerm(p, "pixity.normalgym")) return "\uE00D";
        if (ranks.hasPerm(p, "pixity.firegym")) return "\uE00E";
        if (ranks.hasPerm(p, "pixity.watergym")) return "\uE00F";
        if (ranks.hasPerm(p, "pixity.grassgym")) return "\uE010";
        if (ranks.hasPerm(p, "pixity.electricgym")) return "\uE011";
        if (ranks.hasPerm(p, "pixity.icegym")) return "\uE012";
        if (ranks.hasPerm(p, "pixity.fightinggym")) return "\uE013";
        if (ranks.hasPerm(p, "pixity.poisongym")) return "\uE014";
        if (ranks.hasPerm(p, "pixity.groundgym")) return "\uE015";
        if (ranks.hasPerm(p, "pixity.flyinggym")) return "\uE016";
        if (ranks.hasPerm(p, "pixity.psychicgym")) return "\uE017";
        if (ranks.hasPerm(p, "pixity.buggym")) return "\uE018";
        if (ranks.hasPerm(p, "pixity.rockgym")) return "\uE019";
        if (ranks.hasPerm(p, "pixity.ghostgym")) return "\uE01A";
        if (ranks.hasPerm(p, "pixity.dragongym")) return "\uE01B";
        if (ranks.hasPerm(p, "pixity.darkgym")) return "\uE01C";
        if (ranks.hasPerm(p, "pixity.steelgym")) return "\uE01D";
        if (ranks.hasPerm(p, "pixity.fairygym")) return "\uE01E";
        if (ranks.hasPerm(p, "pixity.elite4")) return "\uE00C";




        return "";
    }
}