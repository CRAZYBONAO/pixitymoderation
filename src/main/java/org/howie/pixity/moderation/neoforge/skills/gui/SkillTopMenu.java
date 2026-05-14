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

public class SkillTopMenu {

    public static void open(ServerPlayer player, SkillType type) {

        Component title = clean(TextFormatter.parse(
                SkillColor.get(type) + " &8Leaderboard"
        ));

        var provider = GuiBuilder.create(title,
                6,
                (id, inv, container) -> new ChestMenu(
                        net.minecraft.world.inventory.MenuType.GENERIC_9x6,
                        id,
                        inv,
                        container,
                        6
                ) {

                    @Override
                    public void clicked(int slot, int button, net.minecraft.world.inventory.ClickType clickType,
                                        net.minecraft.world.entity.player.Player p) {

                        if (!(p instanceof ServerPlayer sp)) return;




                        if (slot < 0 || slot >= this.slots.size()) return;




                        if (slot == 49) {
                            SkillDetailMenu.open(sp, type);
                            return;
                        }


                    }
                }
        );

        player.openMenu(provider);

        if (!(player.containerMenu instanceof ChestMenu menu)) return;

        var service = new LeaderboardService(PixityModerationNeoForge.SKILL_SERVICE);
        var skills = PixityModerationNeoForge.SKILL_SERVICE;




        ItemStack filler = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
        filler.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal(""));




        for (int i = 0; i <= 8; i++) {
            menu.getSlot(i).set(filler.copy());
        }




        for (int row = 1; row <= 4; row++) {
            menu.getSlot(row * 9).set(filler.copy());
            menu.getSlot(row * 9 + 8).set(filler.copy());
        }




        for (int i = 45; i <= 53; i++) {
            menu.getSlot(i).set(filler.copy());
        }




        ItemStack back = new ItemStack(Items.BARRIER);
        back.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                TextFormatter.parse("&c&lBACK"));

        menu.getSlot(49).set(back);




        int[] slots = {
                10,11,12,13,14,15,16,
                19,20,21,22,23,24,25,
                28,29,30,31,32,33,34
        };

        var top = service.getTop(type, slots.length);

        for (int i = 0; i < top.size(); i++) {

            var entry = top.get(i);
            UUID uuid = entry.getKey();
            int level = entry.getValue();

            var data = skills.get(uuid);
            double xp = data.getXp(type);
            double needed = skills.getXpForLevel(level);

            String name = getName(player, uuid);

            ItemStack head = PlayerHeadUtil.create(player, uuid, name);

            int rank = i + 1;

            java.util.List<Component> lore = new java.util.ArrayList<>();

            lore.add(clean(TextFormatter.parse(getRankColor(rank) + " &f" + name)));
            lore.add(clean(TextFormatter.parse("&7" + SkillColor.getPlain(type) + " &eSpecialist")));
            lore.add(clean(Component.literal("")));
            lore.add(clean(TextFormatter.parse("&7Level: &a" + level)));
            lore.add(clean(TextFormatter.parse("&7XP: &e" + (int) xp + " &7/ &e" + (int) needed)));
            lore.add(clean(Component.literal("")));
            lore.add(clean(TextFormatter.parse("&8Skill: " + SkillColor.getPlain(type))));

            head.set(net.minecraft.core.component.DataComponents.LORE,
                    new net.minecraft.world.item.component.ItemLore(lore));

            menu.getSlot(slots[i]).set(head);
        }
    }

    private static Component clean(Component c) {
        return c.copy().withStyle(style -> style.withItalic(false));
    }

    private static String getRankColor(int rank) {
        return switch (rank) {
            case 1 -> "&6#1";
            case 2 -> "&7#2";
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