package org.howie.pixity.moderation.neoforge.chat.cosmetics;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemLore;

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.economy.CurrencyType;
import org.howie.pixity.moderation.neoforge.economy.EconomyService;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.shop.EconomyBridge;
import org.howie.pixity.moderation.neoforge.shop.ShopService;
import org.howie.pixity.moderation.neoforge.shop.ShopSortType;
import org.howie.pixity.moderation.neoforge.shop.gui.ShopCategoryMenu;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.*;

public class CosmeticsGui {

    private final ChatCosmeticsService cosmetics;
    private final ChatCosmeticsConfig config;
    private final EconomyService economy;
    private final RankService ranks;
    private final ShopService shopService;
    private final EconomyBridge econ;
    private boolean nameMode = false;

    private int page = 0;
    private static final int ITEMS_PER_PAGE = 28;


    private enum MenuMode {
        MAIN,
        CHAT,
        NAME,
        GLOW
    }


    private MenuMode mode = MenuMode.MAIN;

    public CosmeticsGui(ChatCosmeticsService cosmetics,
                        ChatCosmeticsConfig config,
                        EconomyService economy,
                        RankService ranks,
                        EconomyBridge econ,
                        ShopService shopService) {
        this.cosmetics = cosmetics;
        this.config = config;
        this.economy = economy;
        this.ranks = ranks;
        this.econ = econ;
        this.shopService = shopService;
    }

    private static class TagData {
        String id;
        double price;
        boolean legendary;

        TagData(String id, double price, boolean legendary) {
            this.id = id;
            this.price = price;
            this.legendary = legendary;
        }
    }

    public void openMain(ServerPlayer p) {

        mode = MenuMode.MAIN;
        nameMode = false;

        CosmeticsContainer cont =
                new CosmeticsContainer(p, cosmetics, config, economy, this, ranks);

        fill(cont);


        ItemStack name = new ItemStack(Items.NAME_TAG);
        name.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&b&lName Colors"));

        cont.setItem(20, name);
        cont.registerButton(20, () -> openName(p));

        ItemStack glow = new ItemStack(Items.NETHER_STAR);

        boolean unlocked = ranks.hasPerm(p, "pixity.cosmetic.glow");
        boolean enabled = cosmetics.hasGlow(p.getUUID());

        glow.set(
                DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&b&lPlayer Glow")
        );

        List<Component> lore = new ArrayList<>();

        lore.add(LegacyAmpersand.parse("&eDescription:"));
        lore.add(LegacyAmpersand.parse("&7Stand out from other players by having a &bshiny glow!"));
        lore.add(Component.literal(""));

        lore.add(LegacyAmpersand.parse(
                "&7Status: " + (unlocked ? "&aUNLOCKED" : "&cLOCKED")
        ));

        lore.add(LegacyAmpersand.parse(
                "&7Toggled: " + (enabled ? "&aON" : "&cOFF")
        ));

        lore.add(Component.literal(""));

        lore.add(LegacyAmpersand.parse("&7Unlock in &e/shop"));

        glow.set(DataComponents.LORE, new ItemLore(lore));

        cont.setItem(22, glow);
        cont.registerButton(22, () -> {

            String perm = "pixity.cosmetic.glow";

            if (!ranks.hasPerm(p, perm)) {
                p.sendSystemMessage(
                        LegacyAmpersand.parse("&9&lCOSMETICS &7>> &cUnlock this in /shop!")
                );
                return;
            }

            cosmetics.toggleGlow(p);
            openMain(p);
        });

        ItemStack chat = new ItemStack(Items.WRITABLE_BOOK);
        chat.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&a&lChat Colors"));

        cont.setItem(24, chat);
        cont.registerButton(24, () -> open(p));

        ItemStack clear = new ItemStack(Items.BARRIER);
        clear.set(
                DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&c&lClear All Cosmetics")
        );

        clear.set(
                DataComponents.LORE,
                new ItemLore(List.of(
                        LegacyAmpersand.parse("&7Removes:"),
                        LegacyAmpersand.parse("&8• &7Chat Color"),
                        LegacyAmpersand.parse("&8• &7Chat Gradient"),
                        LegacyAmpersand.parse("&8• &7Name Color"),
                        LegacyAmpersand.parse("&8• &7Name Gradient"),
                        LegacyAmpersand.parse("&8• &7Player Glow"),
                        Component.literal(""),
                        LegacyAmpersand.parse("&cClick to reset everything")
                ))
        );

        cont.setItem(49, clear);

        cont.registerButton(49, () -> {

            cosmetics.clearChat(p.getUUID());
            cosmetics.clearName(p.getUUID());
            cosmetics.clearGlow(p);
            org.howie.pixity.moderation.neoforge.chat.cosmetics.CosmeticService.clear(p);

            p.setGlowingTag(false);

            reopenLast(p);
        });


        ItemStack aura = new ItemStack(Items.NETHER_STAR);

        aura.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&5&lAuras"));

        cont.setItem(28, aura);
        cont.registerButton(28, () -> openAuras(p));


        ItemStack wings = new ItemStack(Items.ELYTRA);
        wings.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&e&lWings"));

        cont.setItem(30, wings);
        cont.registerButton(30, () -> openWings(p));


        ItemStack trails = new ItemStack(Items.BLAZE_POWDER);
        trails.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&c&lPlayer Trails"));

        cont.setItem(32, trails);
        cont.registerButton(32, () -> openTrails(p));


        ItemStack tags = new ItemStack(Items.NAME_TAG);
        tags.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&9&lName Tags"));

        cont.setItem(34, tags);
        cont.registerButton(34, () -> openTags(p));

        p.openMenu(new SimpleMenuProvider(
                (id, inv, player) ->
                        new ChestMenu(MenuType.GENERIC_9x6, id, inv, cont, 6),
                Component.literal("§6Cosmetics")
        ));
    }

    public void openAuras(ServerPlayer p) {

        CosmeticsContainer cont =
                new CosmeticsContainer(p, cosmetics, config, economy, this, ranks);

        fill(cont);

        int slot = 10;

        String[] auras = {"flame", "smoke", "magic", "cloud", "rainbow"};

        for (String aura : auras) {

            ItemStack it = new ItemStack(Items.NETHER_STAR);

            String perm = "pixity.cosmetic.auras." + aura;

            boolean unlocked = ranks.hasPerm(p, perm);
            boolean equipped = aura.equalsIgnoreCase(CosmeticService.getAura(p));

            it.set(DataComponents.CUSTOM_NAME, Component.literal(aura.toUpperCase()));

            List<Component> lore = new ArrayList<>();
            lore.add(LegacyAmpersand.parse("&7Aura Cosmetic"));
            lore.add(Component.literal(""));

            if (!unlocked) {
                lore.add(LegacyAmpersand.parse("&cLOCKED"));
                lore.add(LegacyAmpersand.parse("&7Click to open shop"));
            } else {
                lore.add(LegacyAmpersand.parse(
                        "&eStatus: " + (equipped ? "&aEQUIPPED" : "&7Click to equip")
                ));
            }

            it.set(DataComponents.LORE, new ItemLore(lore));

            if (unlocked) it.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

            cont.setItem(slot, it);

            cont.registerButton(slot, () -> {
                if (!unlocked) {
                    p.sendSystemMessage(
                            LegacyAmpersand.parse("&9&lCOSMETICS &7>> &eOpening shop...")
                    );

                    p.closeContainer();

                    ShopCategoryMenu.open(
                            p,
                            shopService,
                            econ,
                            ranks,
                            "tokens:auras",
                            0,
                            ShopSortType.NONE
                    );

                    return;
                }

                CosmeticService.setAura(p, aura);
                openAuras(p);
            });

            slot++;
        }

        addBack(cont, p);

        p.openMenu(new SimpleMenuProvider(
                (id, inv, player) ->
                        new ChestMenu(MenuType.GENERIC_9x6, id, inv, cont, 6),
                Component.literal("§5Auras")
        ));
    }

    public void openWings(ServerPlayer p) {

        CosmeticsContainer cont =
                new CosmeticsContainer(p, cosmetics, config, economy, this, ranks);

        fill(cont);

        int slot = 10;

        String[] wings = {
                "angel", "dragon", "pixie", "bee", "rainbow",
                "inferno", "frost", "storm",
                "void", "celestial", "galaxy",
                "neon", "pulse", "hologram",
                "phantom"
        };

        for (String wing : wings) {

            ItemStack it = new ItemStack(Items.ELYTRA);

            String perm = "pixity.cosmetic.wings." + wing;

            boolean unlocked = ranks.hasPerm(p, perm);
            boolean equipped = wing.equalsIgnoreCase(CosmeticService.getWings(p));

            Component name = Component.literal(wing.toUpperCase());

            it.set(DataComponents.CUSTOM_NAME, name);

            List<Component> lore = new ArrayList<>();
            lore.add(LegacyAmpersand.parse("&7Wings Cosmetic"));
            lore.add(Component.literal(""));

            if (!unlocked) {
                lore.add(LegacyAmpersand.parse("&cLOCKED"));
                lore.add(LegacyAmpersand.parse("&7Click to open shop"));
            } else {
                lore.add(LegacyAmpersand.parse(
                        "&eStatus: " + (equipped ? "&aEQUIPPED" : "&7Click to equip")
                ));
            }

            it.set(DataComponents.LORE, new ItemLore(lore));

            if (unlocked) {
                it.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
            }

            cont.setItem(slot, it);

            cont.registerButton(slot, () -> {

                if (!unlocked) {
                    p.sendSystemMessage(
                            LegacyAmpersand.parse("&9&lCOSMETICS &7>> &eOpening shop...")
                    );

                    p.closeContainer();

                    ShopCategoryMenu.open(
                            p,
                            shopService,
                            econ,
                            ranks,
                            "tokens:wings",
                            0,
                            ShopSortType.NONE
                    );

                    return;
                }

                CosmeticService.setWings(p, wing);
                openWings(p);
            });

            slot++;
        }

        addBack(cont, p);

        p.openMenu(new SimpleMenuProvider(
                (id, inv, player) ->
                        new ChestMenu(MenuType.GENERIC_9x6, id, inv, cont, 6),
                Component.literal("§eWings")
        ));
    }

    public void openTrails(ServerPlayer p) {

        CosmeticsContainer cont =
                new CosmeticsContainer(p, cosmetics, config, economy, this, ranks);

        fill(cont);

        int slot = 10;

        String[] trails = {
                "basic",
                "flame",
                "magic",
                "cloud",
                "rainbow",
                "wave",
                "spiral",
                "pulse",
                "velocity",
                "ribbon",
                "burst"
        };

        for (String trail : trails) {

            ItemStack it = new ItemStack(Items.BLAZE_POWDER);

            String perm = "pixity.cosmetic.trails." + trail;

            boolean unlocked = ranks.hasPerm(p, perm);
            boolean equipped = trail.equalsIgnoreCase(CosmeticService.getTrail(p));

            it.set(DataComponents.CUSTOM_NAME, Component.literal(trail.toUpperCase()));

            List<Component> lore = new ArrayList<>();
            lore.add(LegacyAmpersand.parse("&7Trail Cosmetic"));
            lore.add(Component.literal(""));

            if (!unlocked) {
                lore.add(LegacyAmpersand.parse("&cLOCKED"));
                lore.add(LegacyAmpersand.parse("&7Click to open shop"));
            } else {
                lore.add(LegacyAmpersand.parse(
                        "&eStatus: " + (equipped ? "&aEQUIPPED" : "&7Click to equip")
                ));
            }

            it.set(DataComponents.LORE, new ItemLore(lore));

            if (unlocked) it.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

            cont.setItem(slot, it);

            cont.registerButton(slot, () -> {
                if (!unlocked) {
                    p.sendSystemMessage(
                            LegacyAmpersand.parse("&9&lCOSMETICS &7>> &eOpening shop...")
                    );

                    p.closeContainer();

                    ShopCategoryMenu.open(
                            p,
                            shopService,
                            econ,
                            ranks,
                            "tokens:trails",
                            0,
                            ShopSortType.NONE
                    );

                    return;
                }

                CosmeticService.setTrail(p, trail);
                openTrails(p);
            });

            slot++;
        }

        addBack(cont, p);

        p.openMenu(new SimpleMenuProvider(
                (id, inv, player) ->
                        new ChestMenu(MenuType.GENERIC_9x6, id, inv, cont, 6),
                Component.literal("§cTrails")
        ));
    }

    public void openTags(ServerPlayer p) {

        CosmeticsContainer cont =
                new CosmeticsContainer(p, cosmetics, config, economy, this, ranks);

        fill(cont);

        int slot = 10;

        String[] tags = {
                "charmander",
                "squirtle",
                "bulbasaur",
                "charizard",
                "blastoise",
                "venusaur",
                "mew",
                "mewtwo",
                "articuno",
                "zapdos",
                "moltres"
        };

        for (String tag : tags) {

            ItemStack it = new ItemStack(Items.NAME_TAG);

            String perm = "pixity.cosmetic.tag." + tag;

            boolean unlocked = ranks.hasPerm(p, perm);

            boolean equipped = tag.equalsIgnoreCase(
                    org.howie.pixity.moderation.neoforge.chat.cosmetics.CosmeticService.getTag(p)
            );

            boolean legendary =
                    tag.equalsIgnoreCase("mewtwo") ||
                            tag.equalsIgnoreCase("mew") ||
                            tag.equalsIgnoreCase("articuno") ||
                            tag.equalsIgnoreCase("zapdos") ||
                            tag.equalsIgnoreCase("moltres");

            Component name = CosmeticService.buildTagPreview(tag);

            it.set(DataComponents.CUSTOM_NAME, name);

            List<Component> lore = new ArrayList<>();

            lore.add(LegacyAmpersand.parse("&7Pokémon Tag"));
            lore.add(Component.literal(""));

            if (!unlocked) {
                lore.add(LegacyAmpersand.parse("&cLOCKED"));
                lore.add(LegacyAmpersand.parse("&7Click to open shop"));
            } else {
                lore.add(LegacyAmpersand.parse(
                        "&eStatus: " + (equipped ? "&aEQUIPPED" : "&7Click to equip")
                ));
            }

            if (legendary) {
                lore.add(Component.literal(""));
                lore.add(LegacyAmpersand.parse("&6&lLEGENDARY"));
            }

            it.set(DataComponents.LORE, new ItemLore(lore));

            if (unlocked) {
                it.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
            }

            cont.setItem(slot, it);

            cont.registerButton(slot, () -> {

                if (!unlocked) {
                    p.sendSystemMessage(
                            LegacyAmpersand.parse("&9&lCOSMETICS &7>> &eOpening shop...")
                    );

                    p.closeContainer();

                    ShopCategoryMenu.open(
                            p,
                            shopService,
                            econ,
                            ranks,
                            "tokens:tags",
                            0,
                            ShopSortType.NONE
                    );

                    return;
                }

                CosmeticService.setTag(p, tag);

                p.sendSystemMessage(
                        LegacyAmpersand.parse("&9&lCOSMETICS &7>> &aTag equipped!")
                );

                openTags(p);
            });

            slot++;
            if (slot % 9 == 8) slot += 2;
        }

        addBack(cont, p);

        p.openMenu(new SimpleMenuProvider(
                (id, inv, player) ->
                        new ChestMenu(MenuType.GENERIC_9x6, id, inv, cont, 6),
                Component.literal("§9Tags")
        ));
    }



    public void openName(ServerPlayer p) {

        mode = MenuMode.NAME;
        nameMode = true;
        page = 0;

        CosmeticsContainer cont =
                new CosmeticsContainer(p, cosmetics, config, economy, this, ranks);

        fill(cont);

        int slot = 10;

        for (CosmeticCategory cat : CosmeticCategory.values()) {

            if (cat == CosmeticCategory.NAME) continue;
            if (cat == CosmeticCategory.GLOW) continue;

            ItemStack it = new ItemStack(Items.NAME_TAG);
            it.set(DataComponents.CUSTOM_NAME,
                    LegacyAmpersand.parse("&b" + format(cat)));

            cont.setItem(slot, it);
            cont.registerButton(slot, () -> openCategory(p, cat));

            slot++;
            if (slot % 9 == 8) slot += 2;
        }

        ItemStack back = new ItemStack(Items.BARRIER);
        back.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&cBack"));

        cont.setItem(49, back);
        cont.registerButton(49, () -> openMain(p));

        p.openMenu(new SimpleMenuProvider(
                (id, inv, player) ->
                        new ChestMenu(MenuType.GENERIC_9x6, id, inv, cont, 6),
                Component.literal("§bName Colors")
        ));

        ItemStack clear = new ItemStack(Items.BARRIER);
        clear.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&cClear Name Color"));

        cont.setItem(48, clear);

        cont.registerButton(48, () -> {
            cosmetics.clearName(p.getUUID());
            reopenLast(p);
        });
    }

    public void openGlow(ServerPlayer p) {

        mode = MenuMode.GLOW;
        nameMode = false;

        CosmeticsContainer cont =
                new CosmeticsContainer(p, cosmetics, config, economy, this, ranks);

        fill(cont);

        ItemStack t1 = new ItemStack(Items.LEATHER_CHESTPLATE);
        t1.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&a&lTier 1 Glow"));

        cont.setItem(20, t1);
        cont.registerButton(20, () -> openGlowTier(p, 1));

        ItemStack t2 = new ItemStack(Items.LEATHER_CHESTPLATE);
        t2.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&e&lTier 2 Glow"));

        cont.setItem(22, t2);
        cont.registerButton(22, () -> openGlowTier(p, 2));

        ItemStack t3 = new ItemStack(Items.LEATHER_CHESTPLATE);
        t3.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&c&lTier 3 Glow"));

        cont.setItem(24, t3);
        cont.registerButton(24, () -> openGlowTier(p, 3));

        ItemStack back = new ItemStack(Items.BARRIER);
        back.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&cBack"));

        cont.setItem(49, back);
        cont.registerButton(49, () -> openMain(p));

        p.openMenu(new SimpleMenuProvider(
                (id, inv, player) ->
                        new ChestMenu(MenuType.GENERIC_9x6, id, inv, cont, 6),
                Component.literal("§ePlayer Glow")
        ));
    }

    public void openGlowTier(ServerPlayer p, int tier) {

        mode = MenuMode.GLOW;
        nameMode = false;
        page = 0;

        CosmeticsContainer cont =
                new CosmeticsContainer(p, cosmetics, config, economy, this, ranks);

        fill(cont);

        List<Map.Entry<String, ChatCosmeticsConfig.GlowOption>> list =
                new ArrayList<>();

        for (var e : config.glow.entrySet()) {
            if (e.getValue().thickness == tier)
                list.add(e);
        }

        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(list.size(), start + ITEMS_PER_PAGE);

        int slot = 10;

        for (int i = start; i < end; i++) {
            var e = list.get(i);
            slot = addGlow(cont, p, slot, e.getKey(), e.getValue());
        }

        addNav(cont, p, CosmeticCategory.GLOW, list.size());

        ItemStack back = new ItemStack(Items.BARRIER);
        back.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&cBack"));

        cont.setItem(49, back);
        cont.registerButton(49, () -> openGlow(p));

        p.openMenu(new SimpleMenuProvider(
                (id, inv, player) ->
                        new ChestMenu(MenuType.GENERIC_9x6, id, inv, cont, 6),
                Component.literal("§eGlow Tier " + tier)
        ));

        ItemStack clear = new ItemStack(Items.BARRIER);
        clear.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&cClear Glow"));

        cont.setItem(48, clear);

        cont.registerButton(48, () -> {
            cosmetics.clearGlow(p);
            reopenLast(p);
        });
    }

    private int addHeader(CosmeticsContainer cont, int slot, String name) {

        if (slot >= 54) return slot;
        ItemStack it = new ItemStack(Items.NETHER_STAR);
        it.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse(name));

        cont.setItem(slot, it);

        return slot + 9;
    }

    public void open(ServerPlayer p) {

        nameMode = false;
        mode = MenuMode.CHAT;
        page = 0;

        CosmeticsContainer cont =
                new CosmeticsContainer(p, cosmetics, config, economy, this, ranks);

        fill(cont);

        int slot = 10;

        for (CosmeticCategory cat : CosmeticCategory.values()) {

            if (cat == CosmeticCategory.NAME) continue;
            if (cat == CosmeticCategory.GLOW) continue;

            ItemStack it = new ItemStack(Items.NAME_TAG);
            it.set(DataComponents.CUSTOM_NAME,
                    LegacyAmpersand.parse("&e" + format(cat)));

            cont.setItem(slot, it);
            cont.registerButton(slot, () -> openCategory(p, cat));

            slot++;
            if (slot % 9 == 8) slot += 2;
        }

        ItemStack back = new ItemStack(Items.BARRIER);
        back.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&cBack"));

        cont.setItem(49, back);
        cont.registerButton(49, () -> openMain(p));

        p.openMenu(new SimpleMenuProvider(
                (id, inv, player) ->
                        new ChestMenu(MenuType.GENERIC_9x6, id, inv, cont, 6),
                Component.literal("§dChat Cosmetics")
        ));

        ItemStack clear = new ItemStack(Items.BARRIER);
        clear.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&cClear Chat Color"));

        cont.setItem(48, clear);

        cont.registerButton(48, () -> {
            cosmetics.clearChat(p.getUUID());
            reopenLast(p);
        });
    }

    private int addGlow(CosmeticsContainer cont,
                        ServerPlayer p,
                        int slot,
                        String key,
                        ChatCosmeticsConfig.GlowOption opt) {

        boolean unlocked = hasPerm(p, opt.permission);


        ItemStack it = new ItemStack(Items.LEATHER_CHESTPLATE);

        boolean enabled = cosmetics.hasGlow(p.getUUID());

        List<Component> lore = new ArrayList<>();

        lore.add(LegacyAmpersand.parse("&7Adds a white glow around your player."));
        lore.add(Component.literal(""));

        if (!unlocked) {
            lore.add(LegacyAmpersand.parse("&7Unlock in &e/shop"));
        } else {
            lore.add(LegacyAmpersand.parse("&eClick to toggle"));
        }

        lore.add(Component.literal(""));

        lore.add(LegacyAmpersand.parse(
                "&eStatus: " + (enabled ? "&aENABLED" : "&cDISABLED")
        ));

        it.set(DataComponents.LORE, new ItemLore(lore));

        cont.setItem(slot, it);

        cont.registerGlowSlot(slot, key);

        slot++;
        if (slot % 9 == 8) slot += 2;

        return slot;
    }

    public void openCategory(ServerPlayer p, CosmeticCategory category) {

        CosmeticsContainer cont =
                new CosmeticsContainer(p, cosmetics, config, economy, this, ranks);


        cont.setCategory(category);


        fill(cont);

        List<ItemBuilder> entries = new ArrayList<>();

        for (var e : config.colors.entrySet())
            if (e.getValue().category == category)
                entries.add((slot) -> addColor(cont, p, slot, e.getKey(), e.getValue()));

        for (var e : config.gradients.entrySet())
            if (e.getValue().category == category)
                entries.add((slot) -> addGradient(cont, p, slot, e.getKey(), e.getValue()));

        for (var e : config.animated.entrySet())
            if (e.getValue().category == category)
                entries.add((slot) -> addAnimated(cont, p, slot, e.getKey(), e.getValue()));

        for (var e : config.glow.entrySet())
            if (category == CosmeticCategory.GLOW)
                entries.add((slot) ->
                        addGlow(cont, p, slot, e.getKey(), e.getValue()));

        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(entries.size(), start + ITEMS_PER_PAGE);

        int slot = 10;

        for (int i = start; i < end; i++) {
            slot = entries.get(i).build(slot);
        }

        addNav(cont, p, category, entries.size());

        p.openMenu(new SimpleMenuProvider(
                (id, inv, player) ->
                        new ChestMenu(MenuType.GENERIC_9x6, id, inv, cont, 6),
                Component.literal("§d" + format(category))
        ));
    }

    private int addColor(CosmeticsContainer cont, ServerPlayer p,
                         int slot, String key, ChatCosmeticsConfig.ColorOption opt) {

        String perm = ChatCosmeticsService.buildPerm(
                isNameMode(),
                opt.category,
                key
        );

        boolean unlocked = hasPerm(p, perm);


        ItemStack it = new ItemStack(Items.NAME_TAG);

        it.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse(opt.code + opt.name));

        List<Component> lore = new ArrayList<>();

        if (unlocked) {
            it.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        }

        lore.add(LegacyAmpersand.parse("&eDescription:"));
        lore.add(LegacyAmpersand.parse(
                "&7Stand out from &aother players &7in"
        ));
        lore.add(LegacyAmpersand.parse(
                "&echat " + opt.code + "by using this color."
        ));

        lore.add(Component.literal(""));

        lore.add(LegacyAmpersand.parse("&7Unlock in &e/shop"));

        lore.add(Component.literal(""));

        String current = cosmetics.getChatColor(p.getUUID());
        boolean equipped = key.equalsIgnoreCase(current);

        lore.add(LegacyAmpersand.parse(
                "&eStatus: " + (!unlocked
                        ? "&cLOCKED"
                        : (equipped ? "&aEQUIPPED" : "&7Click to equip"))
        ));

        it.set(DataComponents.LORE, new ItemLore(lore));

        cont.setItem(slot, it);
        cont.registerColorSlot(slot, key);

        slot++;
        if (slot % 9 == 8) slot += 2;
        return slot;
    }

    private int addGradient(CosmeticsContainer cont, ServerPlayer p,
                            int slot, String key, ChatCosmeticsConfig.GradientOption opt) {



        String perm = ChatCosmeticsService.buildPerm(
                isNameMode(),
                opt.category,
                key
        );

        boolean unlocked = hasPerm(p, perm);

        ItemStack it = new ItemStack(Items.NAME_TAG);

        Component name = TextFormatter.gradient(
                opt.name,
                opt.start,
                opt.end
        );

        name = name.copy().setStyle(
                name.getStyle().withItalic(false)
        );

        it.set(DataComponents.CUSTOM_NAME, name);

        if (unlocked) {
            it.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        }

        List<Component> lore = new ArrayList<>();

        lore.add(LegacyAmpersand.parse("&eDescription:"));
        lore.add(LegacyAmpersand.parse("&7Stand out from &aother players"));
        Component desc = TextFormatter.gradient(
                "in chat using this gradient:",
                opt.start,
                opt.end
        );

        desc = desc.copy().setStyle(
                desc.getStyle().withItalic(false)
        );



        lore.add(desc);


        lore.add(Component.literal(""));

        lore.add(LegacyAmpersand.parse("&7Unlock in &e/shop"));



        lore.add(Component.literal(""));

        String current = cosmetics.getChatColor(p.getUUID());
        boolean equipped = key.equalsIgnoreCase(current);

        lore.add(LegacyAmpersand.parse(
                "&eStatus: " + (!unlocked
                        ? "&cLOCKED"
                        : (equipped ? "&aEQUIPPED" : "&7Click to equip"))
        ));

        it.set(DataComponents.LORE, new ItemLore(lore));

        cont.setItem(slot, it);
        cont.registerGradientSlot(slot, key);

        slot++;
        if (slot % 9 == 8) slot += 2;
        return slot;
    }

    private int addAnimated(CosmeticsContainer cont, ServerPlayer p,
                            int slot, String key, ChatCosmeticsConfig.AnimatedOption opt) {

        String perm = ChatCosmeticsService.buildPerm(
                isNameMode(),
                opt.category,
                key
        );

        boolean unlocked = hasPerm(p, perm);

        ItemStack it = new ItemStack(Items.NAME_TAG);

        if (unlocked) {
            it.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        }

        String[] first = opt.frames[0];

        Component name = TextFormatter.gradient(
                opt.name,
                first[0],
                first[1]
        );

        name = name.copy().setStyle(
                name.getStyle().withItalic(false)
        );

        it.set(DataComponents.CUSTOM_NAME, name);

        List<Component> lore = new ArrayList<>();

        lore.add(LegacyAmpersand.parse("&eDescription:"));
        lore.add(LegacyAmpersand.parse("&7Animated chat gradient"));

        lore.add(Component.literal(""));

        int previewCount = Math.min(3, opt.frames.length);

        for (int i = 0; i < previewCount; i++) {
            String[] frame = opt.frames[i];

            Component preview = TextFormatter.gradient(
                    "Animated Preview",
                    frame[0],
                    frame[1]
            );

            preview = preview.copy().setStyle(
                    preview.getStyle().withItalic(false)
            );

            lore.add(preview);
        }


        lore.add(Component.literal(""));

        lore.add(LegacyAmpersand.parse("&7Unlock in &e/shop"));

        lore.add(Component.literal(""));

        String current = cosmetics.getChatColor(p.getUUID());
        boolean equipped = key.equalsIgnoreCase(current);

        lore.add(LegacyAmpersand.parse(
                "&eStatus: " + (!unlocked
                        ? "&cLOCKED"
                        : (equipped ? "&aEQUIPPED" : "&7Click to equip"))
        ));

        it.set(DataComponents.LORE, new ItemLore(lore));

        cont.setItem(slot, it);
        cont.registerAnimatedSlot(slot, key);

        slot++;
        if (slot % 9 == 8) slot += 2;
        return slot;
    }

    private void addBack(CosmeticsContainer cont, ServerPlayer p) {

        ItemStack back = new ItemStack(Items.BARRIER);
        back.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&cBack"));

        cont.setItem(49, back);
        cont.registerButton(49, () -> openMain(p));
    }

    private String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }



    private void addNav(CosmeticsContainer cont,
                        ServerPlayer p,
                        CosmeticCategory category,
                        int total) {

        ItemStack back = new ItemStack(Items.BARRIER);
        back.set(DataComponents.CUSTOM_NAME,
                LegacyAmpersand.parse("&cBack"));

        cont.setItem(49, back);

        cont.registerButton(49, () -> {
            if (mode == MenuMode.NAME) openName(p);
            else open(p);
        });

        if (page > 0) {
            ItemStack prev = new ItemStack(Items.ARROW);
            prev.set(DataComponents.CUSTOM_NAME,
                    LegacyAmpersand.parse("&ePrevious"));
            cont.setItem(45, prev);
            cont.registerPrev(45);
        }

        if ((page + 1) * ITEMS_PER_PAGE < total) {
            ItemStack next = new ItemStack(Items.ARROW);
            next.set(DataComponents.CUSTOM_NAME,
                    LegacyAmpersand.parse("&aNext"));
            cont.setItem(53, next);
            cont.registerNext(53);
        }
    }

    public ShopService getShopService() {
        return shopService;
    }

    public EconomyBridge getEcon() {
        return econ;
    }


    public void reopenLast(ServerPlayer p) {

        switch (mode) {
            case NAME -> openName(p);
            case GLOW -> openGlow(p);
            case CHAT -> open(p);
            default -> openMain(p);
        }
    }

    private void fill(CosmeticsContainer cont) {
        ItemStack filler = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
        filler.set(DataComponents.CUSTOM_NAME, Component.literal(""));

        for (int i = 0; i < 54; i++)
            cont.setItem(i, filler.copy());
    }

    public void nextPage(ServerPlayer p, CosmeticCategory cat) {
        page++;
        openCategory(p, cat);
    }

    public void prevPage(ServerPlayer p, CosmeticCategory cat) {
        if (page > 0) page--;
        openCategory(p, cat);
    }

    private String format(CosmeticCategory cat) {
        String s = cat.name().toLowerCase();
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private boolean hasPerm(ServerPlayer p, String perm) {
        return perm != null && ranks.hasPerm(p, perm);
    }

    public boolean isNameMode() {
        return nameMode;
    }

    private interface ItemBuilder {
        int build(int slot);
    }
}