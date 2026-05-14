package org.howie.pixity.moderation.neoforge.playtime;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import org.howie.pixity.moderation.neoforge.playtime.gui.PlaytimeLevelsMenu;

import java.util.List;

public final class PlaytimeLevelsCommand {

    private final PlaytimeService playtime;
    private final SQLitePlaytimeStore store;
    private final List<PlaytimeLevelsMenu.Level> levels;

    public PlaytimeLevelsCommand(
            PlaytimeService playtime,
            SQLitePlaytimeStore store,
            List<PlaytimeLevelsMenu.Level> levels
    ) {
        this.playtime = playtime;
        this.store = store;
        this.levels = levels;
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {

        d.register(Commands.literal("playtimelevels")
                .executes(ctx -> {

                    ServerPlayer player =
                            ctx.getSource().getPlayerOrException();

                    open(player, 0);
                    return 1;
                }));
    }

    private void open(ServerPlayer player, int page) {

        player.openMenu(new MenuProvider() {

            @Override
            public Component getDisplayName() {
                return Component.literal("Playtime Levels");
            }

            @Override
            public net.minecraft.world.inventory.AbstractContainerMenu createMenu(
                    int id,
                    Inventory inv,
                    Player p
            ) {
                return new PlaytimeLevelsMenu(
                        id,
                        inv,
                        playtime,
                        store,
                        levels,
                        page
                );
            }
        });
    }
}