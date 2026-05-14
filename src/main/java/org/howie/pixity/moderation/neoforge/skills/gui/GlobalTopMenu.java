package org.howie.pixity.moderation.neoforge.skills.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.howie.pixity.moderation.PixityModerationNeoForge;
import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.skills.*;
import org.howie.pixity.moderation.neoforge.skills.leaderboard.LeaderboardService;

import java.util.UUID;

public class GlobalTopMenu {

    public static void open(ServerPlayer player) {




        Component title = TextFormatter.parse(
                "<rainbow>&lGLOBAL LEADERBOARD</rainbow>"
        );

        var provider = GuiBuilder.create(
                title,
                6,
                (id, inv, container) -> new ChestMenu(
                        net.minecraft.world.inventory.MenuType.GENERIC_9x6,
                        id,
                        inv,
                        container,
                        6
                )
        );

        player.openMenu(provider);

        if (!(player.containerMenu instanceof ChestMenu menu)) return;

        var service = new LeaderboardService(PixityModerationNeoForge.SKILL_SERVICE);
        var skills = PixityModerationNeoForge.SKILL_SERVICE;




        ItemStack filler = createFiller();


        for (int i = 0; i < 9; i++) menu.getSlot(i).set(filler.copy());


        for (int i = 45; i < 54; i++) menu.getSlot(i).set(filler.copy());


        for (int row = 1; row < 5; row++) {
            menu.getSlot(row * 9).set(filler.copy());
            menu.getSlot(row * 9 + 8).set(filler.copy());
        }




        ItemStack back = new ItemStack(Items.BARRIER);
        back.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                TextFormatter.parse("&c&lBACK"));

        menu.getSlot(49).set(back);




        var top = service.getGlobalTop(36);

        for (int i = 0; i < top.size(); i++) {

            var entry = top.get(i);

            UUID uuid = entry.getKey();
            int total = entry.getValue();

            String name = getName(player, uuid);

            ItemStack head = PlayerHeadUtil.create(player, uuid, name);

            int rank = i + 1;

            java.util.List<Component> lore = new java.util.ArrayList<>();


            lore.add(
                    TextFormatter.parse(getRankColor(rank) + " &f" + name)
                            .copy()
                            .withStyle(style -> style.withItalic(false))
            );

            lore.add(Component.literal(""));


            lore.add(
                    TextFormatter.parse("&7Total Level: &a" + total)
                            .copy()
                            .withStyle(style -> style.withItalic(false))
            );

            lore.add(Component.literal(""));


            lore.add(
                    TextFormatter.parse("<rainbow>GLOBAL</rainbow> &7Skill Ranking")
                            .copy()
                            .withStyle(style -> style.withItalic(false))
            );

            head.set(net.minecraft.core.component.DataComponents.LORE,
                    new net.minecraft.world.item.component.ItemLore(lore));


            int slot = getSlot(i);
            menu.getSlot(slot).set(head);
        }
    }




    private static int getSlot(int index) {
        int row = index / 7;
        int col = index % 7;
        return (row + 1) * 9 + (col + 1);
    }




    private static ItemStack createFiller() {
        ItemStack pane = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
        pane.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                Component.literal(""));
        return pane;
    }




    private static String getRankColor(int rank) {
        return switch (rank) {
            case 1 -> "&6&l#1";
            case 2 -> "&7&l#2";
            case 3 -> "&#cd7f32&l#3";
            default -> "<white>#" + rank + "</white>";
        };
    }




    private static String getName(ServerPlayer viewer, UUID uuid) {

        var server = viewer.getServer();
        if (server == null) return "Unknown";

        var profile = server.getProfileCache().get(uuid).orElse(null);

        if (profile != null) {
            return profile.getName();
        }

        return uuid.toString().substring(0, 8);
    }
}