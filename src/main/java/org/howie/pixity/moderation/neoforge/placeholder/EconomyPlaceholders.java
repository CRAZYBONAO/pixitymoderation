package org.howie.pixity.moderation.neoforge.placeholder;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.howie.pixity.moderation.PixityModerationNeoForge;

import org.howie.pixity.moderation.neoforge.economy.CurrencyType;
import org.howie.pixity.moderation.neoforge.economy.EconomyService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EconomyPlaceholders {

    public static void register() {

        registerBalances();

        registerBaltopPosition();

        registerBaltopEntries();
    }

    /*
     * %balance_money%
     * %balance_tokens%
     * %balance_coins%
     */

    private static void registerBalances() {

        PlaceholderRegistry.registerRegex(

                "%balance_(money|coins|tokens)%",

                (context, match) -> {

                    ServerPlayer player =
                            context.player();

                    if (player == null)
                        return "0";

                    CurrencyType type =
                            currency(
                                    match.group(1)
                            );

                    if (type == null)
                        return "0";

                    double balance =
                            PixityModerationNeoForge
                                    .ECONOMY_SERVICE
                                    .get(
                                            player,
                                            type
                                    );

                    return format(balance);
                }
        );
    }

    /*
     * %baltop_position_money%
     */

    private static void registerBaltopPosition() {

        PlaceholderRegistry.registerRegex(

                "%baltop_position_(money|coins|tokens)%",

                (context, match) -> {

                    ServerPlayer player =
                            context.player();

                    if (player == null)
                        return "#0";

                    CurrencyType type =
                            currency(
                                    match.group(1)
                            );

                    if (type == null)
                        return "#0";

                    List<Map.Entry<UUID, Double>> top =
                            sorted(type);

                    UUID uuid =
                            player.getUUID();

                    for (int i = 0; i < top.size(); i++) {

                        if (top.get(i)
                                .getKey()
                                .equals(uuid)) {

                            return "#" + (i + 1);
                        }
                    }

                    return "#0";
                }
        );
    }

    /*
     * %balance_top_money_1%
     */

    private static void registerBaltopEntries() {

        PlaceholderRegistry.registerRegex(

                "%balance_top_(money|coins|tokens)_(\\d+)%",

                (context, match) -> {

                    CurrencyType type =
                            currency(
                                    match.group(1)
                            );

                    if (type == null)
                        return "Unknown";

                    int position;

                    try {

                        position =
                                Integer.parseInt(
                                        match.group(2)
                                ) - 1;

                    } catch (Exception e) {

                        return "Unknown";
                    }

                    List<Map.Entry<UUID, Double>> top =
                            sorted(type);

                    if (position < 0
                            || position >= top.size()) {

                        return "Unknown";
                    }

                    Map.Entry<UUID, Double> entry =
                            top.get(position);

                    MinecraftServer server =
                            context.player() != null
                                    ? context.player().getServer()
                                    : null;

                    String name =
                            entry.getKey().toString();

                    if (server != null
                            && server.getProfileCache() != null) {

                        name =
                                server.getProfileCache()
                                        .get(entry.getKey())
                                        .map(p -> p.getName())
                                        .orElse(name);
                    }

                    return name
                            + " - "
                            + format(
                            entry.getValue()
                    );
                }
        );
    }

    private static List<Map.Entry<UUID, Double>> sorted(
            CurrencyType type
    ) {

        EconomyService econ =
                PixityModerationNeoForge
                        .ECONOMY_SERVICE;

        List<Map.Entry<UUID, Double>> list =
                new ArrayList<>();

        for (UUID uuid :
                econ.getAllUsers()) {

            list.add(

                    Map.entry(

                            uuid,

                            econ.get(
                                    uuid,
                                    type
                            )
                    )
            );
        }

        list.sort(

                Comparator.comparingDouble(
                        Map.Entry<UUID, Double>::getValue
                ).reversed()
        );

        return list;
    }

    private static CurrencyType currency(
            String input
    ) {

        try {

            return CurrencyType.valueOf(
                    input.toUpperCase()
            );

        } catch (Exception e) {

            return null;
        }
    }

    private static String format(
            double value
    ) {

        return String.format(
                "%,.0f",
                value
        );
    }
}