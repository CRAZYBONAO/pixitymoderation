package org.howie.pixity.moderation.neoforge.skills.gui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.skills.AbilityType;
import org.howie.pixity.moderation.neoforge.skills.SkillType;

import java.util.List;

public class SkillDetailMenu {

    public static void open(ServerPlayer player, SkillType type) {

        var provider = GuiBuilder.create(
                getTitle(type),
                3,
                (id, inv, container) -> new ChestMenu(MenuType.GENERIC_9x3, id, inv, container, 3) {

                    {
                        populate(container,  type, (ServerPlayer) player);
                    }

                    @Override
                    public void clicked(int slot, int button, ClickType clickType, net.minecraft.world.entity.player.Player p) {

                        if (!(p instanceof ServerPlayer sp)) return;




                        if (slot < 0 || slot >= this.slots.size()) return;




                        switch (slot) {
                            case 10 -> sendInfo(sp, type);

                            case 13 -> {
                                sp.sendSystemMessage(TextFormatter.parse(
                                        "<gradient:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7➤ Opening leaderboard..."
                                ));

                                SkillTopMenu.open(sp, type);
                            }

                            case 16 -> sendXpInfo(sp, type);

                            case 22 -> {
                                SkillMainMenu.open(sp);
                                return;
                            }
                        }




                        switch (type) {

                            case MINER -> handleMinerClicks(sp, slot);
                            case EXCAVATION -> handleExcavationClicks(sp, slot);
                            case WOODCUTTER -> handleWoodcutterClicks(sp, slot);
                            case FARMER -> handleFarmerClicks(sp, slot);
                            case HUNTER -> handleHunterClicks(sp, slot);
                            case KILLER -> handleKillerClicks(sp, slot);
                            case BUILDER -> handleBuilderClicks(sp, slot);
                            case CRAFTER -> handleCrafterClicks(sp, slot);
                            case ENCHANTER -> handleEnchanterClicks(sp, slot);
                            case BREWER -> handleBrewerClicks(sp, slot);
                            case TRAINER -> handleTrainerClicks(sp, slot);
                            case PROFESSOR -> handleProfessorClicks(sp, slot);
                        }
                    }
                }
        );

        player.sendSystemMessage(TextFormatter.parse(
                "<gradient:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7➤ &aOpening menu..."
        ));

        player.openMenu(provider);
    }




    private static void populate(SimpleContainer c, SkillType type, ServerPlayer player) {

        ItemStack filler = item(Items.BLACK_STAINED_GLASS_PANE, "");

        for (int i = 0; i < 27; i++) {
            c.setItem(i, filler);
        }




        c.setItem(10, infoBook(type));

        c.setItem(13, skillItem(
                Items.NETHER_STAR,
                getColoredName(type) + " LEADERBOARD",
                "",
                "&7Click to view leaderboard.",
                ""
        ));

        c.setItem(16, skillItem(
                Items.EXPERIENCE_BOTTLE,
                "&a&lHow to gain XP",
                "",
                "&7Click to show XP methods.",
                ""
        ));

        c.setItem(22, skillItem(
                Items.BARRIER,
                "&c&lBACK TO MAIN MENU",
                "",
                "&7Click to go back.",
                ""
        ));




        switch (type) {




            case MINER -> {
                c.setItem(11, abilityItem(player, type, AbilityType.MINERS_LUCK, 1,
                        "Miner's Luck", "Chance for bonus ore drops", "Passive"));

                c.setItem(12, abilityItem(player, type, AbilityType.MINERS_FRENZY, 25,
                        "Miner's Frenzy", "Instant mining", "Right click pickaxe"));

                c.setItem(14, abilityItem(player, type, AbilityType.DRILL, 50,
                        "Drill", "3x3 mining", "Right click pickaxe"));

                c.setItem(15, abilityItem(player, type, AbilityType.MINERS_BEST_FRIEND, 75,
                        "Miner's Best Friend", "No durability loss on pickaxes", "Toggle"));
            }




            case EXCAVATION -> {
                c.setItem(11, abilityItem(player, type, AbilityType.EXCAVATORS_KEEP, 1,
                        "Excavator's Keep", "Bonus drops", "Passive"));

                c.setItem(12, abilityItem(player, type, AbilityType.DIGGING_FRENZY, 25,
                        "Digging Frenzy", "Instant digging", "Right click shovel"));

                c.setItem(14, abilityItem(player, type, AbilityType.QUICK_DIG, 50,
                        "Quick Dig", "3x3 digging", "Right click shovel"));

                c.setItem(15, abilityItem(player, type, AbilityType.EXCAVATORS_BEST_FRIEND, 75,
                        "Excavator's Best Friend", "No durability loss on shovels", "Toggle"));
            }




            case WOODCUTTER -> {
                c.setItem(11, abilityItem(player, type, AbilityType.WOODCUTTERS_KEEP, 1,
                        "Woodcutter's Keep", "Bonus wood drops", "Passive"));

                c.setItem(12, abilityItem(player, type, AbilityType.WOODCUTTER_FRENZY, 25,
                        "Woodcutter Frenzy", "Instant breaking", "Right click axe"));

                c.setItem(14, abilityItem(player, type, AbilityType.TREECAPITATOR, 50,
                        "Treecapitator", "Break whole tree", "Right click axe"));

                c.setItem(15, abilityItem(player, type, AbilityType.WOODCUTTERS_BEST_FRIEND, 75,
                        "Woodcutter's Best Friend", "No durability loss on axes", "Toggle"));
            }




            case FARMER -> {
                c.setItem(11, abilityItem(player, type, AbilityType.FARMERS_KEEP, 1,
                        "Farmer's Keep", "Bonus crop drops", "Passive"));

                c.setItem(12, abilityItem(player, type, AbilityType.MAGNETIC_CROPS, 25,
                        "Magnetic Crops", "Auto pickup", "Passive"));

                c.setItem(14, abilityItem(player, type, AbilityType.REPLANTER, 50,
                        "Replanter", "Auto replant crops", "Passive"));

                c.setItem(15, abilityItem(player, type, AbilityType.FARMERS_DREAM, 75,
                        "Farmer's Dream", "No durability loss on hoes", "Toggle"));
            }




            case HUNTER -> {
                c.setItem(11, abilityItem(player, type, AbilityType.HUNTERS_KEEP, 1,
                        "Hunter's Keep", "Bonus mob drops", "Passive"));

                c.setItem(12, abilityItem(player, type, AbilityType.DOUBLE_STRIKE, 35,
                        "Double Strike", "Double damage chance", "Passive"));

                c.setItem(14, abilityItem(player, type, AbilityType.HUNTERS_DREAM, 75,
                        "Hunter's Dream", "No durability loss on swords", "Passive"));
            }




            case KILLER -> {
                c.setItem(11, abilityItem(player, type, AbilityType.KILLERS_KEEP, 1,
                        "Killer's Keep", "Bonus rewards", "Passive"));

                c.setItem(12, abilityItem(player, type, AbilityType.VAMPIRE, 25,
                        "Vampire", "Steal health", "Passive"));

                c.setItem(14, abilityItem(player, type, AbilityType.TRUE_STRIKE, 50,
                        "True Strike", "Ignore armor", "Passive"));

                c.setItem(15, abilityItem(player, type, AbilityType.KILLERS_DREAM, 75,
                        "Killer's Dream", "No durability loss on armor pieces", "Passive"));
            }




            case BUILDER -> {
                c.setItem(11, abilityItem(player, type, AbilityType.BUILDERS_KEEP, 1,
                        "Builder's Keep", "Chance to not consume blocks when building", "Passive"));
            }




            case CRAFTER -> {
                c.setItem(11, abilityItem(player, type, AbilityType.CRAFTERS_KEEP, 1,
                        "Crafter's Keep", "Has a chance to refund crafting materials", "Passive"));
            }




            case ENCHANTER -> {
                c.setItem(11, abilityItem(player, type, AbilityType.ENCHANTERS_KEEP, 1,
                        "Enchanter's Keep", "Has a chance to Refund XP when enchanting", "Passive"));
            }




            case BREWER -> {
                c.setItem(11, abilityItem(player, type, AbilityType.STEADY_HANDS, 25,
                        "Steady Hands", "Has a chance to upgrade potion level when brewing", "Passive"));

                c.setItem(12, abilityItem(player, type, AbilityType.EFFICIENT_BREWMAN, 50,
                        "Efficient Brewman", "Has a chance to refund materials when brewing", "Passive"));

                c.setItem(14, abilityItem(player, type, AbilityType.BREW_EFFICIANDO, 75,
                        "Brew Efficianado", "Has a chance to not consume the Potion when dranken", "Passive"));
            }




            case TRAINER -> {
                c.setItem(11, abilityItem(player, type, AbilityType.TRAINERS_KEEP, 1,
                        "Trainer's Keep", "Has a chance to give bonus pokemon xp after battles", "Passive"));
                c.setItem(12, abilityItem(player, type, AbilityType.TRAINERS_INSIGHT, 25,
                        "Trainer's Insight", "Boosts your Trainer Skill XP gain", "Passive"));
                c.setItem(14, abilityItem(player, type, AbilityType.ADRENALINE_RUSH, 50,
                        "Adrenaline Rush", "Massively boosts pokemon xp gains temporarily and boosts infusion", "Passive"));
                c.setItem(15, abilityItem(player, type, AbilityType.INFUSION, 75,
                        "Infusion", "Has a chance to give an increase to your pokemons IVs when caught", "Passive"));
            }




            case PROFESSOR -> {
                c.setItem(11, abilityItem(player, type, AbilityType.PROFESSORS_KEEP, 1,
                        "Professor's Keep", "Has a chance to give bonus items when capturing pokemon", "Passive"));
                c.setItem(12, abilityItem(player, type, AbilityType.PRECISION_THROW, 25,
                        "Precision Throw", "Has a chance to increase your chances to catch pokemon (excludes legenadrys and shiny)", "Active"));
                c.setItem(14, abilityItem(player, type, AbilityType.CAPTURE_AURA, 50,
                        "Capture Aura", "Boosts you and your friends capture rate when in the same area", "Active"));
                c.setItem(15, abilityItem(player, type, AbilityType.GOTTA_CATCH_EM_ALL, 75,
                        "Gotta Catch'em All", "Has a chance to give a big boost when capturing pokemon", "Active"));
            }
        }
    }




    private static ItemStack infoBook(SkillType type) {

        ItemStack stack = new ItemStack(Items.BOOK);

        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

        stack.set(DataComponents.CUSTOM_NAME,
                TextFormatter.parse("&a&lINFO"));

        stack.set(DataComponents.LORE, new ItemLore(List.of(
                Component.literal(""),
                TextFormatter.parse(getLore(type)),
                Component.literal("")
        )));

        return stack;
    }

    private static ItemStack skillItem(net.minecraft.world.item.Item item, String name, String l1, String l2, String l3) {

        ItemStack stack = new ItemStack(item);

        stack.set(DataComponents.CUSTOM_NAME, TextFormatter.parse(name));

        stack.set(DataComponents.LORE, new ItemLore(List.of(
                Component.literal(l1),
                TextFormatter.parse(l2),
                Component.literal(l3)
        )));

        return stack;
    }

    private static ItemStack item(net.minecraft.world.item.Item item, String name) {
        ItemStack stack = new ItemStack(item);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(name));
        return stack;
    }




    private static Component getTitle(SkillType type) {
        return TextFormatter.parse(getColoredName(type));
    }

    private static String getColoredName(SkillType type) {
        return switch (type) {
            case MINER -> "<gradient:#5D5D5D:#FFFFFF:#777777>&lMINER</gradient>";
            case BREWER -> "<gradient:#FFB000:#FFFFFF:#FFB000>&lBREWER</gradient>";
            case EXCAVATION -> "<gradient:#584212:#F1EF8D:#473308>&lEXCAVATOR</gradient>";
            case WOODCUTTER -> "<gradient:#584212:#2BCB12:#473308>&lWOODCUTTER</gradient>";
            case CRAFTER -> "<gradient:#E5AE37:#775C15:#E5AE37>&lCRAFTER</gradient>";
            case ENCHANTER -> "<gradient:#9537E5:#E918FF:#8E37E5>&lENCHANTER</gradient>";
            case HUNTER -> "<gradient:#4C0F0F:#FFAE18:#4C0F0F>&lHUNTER</gradient>";
            case KILLER -> "<gradient:#F61616:#4C0F0F:#F51717>&lKILLER</gradient>";
            case BUILDER -> "<gradient:#00FF83:#4F6FAB:#00FF83>&lBUILDER</gradient>";
            case FARMER -> "<gradient:#2DFF00:#14640C:#2DFF00>&lFARMER</gradient>";
            case TRAINER -> "<gradient:#EE8D8D:#FFFFFF:#EE8D8D>&lTRAINER</gradient>";
            case PROFESSOR -> "<gradient:#8DD6EE:#FFFFFF:#8DD6EE>&lPROFESSOR</gradient>";
        };
    }

    private static String getLore(SkillType type) {
        return switch (type) {
            case MINER -> "&7Gain xp by <gradient:#5D5D5D:#FFFFFF:#777777>mining ores</gradient> &7around the &aworld&7.";
            case BREWER -> "&7Gain xp by <gradient:#FFB000:#FFFFFF:#FFB000>brewing potions</gradient>&7.";
            case EXCAVATION -> "&7Gain xp by <gradient:#584212:#F1EF8D:#473308>digging blocks</gradient>&7.";
            case WOODCUTTER -> "&7Gain xp by <gradient:#584212:#2BCB12:#473308>chopping trees</gradient>&7.";
            case CRAFTER -> "&7Gain xp by <gradient:#E5AE37:#775C15:#E5AE37>crafting items</gradient>&7.";
            case ENCHANTER -> "&7Gain xp by <gradient:#9537E5:#E918FF:#8E37E5>enchanting items</gradient>&7.";
            case HUNTER -> "&7Gain xp by <gradient:#4C0F0F:#FFAE18:#4C0F0F>killing mobs</gradient>&7.";
            case KILLER -> "&7Gain xp by <gradient:#F61616:#4C0F0F:#F51717>killing players</gradient>&7.";
            case BUILDER -> "&7Gain xp by <gradient:#00FF83:#4F6FAB:#00FF83>placing blocks</gradient>&7.";
            case FARMER -> "&7Gain xp by <gradient:#2DFF00:#14640C:#2DFF00>farming crops</gradient>&7.";
            case TRAINER -> "&7Gain xp by <gradient:#EE8D8D:#FFFFFF:#EE8D8D>leveling pokemon</gradient>&7.";
            case PROFESSOR -> "&7Gain xp by <gradient:#8DD6EE:#FFFFFF:#8DD6EE>catching pokemon</gradient>&7.";
        };
    }

    private static void handleMinerClicks(ServerPlayer p, int slot) {
        switch (slot) {
            case 11 -> toggleAbility(p, SkillType.MINER, AbilityType.MINERS_LUCK);
            case 12 -> toggleAbility(p, SkillType.MINER, AbilityType.MINERS_FRENZY);
            case 14 -> toggleAbility(p, SkillType.MINER, AbilityType.DRILL);
            case 15 -> toggleAbility(p, SkillType.MINER, AbilityType.MINERS_BEST_FRIEND);
        }
    }

    private static void handleExcavationClicks(ServerPlayer p, int slot) {
        switch (slot) {
            case 11 -> toggleAbility(p, SkillType.EXCAVATION, AbilityType.EXCAVATORS_KEEP);
            case 12 -> toggleAbility(p, SkillType.EXCAVATION, AbilityType.DIGGING_FRENZY);
            case 14 -> toggleAbility(p, SkillType.EXCAVATION, AbilityType.QUICK_DIG);
            case 15 -> toggleAbility(p, SkillType.EXCAVATION, AbilityType.EXCAVATORS_BEST_FRIEND);
        }
    }

    private static void handleWoodcutterClicks(ServerPlayer p, int slot) {
        switch (slot) {
            case 11 -> toggleAbility(p, SkillType.WOODCUTTER, AbilityType.WOODCUTTERS_KEEP);
            case 12 -> toggleAbility(p, SkillType.WOODCUTTER, AbilityType.WOODCUTTER_FRENZY);
            case 14 -> toggleAbility(p, SkillType.WOODCUTTER, AbilityType.TREECAPITATOR);
            case 15 -> toggleAbility(p, SkillType.WOODCUTTER, AbilityType.WOODCUTTERS_BEST_FRIEND);
        }
    }

    private static void handleFarmerClicks(ServerPlayer p, int slot) {
        switch (slot) {
            case 11 -> toggleAbility(p, SkillType.FARMER, AbilityType.FARMERS_KEEP);
            case 12 -> toggleAbility(p, SkillType.FARMER, AbilityType.MAGNETIC_CROPS);
            case 14 -> toggleAbility(p, SkillType.FARMER, AbilityType.REPLANTER);
            case 15 -> toggleAbility(p, SkillType.FARMER, AbilityType.FARMERS_DREAM);
        }
    }

    private static void handleHunterClicks(ServerPlayer p, int slot) {
        switch (slot) {
            case 11 -> toggleAbility(p, SkillType.HUNTER, AbilityType.HUNTERS_KEEP);
            case 12 -> toggleAbility(p, SkillType.HUNTER, AbilityType.DOUBLE_STRIKE);
            case 14 -> toggleAbility(p, SkillType.HUNTER, AbilityType.HUNTERS_DREAM);
        }
    }

    private static void handleKillerClicks(ServerPlayer p, int slot) {
        switch (slot) {
            case 11 -> toggleAbility(p, SkillType.KILLER, AbilityType.KILLERS_KEEP);
            case 12 -> toggleAbility(p, SkillType.KILLER, AbilityType.VAMPIRE);
            case 14 -> toggleAbility(p, SkillType.KILLER, AbilityType.TRUE_STRIKE);
            case 15 -> toggleAbility(p, SkillType.KILLER, AbilityType.KILLERS_DREAM);
        }
    }

    private static void handleBuilderClicks(ServerPlayer p, int slot) {
        if (slot == 11) {
            toggleAbility(p, SkillType.BUILDER, AbilityType.BUILDERS_KEEP);
        }
    }

    private static void handleCrafterClicks(ServerPlayer p, int slot) {
        if (slot == 11) {
            toggleAbility(p, SkillType.CRAFTER, AbilityType.CRAFTERS_KEEP);
        }
    }

    private static void handleEnchanterClicks(ServerPlayer p, int slot) {
        if (slot == 11) {
            toggleAbility(p, SkillType.ENCHANTER, AbilityType.ENCHANTERS_KEEP);
        }
    }

    private static void handleBrewerClicks(ServerPlayer p, int slot) {
        switch (slot) {
            case 11 -> toggleAbility(p, SkillType.BREWER, AbilityType.STEADY_HANDS);
            case 12 -> toggleAbility(p, SkillType.BREWER, AbilityType.EFFICIENT_BREWMAN);
            case 14 -> toggleAbility(p, SkillType.BREWER, AbilityType.BREW_EFFICIANDO);
        }
    }

    private static void handleTrainerClicks(ServerPlayer p, int slot) {
        switch (slot){
            case 11 -> toggleAbility(p, SkillType.TRAINER, AbilityType.TRAINERS_KEEP);
            case 12 -> toggleAbility(p, SkillType.TRAINER, AbilityType.TRAINERS_INSIGHT);
            case 14 -> toggleAbility(p, SkillType.TRAINER, AbilityType.ADRENALINE_RUSH);
            case 15 -> toggleAbility(p, SkillType.TRAINER, AbilityType.INFUSION);
        }
    }

    private static void handleProfessorClicks(ServerPlayer p, int slot) {
        switch (slot) {
            case 11 -> toggleAbility(p, SkillType.PROFESSOR, AbilityType.PROFESSORS_KEEP);
            case 12 -> toggleAbility(p, SkillType.PROFESSOR, AbilityType.PRECISION_THROW);
            case 14 -> toggleAbility(p, SkillType.PROFESSOR, AbilityType.CAPTURE_AURA);
            case 15 -> toggleAbility(p, SkillType.PROFESSOR, AbilityType.GOTTA_CATCH_EM_ALL);
        }
    }




    private static void sendXpInfo(ServerPlayer player, SkillType type) {

        String prefix = "<gradient:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7➤ ";

        switch (type) {




            case ENCHANTER -> {
                player.sendSystemMessage(TextFormatter.parse(prefix + "<gradient:#9537E5:#E918FF:#8E37E5>&lENCHANTER</gradient> &aXP METHODS:"));
                player.sendSystemMessage(TextFormatter.parse("&7Wooden tools / Leather Armor: &e1 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Stone tools / Chainmail Armor: &e5 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Iron tools and Armor: &e15 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Gold tools and Armor: &e25 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Diamond tools and Armor: &e75 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Netherite tools and Armor: &e500 XP"));
            }




            case MINER -> {
                player.sendSystemMessage(TextFormatter.parse(prefix + "<gradient:#5D5D5D:#FFFFFF:#777777>&lMINER</gradient> &aXP METHODS:"));
                player.sendSystemMessage(TextFormatter.parse("&7Nether Quartz Ore: &e1 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Coal Ore: &e5 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Iron Ore: &e15 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Gold Ore: &e25 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Diamond Ore: &e50 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Emerald Ore: &e100 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Ancient Debris: &e250 XP"));
            }




            case WOODCUTTER -> {
                player.sendSystemMessage(TextFormatter.parse(prefix + "<gradient:#584212:#2BCB12:#473308>&lWOODCUTTER</gradient> &aXP METHODS:"));
                player.sendSystemMessage(TextFormatter.parse("&7Oak / Spruce / Birch: &e3 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Dark Oak / Jungle / Mangrove: &e5 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Acacia / Cherry: &e7 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Cobblemon Logs: &e15 XP"));
            }




            case EXCAVATION -> {
                player.sendSystemMessage(TextFormatter.parse(prefix + "<gradient:#584212:#F1EF8D:#473308>&lEXCAVATOR</gradient> &aXP METHODS:"));
                player.sendSystemMessage(TextFormatter.parse("&7Dirt / Sand / Clay / Snow: &e1 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Snow Block: &e2 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Red Sand / Soul Soil: &e3 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Gravel / Mud / Podzol: &e5 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Suspicious Blocks: &e50 XP"));
            }




            case FARMER -> {
                player.sendSystemMessage(TextFormatter.parse(prefix + "<gradient:#2DFF00:#14640C:#2DFF00>&lFARMER</gradient> &aXP METHODS:"));

                player.sendSystemMessage(TextFormatter.parse("&ePlanting Crops: &a1 XP"));

                player.sendSystemMessage(TextFormatter.parse("&eHarvesting:"));
                player.sendSystemMessage(TextFormatter.parse("&7Wheat / Carrots / Potatoes / Beetroot: &e2 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Melons / Pumpkins: &e3 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Netherwart / Cocoa: &e5 XP"));

                player.sendSystemMessage(TextFormatter.parse("&eOther:"));
                player.sendSystemMessage(TextFormatter.parse("&7Bamboo: &e0.1 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Sugar Cane: &e2 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Cactus: &e3 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Sweet / Glow Berries: &e1 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Chorus Fruit: &e25 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Kelp / Sea Pickles: &e1 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Moss / Torchflower / Pitcher: &e10 XP"));
            }




            case BREWER -> {
                player.sendSystemMessage(TextFormatter.parse(prefix + "<gradient:#FFB000:#FFFFFF:#FFB000>&lBREWER</gradient> &aXP METHODS:"));

                player.sendSystemMessage(Component.literal(""));

                player.sendSystemMessage(TextFormatter.parse("&eTier 1 Potions: &e25 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Speed / Poison / Strength"));

                player.sendSystemMessage(Component.literal(""));


                player.sendSystemMessage(TextFormatter.parse("&eTier 2 Potions: &e75 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Healing / Regen / Fire Resist / Weakness / Slowness"));

                player.sendSystemMessage(Component.literal(""));


                player.sendSystemMessage(TextFormatter.parse("&eTier 3 Potions: &e150 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Jump Boost / Slow Falling"));
            }




            case HUNTER -> {
                player.sendSystemMessage(TextFormatter.parse(prefix + "<gradient:#4C0F0F:#FFAE18:#4C0F0F>&lHUNTER</gradient> &aXP METHODS:"));

                player.sendSystemMessage(TextFormatter.parse("&7Passive mobs: &e1–3 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Hostile mobs: &e5–10 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Strong mobs: &e25 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Bosses: &e250–1000 XP"));
            }




            case BUILDER -> {
                player.sendSystemMessage(TextFormatter.parse(prefix + "<gradient:#00FF83:#4F6FAB:#00FF83>&lBUILDER</gradient> &aXP METHODS:"));
                player.sendSystemMessage(TextFormatter.parse("&7Gain XP by placing blocks"));
                player.sendSystemMessage(TextFormatter.parse("&7Blacklist prevents abuse (ores, blocks, etc)"));
            }




            case CRAFTER -> {
                player.sendSystemMessage(TextFormatter.parse(prefix + "<gradient:#E5AE37:#775C15:#E5AE37>&lCRAFTER</gradient> &aXP METHODS:"));
                player.sendSystemMessage(TextFormatter.parse("&7Craft Vanilla Items: &e2 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Craft Cobblemon Items: &e5 XP"));
            }




            case KILLER -> {
                player.sendSystemMessage(TextFormatter.parse(prefix + "<gradient:#F61616:#4C0F0F:#F51717>&lKILLER</gradient> &aXP METHODS:"));
                player.sendSystemMessage(TextFormatter.parse("&7Kill Players: &e100 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7(Only counts in PvP zones / duels)"));
            }




            case TRAINER -> {
                player.sendSystemMessage(TextFormatter.parse(prefix + "<gradient:#EE8D8D:#FFFFFF:#EE8D8D>&lTRAINER</gradient> &aXP METHODS:"));
                player.sendSystemMessage(TextFormatter.parse("&7Gain XP by leveling up your Pokémon"));
                player.sendSystemMessage(TextFormatter.parse("&7Higher level-ups = more XP"));
            }




            case PROFESSOR -> {
                player.sendSystemMessage(TextFormatter.parse(prefix + "<gradient:#8DD6EE:#FFFFFF:#8DD6EE>&lPROFESSOR</gradient> &aXP METHODS:"));

                player.sendSystemMessage(TextFormatter.parse("&7Catch Pokémon: &e3 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Catch Shiny Pokémon: &e5 XP"));

                player.sendSystemMessage(TextFormatter.parse("&7Catch Mythical: &e150 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Catch Shiny Mythical: &e500 XP"));

                player.sendSystemMessage(TextFormatter.parse("&7Catch Legendary: &e1000 XP"));
                player.sendSystemMessage(TextFormatter.parse("&7Catch Shiny Legendary: &e5000 XP"));
            }




            default -> {
                player.sendSystemMessage(TextFormatter.parse(
                        prefix + "&7XP methods not defined yet."
                ));
            }
        }
    }

    private static void sendInfo(ServerPlayer player, SkillType type) {
        player.sendSystemMessage(TextFormatter.parse(
                "<gradient:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7➤ " + getLore(type)
        ));
    }

    private static ItemStack abilityItem(ServerPlayer player, SkillType skill, AbilityType ability,
                                         int unlockLevel,
                                         String name,
                                         String description,
                                         String usage) {

        var service = org.howie.pixity.moderation.PixityModerationNeoForge.SKILL_SERVICE;
        var activeManager = org.howie.pixity.moderation.PixityModerationNeoForge.ACTIVE_ABILITIES;
        var abilityEngine = org.howie.pixity.moderation.PixityModerationNeoForge.ABILITY_ENGINE;

        var data = service.get(player.getUUID());

        int level = data.getLevel(skill);

        boolean unlocked = level >= unlockLevel;
        boolean enabled = abilityEngine.isEnabled(player, ability);

        boolean active = activeManager.isActive(player, ability);
        boolean onCooldown = activeManager.isOnCooldown(player, ability);

        long remaining = 0;

        if (onCooldown) {
            remaining = activeManager.getRemainingCooldown(player, ability);
        }

        String status = unlocked
                ? (enabled ? "&a&lENABLED" : "&c&lDISABLED")
                : "&4&lLOCKED";

        String cdText;

        if (!unlocked) {
            cdText = "&c&lLOCKED";
        } else if (active) {
            cdText = "&a&lACTIVE";
        } else if (onCooldown) {
            cdText = "&eON COOLDOWN FOR: &f" + formatTime(remaining);
        } else {
            cdText = "&a&lREADY";
        }

        ItemStack stack = new ItemStack(net.minecraft.world.item.Items.BOOK);




        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                org.howie.pixity.moderation.chat.TextFormatter.parse(name));




        String proc = getProcChance(player, skill, ability);
        String scaling = getAbilityScaling(player, skill, ability);




        java.util.List<Component> lore = new java.util.ArrayList<>();

        lore.add(TextFormatter.parse(status));
        lore.add(Component.literal(""));

        lore.add(TextFormatter.parse("&7Unlocks at: &e" + unlockLevel));
        lore.add(TextFormatter.parse("&7Usage: &f" + usage));
        lore.add(TextFormatter.parse("&7" + description));

        if (!proc.isEmpty()) {
            lore.add(Component.literal(""));
            lore.add(TextFormatter.parse(proc));
        }

        if (!scaling.isEmpty()) {
            lore.add(TextFormatter.parse(scaling));
        }

        lore.add(Component.literal(""));
        lore.add(TextFormatter.parse(cdText));
        lore.add(Component.literal(""));

        lore.add(TextFormatter.parse(
                unlocked ? "&a&lCLICK TO TOGGLE" : "&c&lABILITY LOCKED"
        ));


        stack.set(DataComponents.LORE, new ItemLore(lore));




        if (enabled) {
            stack.set(net.minecraft.core.component.DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        }

        return stack;
    }

    private static void toggleAbility(ServerPlayer player, SkillType skill, AbilityType ability) {

        var service = org.howie.pixity.moderation.PixityModerationNeoForge.SKILL_SERVICE;

        var data = service.get(player.getUUID());

        int level = data.getLevel(skill);

        int unlock = switch (ability) {
            case MINERS_LUCK -> 1;
            case MINERS_FRENZY -> 25;
            case DRILL -> 50;
            case MINERS_BEST_FRIEND -> 75;
            default -> 0;
        };

        if (level < unlock) {
            player.sendSystemMessage(TextFormatter.parse(
                    "<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7&l➤ &cError! You have not unlocked this ability!"
            ));
            return;
        }

        service.toggle(player.getUUID(), ability.name().toLowerCase());

        player.sendSystemMessage(TextFormatter.parse(
                "<gradient:#FF0000:#FFFFFF:#FF0000:#FFFFFF:#FF0000>&lPIXITY SKILLS</gradient> &7&l➤ &aToggled " + ability.name()
        ));


        open(player, skill);
    }

    private static String formatTime(long ms) {

        if (ms <= 0) return "&a&lREADY";

        long seconds = ms / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds %= 60;
        minutes %= 60;

        if (hours > 0) return hours + "h " + minutes + "m";
        if (minutes > 0) return minutes + "m " + seconds + "s";
        return seconds + "s";
    }

    private static String getAbilityScaling(ServerPlayer player, SkillType skill, AbilityType ability) {

        var stats = org.howie.pixity.moderation.PixityModerationNeoForge.STAT_ENGINE;

        double luck = stats.getLuck(player);
        double wisdom = stats.getWisdom(player);

        return switch (ability) {




            case MINERS_LUCK,
                 EXCAVATORS_KEEP,
                 WOODCUTTERS_KEEP,
                 FARMERS_KEEP,
                 HUNTERS_KEEP,
                 KILLERS_KEEP,
                 BUILDERS_KEEP,
                 TRAINERS_KEEP,
                 PROFESSORS_KEEP
                    -> "&7Scaling: &bLuck &7(+" + (int) luck + "% effectiveness)";




            case CRAFTERS_KEEP,
                 ENCHANTERS_KEEP,
                 STEADY_HANDS,
                 EFFICIENT_BREWMAN,
                 BREW_EFFICIANDO
                    -> "&7Scaling: &dWisdom &7(+" + (int) wisdom + "% effectiveness)";

            default -> "";
        };
    }

    private static String getProcChance(ServerPlayer player, SkillType skill, AbilityType ability) {

        var data = org.howie.pixity.moderation.PixityModerationNeoForge.SKILL_SERVICE
                .get(player.getUUID());

        int level = data.getLevel(skill);

        double chance = switch (ability) {




            case MINERS_LUCK,
                 EXCAVATORS_KEEP,
                 WOODCUTTERS_KEEP
                    -> level * 1.0;




            case FARMERS_KEEP,
                 HUNTERS_KEEP,
                 KILLERS_KEEP,
                 TRAINERS_KEEP,
                 PROFESSORS_KEEP
                    -> level * 0.25;




            case DOUBLE_STRIKE
                    -> level * 0.5;




            case STEADY_HANDS,
                 EFFICIENT_BREWMAN,
                 BREW_EFFICIANDO
                    -> level * 0.34;

            default -> -1;
        };

        if (chance <= 0) return "";

        return "&7Proc Chance: &a" + String.format("%.2f", chance) + "%";
    }
}