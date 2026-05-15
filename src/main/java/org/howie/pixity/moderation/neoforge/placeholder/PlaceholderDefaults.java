package org.howie.pixity.moderation.neoforge.placeholder;

import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class PlaceholderDefaults {

    public static void register() {

        PlaceholderRegistry.registerPlayer(

                "%player%",

                context -> {

                    if (context.player() == null)
                        return "";

                    return context.player()
                            .getName()
                            .getString();
                }
        );

        PlaceholderRegistry.registerPlayer(

                "%player_x%",

                context -> {

                    if (context.player() == null)
                        return "0";

                    return String.valueOf(

                            context.player()
                                    .getBlockX()
                    );
                }
        );

        PlaceholderRegistry.registerPlayer(

                "%player_y%",

                context -> {

                    if (context.player() == null)
                        return "0";

                    return String.valueOf(

                            context.player()
                                    .getBlockY()
                    );
                }
        );

        PlaceholderRegistry.registerPlayer(

                "%player_z%",

                context -> {

                    if (context.player() == null)
                        return "0";

                    return String.valueOf(

                            context.player()
                                    .getBlockZ()
                    );
                }
        );

        PlaceholderRegistry.registerGlobal(

                "%server_online%",

                v -> {

                    var server =
                            ServerLifecycleHooks
                                    .getCurrentServer();

                    if (server == null)
                        return "0";

                    return String.valueOf(
                            server.getPlayerCount()
                    );
                }
        );

        PlaceholderRegistry.registerGlobal(

                "%server_max_players%",

                v -> {

                    var server =
                            ServerLifecycleHooks
                                    .getCurrentServer();

                    if (server == null)
                        return "0";

                    return String.valueOf(
                            server.getMaxPlayers()
                    );
                }
        );

        SkillPlaceholders.register();

        EconomyPlaceholders.register();

        RankPlaceholders.register();

        FishingPlaceholders.register();

        PlaytimePlaceholders.register();

        ContributionPlaceholders.register();

        System.out.println(
                "[Pixity Placeholder] Loaded default placeholders."
        );
    }
}