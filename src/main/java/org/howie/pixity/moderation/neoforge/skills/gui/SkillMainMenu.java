package org.howie.pixity.moderation.neoforge.skills.gui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import org.howie.pixity.moderation.PixityModerationNeoForge;
import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.fishing.FishTier;
import org.howie.pixity.moderation.neoforge.skills.SkillType;

import java.util.List;

public class SkillMainMenu {

    public static void open(ServerPlayer player) {


        var provider = GuiBuilder.create(
                TextFormatter.parse("<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient>"),
                6,
                (id, inv, container) -> new ChestMenu(MenuType.GENERIC_9x6, id, inv, container, 6) {

                    {
                        populate(container, player);
                    }



                    @Override
                    public void clicked(int slot, int button, ClickType type, net.minecraft.world.entity.player.Player player) {

                        if (!(player instanceof ServerPlayer sp)) return;





                        if (slot < 0 || slot >= this.slots.size()) return;




                        switch (slot) {
                            case 1 -> SkillDetailMenu.open(sp, SkillType.MINER);
                            case 3 -> SkillDetailMenu.open(sp, SkillType.EXCAVATION);
                            case 5 -> SkillDetailMenu.open(sp, SkillType.WOODCUTTER);
                            case 7 -> SkillDetailMenu.open(sp, SkillType.CRAFTER);
                            case 19 -> SkillDetailMenu.open(sp, SkillType.ENCHANTER);
                            case 21 -> SkillDetailMenu.open(sp, SkillType.BREWER);

                            case 23 -> SkillDetailMenu.open(sp, SkillType.BUILDER);
                            case 25 -> SkillDetailMenu.open(sp, SkillType.FARMER);
                            case 37 -> SkillDetailMenu.open(sp, SkillType.HUNTER);
                            case 39 -> SkillDetailMenu.open(sp, SkillType.TRAINER);
                            case 41 -> SkillDetailMenu.open(sp, SkillType.PROFESSOR);
                            case 43 -> SkillDetailMenu.open(sp, SkillType.KILLER);

                            case 49 -> GlobalTopMenu.open(sp);
                        }
                    }
                }
        );

        player.openMenu(provider);
    }

    private static void populate(SimpleContainer c, ServerPlayer player) {

        ItemStack filler = item(Items.BLACK_STAINED_GLASS_PANE, "");

        for (int i = 0; i < 54; i++) {
            c.setItem(i, filler);
        }





        c.setItem(1, skill(
                player,
                SkillType.MINER,
                Items.NETHERITE_PICKAXE,
                "<gradient:#5D5D5D:#FFFFFF:#777777>&lMINER</gradient>",
                "&7Gain xp by &emining &7ores around the &aworld&7."
        ));

        c.setItem(3, skill(
                player,
                SkillType.EXCAVATION,
                Items.NETHERITE_SHOVEL,
                "<gradient:#584212:#F1EF8D:#473308>&lEXCAVATOR</gradient>",
                "&7Gain xp by &edigging &7blocks around the &aworld&7."
        ));

        c.setItem(5, skill(
                player,
                SkillType.WOODCUTTER,
                Items.NETHERITE_AXE,
                "<gradient:#584212:#2BCB12:#473308>&lWOODCUTTER</gradient>",
                "&7Gain xp by &echopping &7trees down around the world."
        ));

        c.setItem(7, skill(
                player,
                SkillType.CRAFTER,
                Items.CRAFTING_TABLE,
                "<gradient:#E5AE37:#775C15:#E5AE37>&lCRAFTER</gradient>",
                "&7Gain xp by &ecrafting &7items."
        ));

        c.setItem(19, skill(
                player,
                SkillType.ENCHANTER,
                Items.ENCHANTING_TABLE,
                "<gradient:#9537E5:#E918FF:#8E37E5>&lENCHANTER</gradient>",
                "&7Gain xp by &eenchanting &7items."
        ));

        c.setItem(21, skill(
                player,
                SkillType.BREWER,
                Items.BREWING_STAND,
                "<gradient:#FFB000:#FFFFFF:#FFB000>&lBREWER</gradient>",
                "&7Gain xp by &ebrewing &7potions."
        ));





        c.setItem(23, skill(
                player,
                SkillType.BUILDER,
                Items.BRICKS,
                "<gradient:#00FF83:#4F6FAB:#00FF83>&lBUILDER</gradient>",
                "&7Gain xp by &eplacing blocks &7around the world or building your base or farms."
        ));

        c.setItem(25, skill(
                player,
                SkillType.FARMER,
                Items.WHEAT,
                "<gradient:#2DFF00:#14640C:#2DFF00>&lFARMER</gradient>",
                "&7Gain xp by &eplanting &7and &eharvesting crops &7around the world or at your base."
        ));

        c.setItem(37, skill(
                player,
                SkillType.HUNTER,
                Items.ZOMBIE_HEAD,
                "<gradient:#4C0F0F:#FFAE18:#4C0F0F>&lHUNTER</gradient>",
                "&7Gain xp by &ekilling mobs &7around the world or at your base with spawners."
        ));

        c.setItem(39, skill(
                player,
                SkillType.TRAINER,
                getCobblemonItem("poke_ball"),
                "<gradient:#EE8D8D:#FFFFFF:#EE8D8D>&lTRAINER</gradient>",
                "&7Gain xp by &eleveling up &7and &eevolving &7your pokemon."
        ));

        c.setItem(41, skill(
                player,
                SkillType.PROFESSOR,
                getCobblemonItem("beast_ball"),
                "<gradient:#8DD6EE:#FFFFFF:#8DD6EE>&lPROFESSOR</gradient>",
                "&7Gain xp by &ecatching &7more pokemon."
        ));

        c.setItem(43, skill(
                player,
                SkillType.KILLER,
                Items.NETHERITE_SWORD,
                "<gradient:#F61616:#4C0F0F:#F51717>&lKILLER</gradient>",
                "&7Gain xp by &ekilling players &7in the warzone or through duels."
        ));





        c.setItem(49, item(
                Items.NETHER_STAR,
                "<rainbow>&lGLOBAL SKILL LEADERBOARD</rainbow>"
        ));

        ItemStack top = item(
                Items.NETHER_STAR,
                "<rainbow>&lGLOBAL SKILL LEADERBOARD</rainbow>"
        );

        top.set(DataComponents.LORE, new ItemLore(List.of(
                clean(Component.literal("")),
                clean(TextFormatter.parse(
                        "&7Click to view the <rainbow>&lGLOBAL LEADERBOARD</rainbow> &7for skills."
                )),
                clean(Component.literal(""))
        )));

        c.setItem(49, top);
    }

    private static ItemStack skill(
            ServerPlayer player,
            SkillType type,
            Item item,
            String name,
            String description
    ) {

        ItemStack stack = new ItemStack(item);

        stack.set(DataComponents.CUSTOM_NAME,
                clean(TextFormatter.parse(name)));

        var skills = org.howie.pixity.moderation.PixityModerationNeoForge.SKILL_SERVICE;

        var data = skills.get(player.getUUID());

        int level = data.getLevel(type);
        double xp = data.getXp(type);

        var service = PixityModerationNeoForge.SKILL_SERVICE;

        double required = service.getXpForLevel(level);

        double percent = required <= 0
                ? 100
                : Math.min(100, (xp / required) * 100);

        List<Component> lore = List.of(

                clean(Component.literal("")),

                clean(TextFormatter.parse(description)),

                clean(Component.literal("")),

                clean(TextFormatter.parse(
                        "&7Level: &e" + level
                )),

                clean(TextFormatter.parse(
                        "&7XP: &b" + (int) xp + "&7/&3" + (int) required
                )),

                clean(TextFormatter.parse(
                        "&7Progress: &a" + String.format("%.1f", percent) + "%"
                )),

                clean(Component.literal("")),

                clean(TextFormatter.parse(
                        "&eClick to view abilities"
                ))
        );

        stack.set(DataComponents.LORE, new ItemLore(lore));

        return stack;
    }

    private static ItemStack item(net.minecraft.world.item.Item item, String name) {
        ItemStack stack = new ItemStack(item);
        stack.set(DataComponents.CUSTOM_NAME,
                clean(TextFormatter.parse(name))
        );
        return stack;
    }



    private static Item getCobblemonItem(String id) {
        Item item = BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath("cobblemon", id)
        );

        return item == Items.AIR ? Items.BARRIER : item;
    }

    private static Component clean(Component c) {
        return c.copy().withStyle(style -> style.withItalic(false));
    }


}