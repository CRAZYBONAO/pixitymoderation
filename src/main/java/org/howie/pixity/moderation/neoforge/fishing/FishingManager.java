package org.howie.pixity.moderation.neoforge.fishing;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.particles.ParticleTypes;

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.fishing.augment.FishingAugment;
import org.howie.pixity.moderation.neoforge.fishing.events.FishingEventManager;
import org.howie.pixity.moderation.neoforge.milestones.core.MilestoneCheckService;
import org.howie.pixity.moderation.neoforge.stats.PlayerStatsDatabase;

import java.util.*;

public class FishingManager {

    private static final Map<UUID, Integer> comboMap = new HashMap<>();
    private static final Map<UUID, Long> comboTimeout = new HashMap<>();

    private static final long COMBO_RESET_TIME = 180000;

    private static final Random R = new Random();


    private static final Map<UUID, Long> tsunamiEnd = new HashMap<>();

    private static final long TSUNAMI_DURATION_MS = 60000;
    private static final double TSUNAMI_RADIUS = 8.0;




    public static final List<FishData> FISH = new ArrayList<>();

    static {




        FISH.add(new FishData("angel_fish", "Angel Fish", FishTier.BRONZE, 5, 15, 15, 1001, List.of("river", "plains"), 50));
        FISH.add(new FishData("betta_fish", "Betta Fish", FishTier.BRONZE, 4, 12, 18, 1002, List.of("river"), 45));
        FISH.add(new FishData("carp_fish", "Carp", FishTier.BRONZE, 8, 20, 22, 1007, List.of("river", "swamp"), 40));
        FISH.add(new FishData("cat_fish", "Catfish", FishTier.BRONZE, 12, 28, 30, 1008, List.of("swamp"), 35));
        FISH.add(new FishData("gold_fish", "Goldfish", FishTier.BRONZE, 3, 10, 12, 1016, List.of("plains"), 50));
        FISH.add(new FishData("shrimp", "Shrimp", FishTier.BRONZE, 2, 6, 15, 1076, List.of("ocean"), 50));
        FISH.add(new FishData("green_barb", "Green Barb", FishTier.BRONZE, 5, 12, 25, 1065, List.of("river"), 45));
        FISH.add(new FishData("rosy_barb", "Rosy Barb", FishTier.BRONZE, 8, 16, 65, 1075, List.of("river"), 40));
        FISH.add(new FishData("trout", "Trout", FishTier.BRONZE, 12, 28, 95, 1079, List.of("river"), 35));





        FISH.add(new FishData("gourami_fish", "Gourami", FishTier.SILVER, 10, 25, 45, 1019, List.of("river"), 30));
        FISH.add(new FishData("haddock_fish", "Haddock", FishTier.SILVER, 15, 30, 55, 1021, List.of("ocean"), 25));
        FISH.add(new FishData("koi_fish", "Koi Fish", FishTier.SILVER, 8, 20, 60, 1025, List.of("river"), 22));
        FISH.add(new FishData("perch_fish", "Perch", FishTier.SILVER, 10, 24, 50, 1032, List.of("river"), 28));
        FISH.add(new FishData("sardine_fish", "Sardine", FishTier.SILVER, 6, 16, 40, 1046, List.of("ocean"), 35));
        FISH.add(new FishData("greenfish", "Greenfish", FishTier.SILVER, 10, 18, 80, 1066, List.of("swamp"), 25));
        FISH.add(new FishData("bream", "Bream", FishTier.SILVER, 10, 24, 70, 1069, List.of("river"), 28));
        FISH.add(new FishData("shallow_sunfish", "Shallow Sunfish", FishTier.SILVER, 14, 26, 270, 1078, List.of("beach"), 14));
        FISH.add(new FishData("slimefish", "Slimefish", FishTier.SILVER, 18, 32, 350, 1077, List.of("swamp"), 15));
        FISH.add(new FishData("icefish", "Icefish", FishTier.SILVER, 15, 30, 300, 1067, List.of("frozen_ocean"), 25));






        FISH.add(new FishData("lion_fish", "Lion Fish", FishTier.GOLD, 12, 26, 120, 1027, List.of("ocean"), 15));
        FISH.add(new FishData("mullet_fish", "Mullet", FishTier.GOLD, 14, 32, 130, 1028, List.of("ocean"), 14));
        FISH.add(new FishData("pike_fish", "Pike", FishTier.GOLD, 20, 40, 150, 1033, List.of("river"), 12));
        FISH.add(new FishData("red_snapper", "Red Snapper", FishTier.GOLD, 18, 35, 170, 1044, List.of("ocean"), 10));
        FISH.add(new FishData("sturgeon", "Sturgeon", FishTier.GOLD, 25, 50, 180, 1051, List.of("river"), 8));
        FISH.add(new FishData("coralfish", "Coralfish", FishTier.GOLD, 12, 24, 240, 1061, List.of("warm_ocean"), 14));
        FISH.add(new FishData("bubblefish", "Bubblefish", FishTier.GOLD, 10, 22, 180, 1059, List.of("river"), 15));
        FISH.add(new FishData("anthias", "Anthias", FishTier.GOLD, 12, 20, 220, 1056, List.of("ocean"), 15));





        FISH.add(new FishData("blue_jellyfish", "Blue Jellyfish", FishTier.DIAMOND, 15, 30, 300, 1003, List.of("ocean"), 7));
        FISH.add(new FishData("bluetang", "Blue Tang", FishTier.DIAMOND, 10, 22, 320, 1004, List.of("ocean"), 6));
        FISH.add(new FishData("octopus", "Octopus", FishTier.DIAMOND, 25, 45, 400, 1030, List.of("deep_ocean"), 5));
        FISH.add(new FishData("stingray", "Stingray", FishTier.DIAMOND, 30, 60, 450, 1050, List.of("deep_ocean"), 4));
        FISH.add(new FishData("tuna", "Tuna", FishTier.DIAMOND, 35, 70, 500, 1052, List.of("ocean"), 4));
        FISH.add(new FishData("purple_firefish", "Purple Firefish", FishTier.DIAMOND, 16, 28, 800, 1074, List.of("nether"), 4));
        FISH.add(new FishData("teal_jellyfish", "Teal Jellyfish", FishTier.DIAMOND, 20, 40, 550, 1068, List.of("ocean"), 6));
        FISH.add(new FishData("blue_eel", "Blue Eel", FishTier.DIAMOND, 25, 50, 650, 1058, List.of("ocean"), 5));






        FISH.add(new FishData("obsidian_piranha", "Obsidian Piranha", FishTier.PLATINUM, 20, 35, 900, 1029, List.of("nether"), 2));
        FISH.add(new FishData("prism_angel", "Prism Angel Fish", FishTier.PLATINUM, 15, 28, 950, 1037, List.of("lush_caves"), 2));
        FISH.add(new FishData("radioactive", "Radioactive Fish", FishTier.PLATINUM, 25, 45, 1100, 1042, List.of("swamp"), 1));
        FISH.add(new FishData("sea_turtle", "Sea Turtle", FishTier.PLATINUM, 40, 80, 1400, 1047, List.of("ocean"), 2));
        FISH.add(new FishData("crimson_bubblefish", "Crimson Bubblefish", FishTier.PLATINUM, 10, 22, 1400, 1062, List.of("badlands"), 2));
        FISH.add(new FishData("fire_eel", "Fire Eel", FishTier.PLATINUM, 35, 60, 1800, 1063, List.of("desert"), 2));
        FISH.add(new FishData("lava_anthias", "Lava Anthias", FishTier.PLATINUM, 14, 28, 2000, 1070, List.of("desert"), 2));
        FISH.add(new FishData("magma_trout", "Magma Trout", FishTier.PLATINUM, 25, 55, 7000, 1071, List.of("badlands"), 2));
        FISH.add(new FishData("molten_coralfish", "Molten Coralfish", FishTier.PLATINUM, 20, 42, 6500, 1072, List.of("desert"), 2));
        FISH.add(new FishData("obsidianfish", "Obsidian Fish", FishTier.PLATINUM, 22, 38, 2200, 1073, List.of("deep_dark"), 2));







        FISH.add(new FishData("void_salmon", "Void Salmon", FishTier.MYTHICAL, 50, 90, 3000, 1045, List.of("deep_dark"), 1));
        FISH.add(new FishData("shark", "Ancient Shark", FishTier.MYTHICAL, 60, 120, 5000, 1048, List.of("deep_ocean"), 1));
        FISH.add(new FishData("warped_eel", "Warped Eel", FishTier.MYTHICAL, 45, 85, 4500, 1053, List.of("deep_ocean"), 1));
        FISH.add(new FishData("warped_shrimp", "Warped Shrimp", FishTier.MYTHICAL, 8, 16, 1200, 1055, List.of("deep_cold_ocean"), 1));
        FISH.add(new FishData("blazorb_jellyfish", "Blazorb Jellyfish", FishTier.MYTHICAL, 20, 45, 4200, 1057, List.of("desert"), 1));
        FISH.add(new FishData("deepsea_tuna", "Deepsea Tuna", FishTier.MYTHICAL, 50, 100, 8500, 1080, List.of("deep_ocean"), 1));




    }



    public static int getEntropy(FishTier tier) {
        return switch (tier) {
            case BRONZE -> 15;
            case SILVER -> 30;
            case GOLD -> 60;
            case DIAMOND -> 120;
            case PLATINUM -> 200;
            case MYTHICAL -> 500;
        };
    }




    public static FishData rollFish(ServerPlayer player) {

        UUID uuid = player.getUUID();

        Map<FishTier, Double> weights = new HashMap<>();

        weights.put(FishTier.BRONZE, 50.0);
        weights.put(FishTier.SILVER, 25.0);
        weights.put(FishTier.GOLD, 15.0);
        weights.put(FishTier.DIAMOND, 7.0);
        weights.put(FishTier.PLATINUM, 2.5);
        weights.put(FishTier.MYTHICAL, 0.5);




        String lure = FishingDatabase.getLureTier(uuid);

        if (lure != null && !lure.isEmpty()) {

            try {
                FishTier boosted = FishTier.valueOf(lure);

                weights.put(boosted, weights.get(boosted) * 2.5);

            } catch (Exception ignored) {}
        }




        double total = weights.values().stream().mapToDouble(d -> d).sum();
        double roll = R.nextDouble() * total;

        FishTier chosen = FishTier.BRONZE;

        double current = 0;

        for (var entry : weights.entrySet()) {

            current += entry.getValue();

            if (roll <= current) {
                chosen = entry.getKey();
                break;
            }
        }




        List<FishData> pool = new ArrayList<>();

        for (FishData f : FISH) {
            if (f.tier == chosen) {
                pool.add(f);
            }
        }

        if (pool.isEmpty()) return FISH.get(0);

        return rollFishFromPool(pool);
    }

    public static ItemStack createFishDisplayItem(FishData fish) {

        ItemStack stack = new ItemStack(Items.COD);

        stack.set(DataComponents.CUSTOM_NAME,
                TextFormatter.parse("<yellow>&l" + fish.displayName + "</yellow>"));

        stack.set(
                DataComponents.CUSTOM_MODEL_DATA,
                new CustomModelData(
                        fish.modelData
                )
        );

        return stack;
    }




    public static ItemStack createFishItem(FishData fish, ServerPlayer player, int size) {

        ItemStack item = new ItemStack(Items.COD);

        item.set(
                DataComponents.CUSTOM_MODEL_DATA,
                new CustomModelData(fish.modelData)
        );


        CompoundTag tag = new CompoundTag();

        tag.putBoolean("pixity_fish", true);
        tag.putString("pixity_type", "fish");

        tag.putString("fish_id", fish.id);
        tag.putString("fish_tier", fish.tier.name());
        tag.putInt("fish_size", size);


        double range = Math.max(1, fish.maxSize - fish.minSize);
        double percent = (size - fish.minSize) / range;

        double value = (fish.baseValue * (1.0 + percent));

        tag.putDouble("fish_value", value);

        item.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));




        item.set(DataComponents.CUSTOM_NAME,
                TextFormatter.parse(
                        getTierColor(fish.tier) + fish.displayName
                                + " <gray>(</gray>&e" + size + "cm<gray>)</gray>"
                ));

        List<Component> lore = new ArrayList<>();

        lore.add(TextFormatter.parse("<green>Want to make a pond for this one?</green>"));
        lore.add(Component.empty());

        lore.add(TextFormatter.parse("<gold>Description:</gold>"));
        lore.add(TextFormatter.parse("<green>➤</green> <gray>Tier:</gray> " + fish.tier.name()));
        lore.add(TextFormatter.parse("<green>➤</green> <gray>Size:</gray> <yellow>" + size + "cm</yellow>"));
        lore.add(TextFormatter.parse("<green>➤</green> <gray>Value:</gray> <green>$" + value + "</green>"));

        lore.add(Component.empty());

        lore.add(TextFormatter.parse("<gold>Biomes:</gold>"));
        for (String biome : fish.biomes) {
            lore.add(TextFormatter.parse("<green>★</green> &e" + biome));
        }

        lore.add(Component.empty());

        item.set(DataComponents.LORE, new ItemLore(lore));

        return item;
    }

    public static ItemStack createLure(FishTier tier, int durationSeconds) {

        ItemStack item = switch (tier) {
            case BRONZE -> new ItemStack(Items.BROWN_DYE);
            case SILVER -> new ItemStack(Items.GRAY_DYE);
            case GOLD -> new ItemStack(Items.YELLOW_DYE);
            case DIAMOND -> new ItemStack(Items.CYAN_DYE);
            case PLATINUM -> new ItemStack(Items.LIME_DYE);
            case MYTHICAL -> new ItemStack(Items.MAGENTA_DYE);
        };

        item.set(DataComponents.CUSTOM_NAME,
                TextFormatter.parse(
                        getTierColor(tier) + tier.name() + " LURE <yellow>(Right click to activate)</yellow>"
                ));

        List<Component> lore = new ArrayList<>();

        lore.add(TextFormatter.parse("<gray>Increases chance of catching</gray>"));
        lore.add(TextFormatter.parse(getTierColor(tier) + tier.name() + " FISH"));
        lore.add(TextFormatter.parse("<gray>for</gray> <yellow>" + durationSeconds + "s</yellow>"));

        item.set(DataComponents.LORE, new ItemLore(lore));

        CompoundTag tag = new CompoundTag();
        tag.putString("pixity_type", "lure");
        tag.putString("lure_tier", tier.name());
        tag.putInt("lure_duration", durationSeconds);

        item.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        return item;
    }

    private static void tryTriggerTsunami(ServerPlayer player) {
        UUID uuid = player.getUUID();


        if (!FishingDatabase.isUnlocked(uuid, "divine_unlocked")) return;


        if (tsunamiEnd.containsKey(player.getUUID())) return;


        double chance = FishingManager.getDivineChance(uuid);
        if (!chance(chance)) return;

        tsunamiEnd.put(player.getUUID(),
                System.currentTimeMillis() + TSUNAMI_DURATION_MS);


        player.sendSystemMessage(TextFormatter.parse(
                "&a&lFISHING &7&l➤ &b&lTSUNAMI ACTIVATED!"
        ));
    }




    public static void handleCatch(ServerPlayer player, FishData fish, int size) {

        UUID uuid = player.getUUID();

        Long end = tsunamiEnd.get(uuid);

        if (end != null && System.currentTimeMillis() > end) {
            tsunamiEnd.remove(uuid);
        }

        if (FishingDatabase.isUnlocked(uuid, "combo_unlocked")) {
            checkComboTimeout(player);
            incrementCombo(player);
        } else {
            resetCombo(player);
        }






        if (chance(getCrabChance(player))) {
            spawnCrab(player);
        }




        boolean raining = player.level().isRaining();

        int xp = switch (fish.tier) {
            case BRONZE -> 15;
            case SILVER -> 50;
            case GOLD -> 120;
            case DIAMOND -> 250;
            case PLATINUM -> 500;
            case MYTHICAL -> 2500;
        };

        int entropy = getEntropy(fish.tier);
        entropy = applyCatchBonus(uuid, entropy);




        CatchContext ctx = new CatchContext(player, fish, raining);
        ctx.xp = xp;
        ctx.entropy = entropy;
        int playerXp = xp;


        int Intellectlvl = getAugmentLevel(player.getMainHandItem(), FishingAugment.INTELLECT);

        if (Intellectlvl > 0) {
            playerXp = (int) Math.round(playerXp * scaleBoost(Intellectlvl, 20, 1.0));
        }




        applyAugments(ctx);
        tryTriggerTsunami(player);









        int finalAmount = applyComboBonus(player, ctx.fishAmount);




        Long tsunamiActive = tsunamiEnd.get(player.getUUID());

        if (tsunamiActive != null && System.currentTimeMillis() < tsunamiActive) {

            if (isWithinTsunamiZone(player)) {

                finalAmount *= 2;

            }
        }




        FishingDatabase.unlockFish(uuid, fish.id);




        handleRareEffects(player, fish);

        if (fish.tier == FishTier.PLATINUM || fish.tier == FishTier.MYTHICAL) {
            spawnRareAura(player, fish.tier);
        }




        player.sendSystemMessage(TextFormatter.parse(
                "&a&lFISHING &7&l➤ <gray>You caught a</gray> "
                        + getTierColor(fish.tier)
                        + fish.displayName + "<gray>!</gray>"
        ));




        FishingDatabase.addFish(uuid, fish.tier);
        PlayerStatsDatabase.add(
                uuid,
                "total_fish_caught",
                1
        );
        MilestoneCheckService.check(
                player,
                "total_fish"
        );

        if(fish.tier == FishTier.BRONZE){
            PlayerStatsDatabase.add(uuid, "bronze_fish_caught", 1);
            MilestoneCheckService.check(
                    player,
                    "bronze_fish"
            );
        }
        if(fish.tier == FishTier.SILVER){
            PlayerStatsDatabase.add(uuid, "silver_fish_caught", 1);
            MilestoneCheckService.check(
                    player,
                    "silver_fish"
            );
        }
        if(fish.tier == FishTier.GOLD){
            PlayerStatsDatabase.add(uuid, "gold_fish_caught", 1);
            MilestoneCheckService.check(
                    player,
                    "gold_fish"
            );
        }
        if(fish.tier == FishTier.DIAMOND){
            PlayerStatsDatabase.add(uuid, "diamond_fish_caught", 1);
            MilestoneCheckService.check(
                    player,
                    "diamond_fish"
            );
        }
        if(fish.tier == FishTier.PLATINUM){
            PlayerStatsDatabase.add(uuid, "platinum_fish_caught", 1);
            MilestoneCheckService.check(
                    player,
                    "platinum_fish"
            );
        }
        if(fish.tier == FishTier.MYTHICAL){
            PlayerStatsDatabase.add(uuid, "mythical_fish_caught", 1);
            MilestoneCheckService.check(
                    player,
                    "mythical_fish"
            );
        }





        FishingDatabase.addXP(uuid, ctx.xp);
        FishingDatabase.updateFishStats(
                uuid,
                fish.id,
                size
        );
        FishingManager.checkLevelUp(player);
        FishingEventManager.onFishCatch(player, fish, size);




        player.giveExperiencePoints(playerXp);

        if (ctx.entropy > 0) {
            FishingDatabase.addEntropy(uuid, ctx.entropy);
        }

        sendComboBar(player);
    }

    private static boolean isWithinTsunamiZone(ServerPlayer player) {

        var hook = player.fishing;
        if (hook == null) return false;

        double dx = player.getX() - hook.getX();
        double dy = player.getY() - hook.getY();
        double dz = player.getZ() - hook.getZ();

        double distanceSq = dx * dx + dy * dy + dz * dz;

        return distanceSq <= (TSUNAMI_RADIUS * TSUNAMI_RADIUS);
    }


    public static void checkLevelUp(ServerPlayer player) {

        UUID uuid = player.getUUID();

        int xp = FishingDatabase.getXP(uuid);
        int currentLevel = getLevelFromXP(xp);

        try {
            var rs = FishingDatabase.getStats(uuid);

            int storedLevel = 0;

            if (rs != null && rs.next()) {
                storedLevel = rs.getInt("level");
            }




            int levelsGained = currentLevel - storedLevel;

            if (levelsGained > 0) {

                FishingDatabase.setLevel(uuid, currentLevel);


                FishingDatabase.addSkillPoints(uuid, levelsGained);




                player.sendSystemMessage(
                        TextFormatter.parse(
                                "&a&lFISHING &7&l➤ "
                                        + "<green>You reached level</green> <gold>" + currentLevel + "</gold>"
                                        + " <gray>(+" + levelsGained + " skill points)</gray>"
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static int guttingLevel(UUID uuid) {
        return FishingDatabase.getSkill(uuid, "gutting_skill");
    }

    public static int luckLevel(UUID uuid) {
        return FishingDatabase.getSkill(uuid, "luck_skill");
    }

    public static int augmenterLevel(UUID uuid) {
        return FishingDatabase.getSkill(uuid, "augment_skill");
    }

    public static boolean has(UUID uuid, String unlockCol) {
        return FishingDatabase.isUnlocked(uuid, unlockCol);
    }




    public static int applyGuttingBonus(UUID uuid, int baseEntropy) {
        int lvl = guttingLevel(uuid);
        double mult = 1.0 + getScalingBonus(lvl);
        return (int) Math.round(baseEntropy * mult);
    }

    public static int applyCatchBonus(UUID uuid, int baseEntropy) {
        int lvl = luckLevel(uuid);
        double mult = 1.0 + getScalingBonus(lvl);
        return (int) Math.round(baseEntropy * mult);
    }

    public static int getLevelFromXP(int xp) {
        return (int) Math.floor(Math.sqrt(xp / 100.0));
    }


    public static FishData getFishById(String id) {

        for (FishData fish : FISH) {
            if (fish.id.equalsIgnoreCase(id)) {
                return fish;
            }
        }

        return null;
    }




    public static String getTierColor(FishTier tier) {
        return switch (tier) {
            case BRONZE -> "&#cd7f32&l";
            case SILVER -> "&#bdbdbd&l";
            case GOLD -> "&#f5f788&l";
            case DIAMOND -> "&#57f2e8&l";
            case PLATINUM -> "&#2deb95&l";
            case MYTHICAL -> "&#db2deb&l";
        };
    }

    private static CompoundTag getData(ItemStack stack) {

        CustomData data = stack.get(DataComponents.CUSTOM_DATA);

        if (data == null) return new CompoundTag();

        return data.copyTag();
    }


    public static boolean isFish(ItemStack stack) {
        CompoundTag tag = getData(stack);
        return tag != null && tag.getBoolean("pixity_fish");
    }

    public static String getFishId(ItemStack stack) {
        return getData(stack).getString("fish_id");
    }

    public static FishTier getFishTier(ItemStack stack) {
        return FishTier.valueOf(getData(stack).getString("fish_tier"));
    }

    public static int getFishSize(ItemStack stack) {
        return getData(stack).getInt("fish_size");
    }

    public static double getFishValue(ItemStack stack) {
        return getData(stack).getDouble("fish_value");
    }


    public static void giveFish(ServerPlayer player, FishData fish, int size) {

        ItemStack item = createFishItem(fish, player, size);



        FishingDatabase.RecordResult result =
                FishingDatabase.updateSize(player.getUUID(), size);




        if (result.longest() || result.shortest()) {

            boolean isLongest = result.longest();

            int rank = FishingDatabase.getRank(
                    player.getUUID(),
                    isLongest ? "longest_fish" : "shortest_fish",
                    !isLongest
            );

            String type = isLongest ? "Biggest Fish" : "Smallest Fish";

            player.sendSystemMessage(TextFormatter.parse(
                    "<gold>&lNEW FISHING RECORD!</gold>"
            ));

            player.sendSystemMessage(TextFormatter.parse(
                    "<yellow>" + type + "</yellow>: &b" + size + "cm"
            ));

            if (rank > 0) {
                player.sendSystemMessage(TextFormatter.parse(
                        "&a&lFISHING &7&l➤ <green>You are now</green> <gold>#" + rank + "</gold> <green>on the leaderboard</green>"
                ));
            }
        }

        if (!player.getInventory().add(item)) {
            player.drop(item, false);
        }
    }


    private static void handleRareEffects(ServerPlayer player, FishData fish) {

        FishTier tier = fish.tier;




        if (tier == FishTier.PLATINUM) {


            player.serverLevel().sendParticles(
                    ParticleTypes.END_ROD,
                    player.getX(), player.getY() + 1, player.getZ(),
                    40,
                    1, 1, 1,
                    0.05
            );


            player.server.getPlayerList().broadcastSystemMessage(
                    TextFormatter.parse(
                            "&a&lFISHING &7&l➤ &#2deb95&l✦ RARE CATCH ✦<yellow>"
                                    + player.getName().getString()
                                    + "</yellow> &#2deb95caught a "
                                    + getTierColor(tier)
                                    + fish.displayName
                                    + "<yellow>!</yellow>"
                    ),
                    false
            );
        }




        if (tier == FishTier.MYTHICAL) {


            player.serverLevel().sendParticles(
                    ParticleTypes.FIREWORK,
                    player.getX(), player.getY() + 1, player.getZ(),
                    80,
                    1.5, 1.5, 1.5,
                    0.2
            );

            player.serverLevel().sendParticles(
                    ParticleTypes.END_ROD,
                    player.getX(), player.getY() + 1, player.getZ(),
                    60,
                    1, 1, 1,
                    0.1
            );


            player.server.getPlayerList().broadcastSystemMessage(
                    TextFormatter.parse(
                            "&a&lFISHING &7&l➤ <rainbow>&l✦ MYTHICAL CATCH ✦</rainbow> <yellow>"
                                    + player.getName().getString()
                                    + "</yellow> <rainbow>pulled a</rainbow> "
                                    + getTierColor(tier)
                                    + fish.displayName
                                    + " <rainbow>from the depths!</rainbow>"
                    ),
                    false
            );
        }
    }

    private static void spawnRareAura(ServerPlayer player, FishTier tier) {

        int duration = tier == FishTier.MYTHICAL ? 100 : 60;

        player.server.tell(new net.minecraft.server.TickTask(0, new Runnable() {

            int ticks = 0;

            @Override
            public void run() {

                if (ticks > duration) return;

                if (tier == FishTier.PLATINUM) {
                    player.serverLevel().sendParticles(
                            ParticleTypes.END_ROD,
                            player.getX(), player.getY() + 0.5, player.getZ(),
                            3,
                            0.4, 0.3, 0.4,
                            0.02
                    );
                }

                if (tier == FishTier.MYTHICAL) {
                    player.serverLevel().sendParticles(
                            ParticleTypes.ENCHANT,
                            player.getX(), player.getY() + 0.5, player.getZ(),
                            6,
                            0.6, 0.4, 0.6,
                            0.05
                    );
                }

                ticks++;
                player.server.tell(new net.minecraft.server.TickTask(2, this));
            }
        }));
    }

    public static void setScaleMultiplier(ItemStack stack, double multiplier) {

        CompoundTag tag = getData(stack);

        tag.putDouble("scale_mult", multiplier);

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static double getScaleMultiplier(ItemStack stack) {

        CompoundTag tag = getData(stack);

        if (!tag.contains("scale_mult")) return 1.0;

        return tag.getDouble("scale_mult");
    }

    public static double rollScaleMultiplier(String risk) {

        double variance;

        switch (risk) {
            case "LOW" -> variance = 0.20;
            case "MEDIUM" -> variance = 0.30;
            case "HIGH" -> variance = 0.40;
            case "EXTREME" -> variance = 0.60;
            default -> variance = 0.20;
        }



        double change = (Math.random() * variance * 2) - variance;

        return 1.0 + change;
    }

    public static void addAugment(ItemStack rod, FishingAugment aug) {

        CustomData data = rod.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = data != null ? data.copyTag() : new CompoundTag();

        CompoundTag augments;

        if (tag.contains("augments")) {
            augments = tag.getCompound("augments");
        } else {
            augments = new CompoundTag();
        }

        int current = augments.getInt(aug.name());
        int next = current + 1;

        int max = getMaxLevel(aug);

        if (next > max) next = max;

        augments.putInt(aug.name(), next);
        tag.put("augments", augments);

        rod.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        updateRodLore(rod);
    }

    public static int getAugmentLevel(ItemStack rod, FishingAugment aug) {

        CompoundTag tag = getData(rod);

        if (!tag.contains("augments")) return 0;

        CompoundTag augments = tag.getCompound("augments");

        return augments.getInt(aug.name());
    }

    public static int getMaxLevel(FishingAugment aug) {
        return switch (aug) {
            case HOTSPOT, STORM -> 15;
            case SATURATION, SAGE, BIOME, PRECISION, CRAB_BAIT, PERCEPTION, TROPHY -> 10;
            case INTELLECT, SOLAR, MASTER -> 20;
        };
    }

    public static double scaleChance(int level, int maxLevel, double maxChance) {
        return (level / (double) maxLevel) * maxChance;
    }

    public static double scaleBoost(int level, int maxLevel, double maxBoost) {
        return 1.0 + ((level / (double) maxLevel) * maxBoost);
    }

    public static int getMasterBonusFish(int level) {

        int maxFish = 4;

        double chanceForMax = (level / 20.0) * 0.10;

        if (Math.random() < chanceForMax) {
            return maxFish;
        }

        return 1 + new Random().nextInt(2);
    }


    public static ItemStack createAugmentItem(FishingAugment aug, ServerPlayer player) {

        ItemStack item = new ItemStack(Items.NETHER_STAR);




        item.set(DataComponents.CUSTOM_NAME,
                TextFormatter.parse("<gold>&l" + aug.display + "</gold>"));




        List<Component> lore = new ArrayList<>();

        lore.add(TextFormatter.parse("<gray>Fishing Augment</gray>"));
        lore.add(Component.empty());

        for (FishingAugment.Req req : aug.requirements) {

            String display;





            if (req.item != null) {

                display =
                        "&e&l" +
                        new ItemStack(req.item)
                                .getHoverName()
                                .getString();
            }





            else {

                display = switch (req.customId) {

                    case "crab_claw" ->
                            "&c&lCrab Claw";

                    case "crab_scale" ->
                            "&c&lCrab Scale";

                    case "dolphin_treasure" ->
                            "&b&lDolphin Treasure";

                    case "squid_tentacle" ->
                            "&8&lSquid Tentacle";

                    default ->
                            req.customId
                                    .replace("_", " ");
                };
            }

            lore.add(
                    TextFormatter.parse(
                            "<gray>- </gray>"
                                    + "<yellow>"
                                    + req.amount
                                    + "x </yellow>"
                                    + display
                    )
            );
        }

        lore.add(Component.empty());
        lore.add(TextFormatter.parse("<yellow>Cost:</yellow> &b" + aug.entropyCost + " &bEntropy"));

        item.set(DataComponents.LORE, new ItemLore(lore));




        CompoundTag tag = new CompoundTag();
        tag.putString("augment_id", aug.name());

        item.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        return item;
    }

    public static FishingAugment getAugment(ItemStack stack) {

        if (stack == null || stack.isEmpty()) return null;

        CustomData data = stack.get(DataComponents.CUSTOM_DATA);

        if (data == null) return null;

        CompoundTag tag = data.copyTag();

        if (!tag.contains("augment_id")) return null;

        try {
            return FishingAugment.valueOf(tag.getString("augment_id"));
        } catch (Exception e) {
            return null;
        }
    }


    public static boolean hasAug(ServerPlayer player, FishingAugment aug) {
        return getAugmentLevel(player.getMainHandItem(), aug) > 0;
    }

    private static void updateRodLore(ItemStack rod) {

        CustomData data = rod.get(DataComponents.CUSTOM_DATA);
        if (data == null) return;

        CompoundTag tag = data.copyTag();
        if (!tag.contains("augments")) return;

        CompoundTag augments = tag.getCompound("augments");

        List<Component> lore = new ArrayList<>();

        lore.add(TextFormatter.parse("<gray>Augments:</gray>"));
        lore.add(Component.empty());

        for (String key : augments.getAllKeys()) {

            FishingAugment aug;

            try {
                aug = FishingAugment.valueOf(key);
            } catch (Exception e) {
                continue;
            }

            int lvl = augments.getInt(key);
            int max = getMaxLevel(aug);


            String color = switch (aug) {
                case HOTSPOT -> "&#ff9a00";
                case STORM -> "&#00c3ff";
                case SATURATION -> "&#ff5555";
                case SAGE -> "&#9cff00";
                case BIOME -> "&#00ff95";
                case PRECISION -> "&#ffffff";
                case CRAB_BAIT -> "&#ff4d4d";
                case INTELLECT -> "&#5dade2";
                case PERCEPTION -> "&#d980fa";
                case TROPHY -> "&#f1c40f";
                case SOLAR -> "&#ffcc00";
                case MASTER -> "&#ff0000";
            };

            lore.add(TextFormatter.parse(
                    color + "&l" + aug.display +
                            " <gray>(</gray><yellow>" + lvl + "</yellow><gray>/</gray><red>" + max + "</red><gray>)</gray>"
            ));
        }

        rod.set(DataComponents.LORE, new ItemLore(lore));
    }

    public static boolean chance(double chance) {
        return Math.random() < chance;
    }

    public static double getAugmentDiscount(UUID uuid) {

        int level = FishingDatabase.getSkill(uuid, "augment_skill");


        return Math.min(getScalingBonus(level), 0.50);
    }

    private static void spawnCrab(ServerPlayer player) {

        var level = player.serverLevel();

        var crab = net.minecraft.world.entity.EntityType.SILVERFISH.create(level);

        if (crab == null) return;

        crab.moveTo(
                player.getX(),
                player.getY(),
                player.getZ(),
                player.getYRot(),
                player.getXRot()
        );




        crab.setCustomName(
                TextFormatter.parse("&c&lCRAB")
        );
        crab.setCustomNameVisible(true);




        crab.getPersistentData().putBoolean("pixity_crab", true);

        level.addFreshEntity(crab);

        player.sendSystemMessage(
                TextFormatter.parse("&a&lFISHING &7&l➤ <gray>A</gray> <red>crab</red> <green>appeared</green><gray>!</gray>")
        );
    }

    private static double getCrabChance(ServerPlayer player) {

        double base = 0.05;

        if (!hasAug(player, FishingAugment.CRAB_BAIT)) {
            return base;
        }

        int crabBaitLvl = getAugmentLevel(player.getMainHandItem(), FishingAugment.CRAB_BAIT);

        double bonus = scaleChance(crabBaitLvl, 10, 0.50);

        return base + bonus;
    }

    public static ItemStack createCustomItem(String id) {

        ItemStack item;

        int modelData = 0;

        switch (id) {

            case "crab_claw" -> {
                item = new ItemStack(net.minecraft.world.item.Items.QUARTZ);
                modelData = 0;
            }

            case "crab_scale" -> {
                item = new ItemStack(net.minecraft.world.item.Items.GREEN_DYE);
                modelData = 0;
            }

            case "dolphin_treasure" -> {
                item = new ItemStack(net.minecraft.world.item.Items.COD);
                modelData = 1998;
            }

            case "squid_tentacle" -> {
                item = new ItemStack(net.minecraft.world.item.Items.COD);
                modelData = 1999;
            }

            default -> {
                return ItemStack.EMPTY;
            }
        }




        if (modelData > 0) {

            item.set(
                    DataComponents.CUSTOM_MODEL_DATA,
                    new net.minecraft.world.item.component.CustomModelData(modelData)
            );
        }




        String name = switch (id) {

            case "crab_claw" -> "&c&lCRAB CLAW";

            case "crab_scale" -> "&c&lCRAB SCALE";

            case "dolphin_treasure" ->
                    "&#57D4FF&lDolphin Treasure";

            case "squid_tentacle" ->
                    "&#A855F7&lSquid Tentacle";

            default -> "<white>Unknown";
        };

        item.set(DataComponents.CUSTOM_NAME,
                TextFormatter.parse(name));




        java.util.List<net.minecraft.network.chat.Component> lore =
                new java.util.ArrayList<>();

        lore.add(TextFormatter.parse(
                "<gray>Used in</gray> <red>augment</red> <gray>crafting</gray>"
        ));

        lore.add(TextFormatter.parse(
                "<yellow>/fishing augments</yellow>"
        ));

        item.set(
                DataComponents.LORE,
                new net.minecraft.world.item.component.ItemLore(lore)
        );




        net.minecraft.nbt.CompoundTag tag =
                new net.minecraft.nbt.CompoundTag();

        tag.putString("pixity_item", id);

        item.set(
                net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(tag)
        );




        item.set(
                DataComponents.ENCHANTMENT_GLINT_OVERRIDE,
                true
        );

        return item;
    }

    public static String getCustomItemId(ItemStack stack) {

        var tag = getData(stack);

        if (!tag.contains("pixity_item")) return null;

        return tag.getString("pixity_item");
    }

    public static void handleCrabDrop(net.minecraft.world.entity.Entity entity) {

        if (!entity.getPersistentData().getBoolean("pixity_crab")) return;

        if (!(entity.level() instanceof net.minecraft.server.level.ServerLevel level)) return;

        var pos = entity.blockPosition();




        level.addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(
                level,
                pos.getX(), pos.getY(), pos.getZ(),
                createCustomItem("crab_scale")
        ));




        if (Math.random() < 0.5) {
            level.addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(
                    level,
                    pos.getX(), pos.getY(), pos.getZ(),
                    createCustomItem("crab_claw")
            ));
        }
    }

    public static void handleMobDrops(net.minecraft.world.entity.Entity entity) {

        if (!(entity.level() instanceof net.minecraft.server.level.ServerLevel level)) return;

        var pos = entity.blockPosition();




        if (entity instanceof net.minecraft.world.entity.animal.Dolphin) {

            if (Math.random() < 0.05) {
                level.addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(
                        level,
                        pos.getX(), pos.getY(), pos.getZ(),
                        createCustomItem("dolphin_treasure")
                ));
            }
        }




        if (entity instanceof net.minecraft.world.entity.animal.Squid) {

            if (Math.random() < 0.20) {
                level.addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(
                        level,
                        pos.getX(), pos.getY(), pos.getZ(),
                        createCustomItem("squid_tentacle")
                ));
            }
        }
    }
    public static boolean hasMaterials(ServerPlayer player, FishingAugment aug) {

        Map<String, Integer> found = new HashMap<>();

        for (ItemStack stack : player.getInventory().items) {

            if (stack.isEmpty()) continue;

            String id = FishingManager.getCustomItemId(stack);

            if (id == null) continue;

            found.merge(id, stack.getCount(), Integer::sum);
        }

        for (FishingAugment.Req req : aug.requirements) {

            if (req.customId != null) {

                int have = found.getOrDefault(req.customId, 0);

                if (have < req.amount) {
                    return false;
                }
            }


            if (req.item != null) {

                int count = 0;

                for (ItemStack stack : player.getInventory().items) {
                    if (stack.getItem() == req.item) {
                        count += stack.getCount();
                    }
                }

                if (count < req.amount) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void removeMaterials(ServerPlayer player, FishingAugment aug) {

        for (FishingAugment.Req req : aug.requirements) {
            removeSpecificMaterial(player, req, req.amount);
        }
    }


    public static int rollFishSize(FishData fish) {
        return fish.minSize + R.nextInt(fish.maxSize - fish.minSize + 1);
    }

    public static class CatchContext {

        public ServerPlayer player;
        public FishData fish;

        public boolean raining;

        public int fishAmount = 1;
        public int xp = 0;
        public int entropy = 0;

        public double moneyMultiplier = 1.0;

        public CatchContext(ServerPlayer player, FishData fish, boolean raining) {
            this.player = player;
            this.fish = fish;
            this.raining = raining;
        }
    }

    public static void applyAugments(CatchContext ctx) {

        ServerPlayer player = ctx.player;




        int hotspotLvl = getAugmentLevel(player.getMainHandItem(), FishingAugment.HOTSPOT);

        if (!ctx.raining && hotspotLvl > 0) {
            double chance = scaleChance(hotspotLvl, 15, 0.75);

            if (chance(chance)) {
                ctx.fishAmount++;
            }
        }




        int StormLVL = getAugmentLevel(player.getMainHandItem(), FishingAugment.STORM);

        if (ctx.raining && StormLVL > 0) {
            double chance = scaleChance(StormLVL, 15, 0.75);

            if (chance(chance)) {
                ctx.fishAmount++;
            }
        }




        int SaturationLVL = getAugmentLevel(player.getMainHandItem(), FishingAugment.SATURATION);

        if (SaturationLVL > 0) {
            double chance = scaleChance(SaturationLVL, 10, 1.0);

            if (chance(chance)) {
                player.getFoodData().eat(4, 0.4f);
            }
        }




        int SageLVL = getAugmentLevel(player.getMainHandItem(), FishingAugment.SAGE);

        if (SageLVL > 0) {
            ctx.xp *= scaleBoost(SageLVL, 10, 0.50);
        }





        int PerceptionLVL = getAugmentLevel(player.getMainHandItem(), FishingAugment.PERCEPTION);

        if (PerceptionLVL > 0 && ctx.fish.tier.ordinal() >= FishTier.DIAMOND.ordinal()) {
            ctx.entropy *= scaleBoost(PerceptionLVL, 10, 0.50);
        }




        int MasterLVL = getAugmentLevel(player.getMainHandItem(), FishingAugment.MASTER);

        if (MasterLVL > 0) {

            double chance = scaleChance(MasterLVL, 20, 0.60);

            if (chance(chance)) {
                ctx.fishAmount += getMasterBonusFish(MasterLVL);
            }
        }




        int BiomeLVL = getAugmentLevel(player.getMainHandItem(), FishingAugment.BIOME);

        if (BiomeLVL > 0) {
            double chance = scaleChance(BiomeLVL, 10, 0.50);

            if (chance(chance)) {
                ctx.fish = getRandomFishFromOtherBiome(ctx.player);
            }
        }
    }

    public static FishData getRandomFishFromOtherBiome(ServerPlayer player) {

        String currentBiome = player.level()
                .getBiome(player.blockPosition())
                .unwrapKey()
                .map(key -> key.location().getPath())
                .orElse("plains");




        List<FishData> pool = new ArrayList<>();

        for (FishData fish : FISH) {
            if (!fish.biomes.contains(currentBiome)) {
                pool.add(fish);
            }
        }

        if (pool.isEmpty()) return rollFish(player);




        return rollFishFromPool(pool);
    }

    public static FishData rollFishFromPool(List<FishData> pool) {

        int totalWeight = 0;

        for (FishData fish : pool) {
            totalWeight += fish.weight;
        }

        int roll = new Random().nextInt(totalWeight);

        int current = 0;

        for (FishData fish : pool) {
            current += fish.weight;

            if (roll < current) {
                return fish;
            }
        }

        return pool.get(0);
    }

    private static int getTierWeight(FishTier tier) {
        return switch (tier) {
            case BRONZE -> 50;
            case SILVER -> 30;
            case GOLD -> 12;
            case DIAMOND -> 5;
            case PLATINUM -> 2;
            case MYTHICAL -> 1;
        };
    }

    public static int getCombo(ServerPlayer player) {
        return comboMap.getOrDefault(player.getUUID(), 0);
    }

    public static void resetCombo(ServerPlayer player) {
        comboMap.remove(player.getUUID());
        comboTimeout.remove(player.getUUID());
    }

    public static void incrementCombo(ServerPlayer player) {

        UUID uuid = player.getUUID();

        int combo = comboMap.getOrDefault(uuid, 0) + 1;

        int maxCombo = getMaxCombo(player);

        if (combo > maxCombo) combo = maxCombo;

        comboMap.put(uuid, combo);
        comboTimeout.put(uuid, System.currentTimeMillis());
    }

    public static int getMaxCombo(ServerPlayer player) {

        int base = 30;

        int level = FishingDatabase.getSkill(player.getUUID(), "combo_skill");

        int bonus = (level / 20) * 5;

        return base + bonus;
    }

    public static void checkComboTimeout(ServerPlayer player) {

        UUID uuid = player.getUUID();

        long last = comboTimeout.getOrDefault(uuid, 0L);

        if (System.currentTimeMillis() - last > COMBO_RESET_TIME) {
            resetCombo(player);
        }
    }

    public static int applyComboBonus(ServerPlayer player, int fishAmount) {

        UUID uuid = player.getUUID();

        if (!FishingDatabase.isUnlocked(uuid, "combo_unlocked")) {
            return fishAmount;
        }

        int combo = getCombo(player);
        if (combo <= 0) return fishAmount;

        int level = FishingDatabase.getLevel(uuid);

        double chance = getComboChance(uuid);


        chance += combo * 0.005;


        chance += level * 0.001;

        chance = Math.min(chance, 0.75);

        if (chance(chance)) {


            int bonus = 1 + (combo / 25);

            return fishAmount + bonus;
        } else {
            resetCombo(player);
        }

        return fishAmount;
    }

    public static void sendComboBar(ServerPlayer player) {

        UUID uuid = player.getUUID();


        if (!FishingDatabase.isUnlocked(uuid, "combo_unlocked")) return;

        int combo = getCombo(player);

        if (combo <= 0) return;

        player.displayClientMessage(
                TextFormatter.parse("<gold>Combo:</gold> <yellow>" + combo + "</yellow>"),
                true
        );
    }

    public static double getScalingBonus(int level) {

        double bonus = 0;

        double increment = 0.01;

        for (int i = 0; i < level; i++) {
            bonus += increment;
            increment = Math.max(0.0005, increment - 0.0002);
        }

        return bonus;
    }





    public static double getDivineChance(UUID uuid) {
        int level = FishingDatabase.getLevel(uuid);
        return Math.min(0.15, (level / 10) * 0.02);
    }

    public static double getComboChance(UUID uuid) {

        int level = FishingDatabase.getLevel(uuid);


        double base = level * 0.002;

        return Math.min(0.50, base);
    }

    public static double getInfusionChance(UUID uuid) {
        int level = FishingDatabase.getLevel(uuid);
        return Math.min(0.75, (level / 7) * 0.05);
    }

    public static void removeSpecificMaterial(ServerPlayer player, FishingAugment.Req req, int amount) {

        int remaining = amount;

        for (int i = 0; i < player.getInventory().items.size(); i++) {

            ItemStack stack = player.getInventory().items.get(i);

            if (stack.isEmpty()) continue;




            if (req.customId != null) {

                String id = FishingManager.getCustomItemId(stack);

                if (!req.customId.equals(id)) continue;

            } else if (req.item != null) {

                if (stack.getItem() != req.item) continue;

            } else {
                continue;
            }

            int remove = Math.min(remaining, stack.getCount());

            stack.shrink(remove);
            remaining -= remove;

            if (stack.isEmpty()) {
                player.getInventory().items.set(i, ItemStack.EMPTY);
            }

            if (remaining <= 0) return;
        }
    }

    public static void updateFishLore(ItemStack stack) {

        List<Component> lore = new ArrayList<>();


        var existing = stack.get(DataComponents.LORE);
        if (existing != null) {
            lore.addAll(existing.lines());
        }

        double mult = getScaleMultiplier(stack);

        lore.add(Component.empty());
        lore.add(TextFormatter.parse("<gray>Scaled Value:</gray> <green>x" + String.format("%.2f", mult) + "</green>"));

        stack.set(DataComponents.LORE,
                new net.minecraft.world.item.component.ItemLore(lore));
    }

    public static boolean hasBeenScaled(ItemStack stack) {

        var data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return false;

        var tag = data.copyTag();

        return tag.contains("scaled");
    }


    public static boolean isFishingRod(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;

        if (stack.is(Items.FISHING_ROD)) return true;


        CompoundTag tag = getData(stack);
        return tag.getBoolean("pixity_fishing_rod");
    }

}