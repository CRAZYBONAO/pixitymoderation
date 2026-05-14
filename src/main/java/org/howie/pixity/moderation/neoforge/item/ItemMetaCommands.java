package org.howie.pixity.moderation.neoforge.item;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;


import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.ArrayList;
import java.util.List;

public final class ItemMetaCommands {

    public static final String PERM_ITEMNAME = "pixity.itemname";
    public static final String PERM_ITEMLORE = "pixity.itemlore";

    private final RankService ranks;

    public ItemMetaCommands(final RankService ranks) {
        this.ranks = ranks;
    }

    private boolean has(ServerPlayer p, String perm) {
        return ranks != null && ranks.hasPerm(p, perm);
    }

    public void register(CommandDispatcher<CommandSourceStack> d) {


        d.register(Commands.literal("itemname")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_ITEMNAME))
                .then(Commands.literal("clear")
                        .executes(ctx -> {

                            ServerPlayer p = ctx.getSource().getPlayerOrException();

                            ItemStack it = p.getMainHandItem();
                            if (it.isEmpty()) {
                                p.sendSystemMessage(LegacyAmpersand.parse("&6&lITEMMETA &7&l➤ &cError! Hold an item in your main hand."));
                                return 0;
                            }

                            it.remove(DataComponents.CUSTOM_NAME);
                            p.sendSystemMessage(LegacyAmpersand.parse("&6&lITEMMETA &7&l➤ &aItem name cleared."));
                            return 1;
                        })
                )

                .then(Commands.argument("text", StringArgumentType.greedyString())
                        .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_ITEMNAME))
                        .executes(ctx -> {

                            ServerPlayer p = ctx.getSource().getPlayerOrException();

                            ItemStack it = p.getMainHandItem();
                            if (it.isEmpty()) {
                                p.sendSystemMessage(LegacyAmpersand.parse("&6&lITEMMETA &7&l➤ &cError! Hold an item in your main hand."));
                                return 0;
                            }

                            String raw = StringArgumentType.getString(ctx, "text");
                            Component name = LegacyAmpersand.parse(raw);



                            it.set(DataComponents.CUSTOM_NAME, name);
                            p.sendSystemMessage(LegacyAmpersand.parse("&6&lITEMMETA &7&l➤ &aItem name set."));
                            return 1;
                        })
                )
        );


        d.register(Commands.literal("itemlore")
                .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_ITEMLORE))
                .then(Commands.literal("clear")

                        .executes(ctx -> {

                            ServerPlayer p = ctx.getSource().getPlayerOrException();

                            ItemStack it = p.getMainHandItem();
                            if (it.isEmpty()) {
                                p.sendSystemMessage(LegacyAmpersand.parse("&6&lITEMMETA &7&l➤ &cError! Hold an item in your main hand."));
                                return 0;
                            }

                            it.remove(DataComponents.LORE);
                            p.sendSystemMessage(LegacyAmpersand.parse("&6&lITEMMETA &7&l➤ &aLore cleared."));
                            return 1;
                        })
                )

                .then(Commands.argument("text", StringArgumentType.greedyString())
                        .requires(src -> src.getEntity() instanceof ServerPlayer p && has(p, PERM_ITEMLORE))
                        .executes(ctx -> {

                            ServerPlayer p = ctx.getSource().getPlayerOrException();

                            ItemStack it = p.getMainHandItem();
                            if (it.isEmpty()) {
                                p.sendSystemMessage(LegacyAmpersand.parse("&6&lITEMMETA &7&l➤ &cError! Hold an item in your main hand."));
                                return 0;
                            }

                            List<Component> lore =
                                    parseLoreLines(
                                            StringArgumentType.getString(ctx, "text")
                                    );

                            setLore(it, lore);

                            p.sendSystemMessage(
                                    LegacyAmpersand.parse("&6&lITEMMETA &7&l➤ &aLore set.")
                            );

                            return 1;
                        })
                )
        );
    }

    private static List<Component> getLore(final ItemStack it) {
        ItemLore lore = it.get(DataComponents.LORE);
        if (lore == null) return new ArrayList<>();
        return new ArrayList<>(lore.lines());
    }

    private static void setLore(final ItemStack it, final List<Component> lines) {
        if (lines == null || lines.isEmpty()) {
            it.remove(DataComponents.LORE);
            return;
        }
        it.set(DataComponents.LORE, new ItemLore(lines));
    }

    private List<Component> parseLoreLines(String input) {

        List<Component> lore = new ArrayList<>();


        String[] parts = input.split("\\|");

        for (String part : parts) {

            String line = part.trim();

            if (line.isEmpty()) {
                lore.add(Component.empty());
                continue;
            }

            lore.add(LegacyAmpersand.parse(line));
        }

        return lore;
    }
}