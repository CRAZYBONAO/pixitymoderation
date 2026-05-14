package org.howie.pixity.moderation.neoforge.rules;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.UUID;

public final class RulesService {


    private final RulesConfigManager cfg;
    private final SQLiteRulesSeenStore store;
    private final RankService ranks;

    public RulesService(final RulesConfigManager cfg,
                        final SQLiteRulesSeenStore store,
                        final RankService ranks) {

        this.cfg = cfg;
        this.store = store;
        this.ranks = ranks;
    }

    public void reload() {
        cfg.reload();
    }

    public void showRules(final ServerPlayer p) {
        RulesConfig c = cfg.get();

        p.sendSystemMessage(LegacyAmpersand.parse("&a==== " + (c.header == null ? "&cRules" : c.header) + " &a===="));

        if (c.lines != null) {
            for (String line : c.lines) {
                if (line == null) continue;
                p.sendSystemMessage(Component.literal(line));
            }
        }

        p.sendSystemMessage(Component.literal("&a&l===================="));
    }

    public void maybeShowOnFirstJoin(final ServerPlayer p) {
        RulesConfig c = cfg.get();
        if (!c.showOnFirstJoin) return;

        String bypass = c.bypassPermission == null ? "pixity.rules.bypass" : c.bypassPermission;

        if (ranks != null && ranks.hasPerm(p, bypass)) return;

        UUID u = p.getUUID();

        if (store.hasSeen(u)) return;

        showRules(p);
        store.markSeen(u);
    }


}
