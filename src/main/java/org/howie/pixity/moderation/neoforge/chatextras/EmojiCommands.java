package org.howie.pixity.moderation.neoforge.chatextras;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.*;

public final class EmojiCommands {

    public static final String PERM_LIST = "pixity.emoji.list";

    private final ChatExtrasConfig cfg;
    private final RankService ranks;

    public EmojiCommands(ChatExtrasConfig cfg, RankService ranks) {
        this.cfg = cfg;
        this.ranks = ranks;
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks.hasPerm(p, perm) || ranks.hasPerm(p, "pixity.admin");
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("emoji")

                .then(Commands.literal("list")

                        .requires(src -> {
                            if (src.getEntity() == null) return true;

                            if (!(src.getEntity() instanceof ServerPlayer p))
                                return false;

                            return src.hasPermission(2) || has(p, PERM_LIST);
                        })

                        .executes(ctx -> {
                            sendPage(ctx.getSource(), 1);
                            return 1;
                        })

                        .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                .executes(ctx -> {
                                    int page = IntegerArgumentType.getInteger(ctx, "page");
                                    sendPage(ctx.getSource(), page);
                                    return 1;
                                })
                        )
                )
        );
    }

    private void sendPage(CommandSourceStack src, int page) {

        if (cfg == null || !cfg.enabled || !cfg.emojiEnabled) {
            src.sendFailure(LegacyAmpersand.parse("&d&lEMOJIS &7&l➤ &cError! Emojis are disabled."));
            return;
        }

        Map<String, String> map = cfg.emojis;

        if (map == null || map.isEmpty()) {
            src.sendFailure(LegacyAmpersand.parse("&d&lEMOJIS &7&l➤ No emojis configured."));
            return;
        }

        List<String> keys = new ArrayList<>(map.keySet());
        Collections.sort(keys);

        final int perPage = 12;

        int totalPages = (int) Math.ceil(keys.size() / (double) perPage);
        int p = Math.max(1, Math.min(page, totalPages));

        src.sendSystemMessage(
                LegacyAmpersand.parse("&d&lEmojis &7(Page " + p + "/" + totalPages + ")")
        );

        int start = (p - 1) * perPage;
        int end = Math.min(keys.size(), start + perPage);

        for (int i = start; i < end; i++) {
            String k = keys.get(i);
            String v = map.get(k);

            src.sendSystemMessage(
                    LegacyAmpersand.parse("&e" + k + " &7→ &f" + v)
            );
        }
    }
}