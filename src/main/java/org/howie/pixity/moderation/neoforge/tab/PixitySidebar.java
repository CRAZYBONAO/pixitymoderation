package org.howie.pixity.moderation.neoforge.tab;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.*;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import org.howie.pixity.moderation.chat.NickHolder;
import org.howie.pixity.moderation.chat.NickManager;
import org.howie.pixity.moderation.neoforge.economy.*;
import org.howie.pixity.moderation.neoforge.fly.FlyTimeService;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.*;

public final class PixitySidebar {

    private final EconomyService economy;
    private final FlyTimeService fly;
    private final RankService ranks;

    private final Scoreboard dummy = new Scoreboard();
    private final Map<UUID, Objective> objectives = new HashMap<>();
    private final Set<UUID> initialized = new HashSet<>();

    private final Map<UUID, Set<String>> sentEntries = new HashMap<>();

    private int anim = 0;
    private int animTick = 0;
    private int currency = 0;
    private int currencyTick = 0;
    private static final int CURRENCY_SPEED = 100;

    private static final boolean DEBUG = false;

    private static void dbg(String msg) {
        if (DEBUG) System.out.println("[PixitySidebar] " + msg);
    }

    public PixitySidebar(EconomyService economy,
                         FlyTimeService fly,
                         RankService ranks) {
        this.economy = economy;
        this.fly = fly;
        this.ranks = ranks;
    }

    public void tick(MinecraftServer server) {

        if (++animTick >= 2) {
            animTick = 0;
            anim++;
        }

        currencyTick++;
        if (currencyTick >= CURRENCY_SPEED) {
            currencyTick = 0;
            currency = (currency + 1) % 3;
        }

        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            render(p);
        }
    }

    private void render(ServerPlayer p) {

        dbg("=== RENDER " + p.getGameProfile().getName() + " ===");

        String objectiveName =
                "pixity_" + p.getUUID().toString().substring(0,8);

        Objective obj = objectives.computeIfAbsent(
                p.getUUID(),
                id -> {
                    dbg("CREATE OBJECTIVE " + objectiveName);
                    return new Objective(
                            dummy,
                            objectiveName,
                            ObjectiveCriteria.DUMMY,
                            Component.literal(""),
                            ObjectiveCriteria.RenderType.INTEGER,
                            false,
                            null
                    );
                }
        );

        if (!initialized.contains(p.getUUID())) {

            dbg("SEND ADD OBJECTIVE");

            p.connection.send(new ClientboundSetObjectivePacket(obj, 0));

            initialized.add(p.getUUID());
        }

        List<String> lines = new ArrayList<>();

        lines.add("§7Server: §fSurvival");
        lines.add("§7Season: §f1");

        lines.add("§r ");

        lines.add("§cYou: §f" + getName(p));
        lines.add("§cFlytime: §f" + formatTime(fly.getTime(p.getUUID())));

        switch (currency) {
            case 0 -> lines.add("§aMoney: §f" + (int)economy.get(p, CurrencyType.MONEY));
            case 1 -> lines.add("§bTokens: §f" + (int)economy.get(p, CurrencyType.TOKENS));
            case 2 -> lines.add("§6Coins: §f" + (int)economy.get(p, CurrencyType.COINS));
        }

        lines.add("§r ");

        lines.add("§7Discord: §fdiscord.pixityreborn.org");
        lines.add("§7Store: §fstore.pixityreborn.org");
        lines.add("§7IP: §fplay.pixityreborn.org");
        lines.add("§cPlayers: §f" + p.server.getPlayerList().getPlayerCount());

        Set<String> previous = sentEntries.computeIfAbsent(p.getUUID(), x -> new HashSet<>());
        Set<String> current = new HashSet<>();

        int score = lines.size();

        for (String line : lines) {

            String entry = getEntry(score);

            dbg("LINE " + score + " -> " + line);

            PlayerTeam team = dummy.getPlayerTeam(entry);

            boolean created = false;

            if (team == null) {
                team = dummy.addPlayerTeam(entry);
                team.getPlayers().add(entry);
                created = true;
            }

            if (!created) {
                dbg("REMOVE TEAM " + entry);
                p.connection.send(ClientboundSetPlayerTeamPacket.createRemovePacket(team));
            }

            team.setPlayerPrefix(LegacyAmpersand.parse(line));
            team.setPlayerSuffix(Component.empty());

            dbg("ADD TEAM " + entry);

            p.connection.send(
                    ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true)
            );

            dbg("SET SCORE " + entry + " = " + score);

            p.connection.send(
                    new ClientboundSetScorePacket(
                            entry,
                            obj.getName(),
                            score,
                            Optional.empty(),
                            Optional.empty()
                    )
            );

            current.add(entry);

            score--;
        }

        dbg("SEND DISPLAY SIDEBAR");

        p.connection.send(new ClientboundSetDisplayObjectivePacket(
                DisplaySlot.SIDEBAR,
                obj
        ));

        obj.setDisplayName(Component.literal(redWhite("Pixity Reborn")));
        p.connection.send(new ClientboundSetObjectivePacket(obj, 2));

        for (String old : previous) {
            if (!current.contains(old)) {
                dbg("RESET " + old);
                p.connection.send(new ClientboundResetScorePacket(old, obj.getName()));
            }
        }

        previous.clear();
        previous.addAll(current);
    }

    private String getName(ServerPlayer p) {

        String prefix = ranks != null ? ranks.prefix(p) : "";
        String suffix = ranks != null ? ranks.suffix(p) : "";

        String name = NickHolder.INSTANCE != null
                ? NickHolder.INSTANCE.getDisplayName(p)
                : p.getGameProfile().getName();

        return prefix + " " + name + " " + suffix;
    }

    private String clean(String input) {

        if (input == null) return "";

        input = input.replace("&", "§");

        input = input.replaceAll("(?i)&#[0-9a-f]{6}", "");
        input = input.replaceAll("(?i)#[0-9a-f]{6}", "");
        input = input.replaceAll("(?i)§x(§[0-9a-f]){6}", "");

        input = input.replaceAll("§(?=[^0-9a-fk-or])", "");

        return input;
    }

    private String redWhite(String text) {

        StringBuilder out = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            boolean red = ((i + anim) % 6) < 3;
            out.append(red ? "§c" : "§f")
                    .append(text.charAt(i));
        }

        return out.toString();
    }

    private static String formatTime(long seconds) {

        long m = seconds / 60;
        long s = seconds % 60;

        return m > 0 ? m + "m " + s + "s" : s + "s";
    }

    private static final String[] ENTRIES = new String[]{
            "§r","§r§r","§r§r§r","§r§r§r§r",
            "§r§r§r§r§r","§r§r§r§r§r§r",
            "§r§r§r§r§r§r§r","§r§r§r§r§r§r§r§r",
            "§r§r§r§r§r§r§r§r§r","§r§r§r§r§r§r§r§r§r§r",
            "§r§r§r§r§r§r§r§r§r§r§r",
            "§r§r§r§r§r§r§r§r§r§r§r§r",
            "§r§r§r§r§r§r§r§r§r§r§r§r§r",
            "§r§r§r§r§r§r§r§r§r§r§r§r§r§r",
            "§r§r§r§r§r§r§r§r§r§r§r§r§r§r§r",
            "§r§r§r§r§r§r§r§r§r§r§r§r§r§r§r§r"
    };

    private String getEntry(int score) {
        return ENTRIES[score];
    }
}