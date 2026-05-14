package org.howie.pixity.moderation.neoforge.chat.cosmetics;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import org.howie.pixity.moderation.neoforge.economy.CurrencyType;
import org.howie.pixity.moderation.neoforge.economy.EconomyService;
import org.howie.pixity.moderation.neoforge.shop.ShopSortType;
import org.howie.pixity.moderation.neoforge.shop.gui.ShopCategoryMenu;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;
import org.howie.pixity.moderation.neoforge.rank.RankService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CosmeticsContainer extends SimpleContainer {

    private final ServerPlayer player;
    private final ChatCosmeticsService cosmetics;
    private final ChatCosmeticsConfig config;
    private final EconomyService economy;
    private final CosmeticsGui gui;
    private final RankService ranks;

    private final Map<Integer, String> colorSlots = new HashMap<>();
    private final Map<Integer, String> gradientSlots = new HashMap<>();
    private final Map<Integer, String> animatedSlots = new HashMap<>();
    private final Map<Integer, CosmeticCategory> categorySlots = new HashMap<>();
    private final Map<Integer, String> glowSlots = new HashMap<>();



    private int nextSlot = -1;
    private int prevSlot = -1;
    private CosmeticCategory currentCategory;



    private final Map<Integer, Runnable> buttons = new HashMap<>();

    public void registerButton(int slot, Runnable action) {
        buttons.put(slot, action);
    }

    public CosmeticsContainer(ServerPlayer player,
                              ChatCosmeticsService cosmetics,
                              ChatCosmeticsConfig config,
                              EconomyService economy,
                              CosmeticsGui gui,
                              RankService ranks) {
        super(54);
        this.player = player;
        this.cosmetics = cosmetics;
        this.config = config;
        this.economy = economy;
        this.gui = gui;
        this.ranks = ranks;
    }

    public void registerGlowSlot(int slot, String key) {
        glowSlots.put(slot, key);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setChanged() {}

    public void registerColorSlot(int slot, String key) { colorSlots.put(slot, key); }
    public void registerGradientSlot(int slot, String key) { gradientSlots.put(slot, key); }
    public void registerAnimatedSlot(int slot, String key) { animatedSlots.put(slot, key); }
    public void registerCategorySlot(int slot, CosmeticCategory cat) { categorySlots.put(slot, cat); }

    public void registerNext(int slot) { this.nextSlot = slot; }
    public void registerPrev(int slot) { this.prevSlot = slot; }
    public void setCategory(CosmeticCategory category) { this.currentCategory = category; }



    @Override
    public ItemStack removeItem(int slot, int amount) {

        if (buttons.containsKey(slot)) {
            buttons.get(slot).run();
            return ItemStack.EMPTY;
        }





        if (slot == nextSlot) {
            gui.nextPage(player, currentCategory);
            return ItemStack.EMPTY;
        }

        if (slot == prevSlot) {
            gui.prevPage(player, currentCategory);
            return ItemStack.EMPTY;
        }

        if (categorySlots.containsKey(slot)) {

            CosmeticCategory cat = categorySlots.get(slot);

            if (cat == CosmeticCategory.NAME) {
                gui.openName(player);
                return ItemStack.EMPTY;
            }

            if (cat == CosmeticCategory.GLOW) {
                gui.openGlow(player);
                return ItemStack.EMPTY;
            }

            if (cat == null) {
                gui.open(player);
                return ItemStack.EMPTY;
            }

            gui.openCategory(player, cat);
            return ItemStack.EMPTY;
        }

        if (glowSlots.containsKey(slot)) {

            if (!hasPerm("pixity.cosmetic.glow")) {
                player.sendSystemMessage(
                        LegacyAmpersand.parse("&9&lCOSMETICS &7>> &eOpening shop...")
                );

                player.closeContainer();

                ShopCategoryMenu.open(
                        player,
                        gui.getShopService(),
                        gui.getEcon(),
                        ranks,
                        "tokens:cosmetics",
                        0,
                        ShopSortType.NONE
                );

                return ItemStack.EMPTY;
            }

            cosmetics.toggleGlow(player);

            gui.reopenLast(player);
            return ItemStack.EMPTY;
        }



        if (colorSlots.containsKey(slot)) {
            String key = colorSlots.get(slot);
            var opt = config.colors.get(key);

            String perm = ChatCosmeticsService.buildPerm(
                    gui.isNameMode(),
                    opt.category,
                    key
            );

            if (!hasPerm(perm)) {
                player.sendSystemMessage(
                        LegacyAmpersand.parse("&9&lCOSMETICS &7>> &eOpening shop...")
                );

                player.closeContainer();

                ShopCategoryMenu.open(
                        player,
                        gui.getShopService(),
                        gui.getEcon(),
                        ranks,
                        "tokens:chatcolors",
                        0,
                        ShopSortType.NONE
                );

                return ItemStack.EMPTY;
            }

            if (gui.isNameMode()) {
                cosmetics.setNameColor(player.getUUID(), opt.code);
            } else {
                cosmetics.setColor(player.getUUID(), opt.code);
            }

            gui.reopenLast(player);
            return ItemStack.EMPTY;
        }


        if (gradientSlots.containsKey(slot)) {
            String key = gradientSlots.get(slot);
            var opt = config.gradients.get(key);

            String perm = ChatCosmeticsService.buildPerm(
                    gui.isNameMode(),
                    opt.category,
                    key
            );

            if (!hasPerm(perm)) {
                player.sendSystemMessage(
                        LegacyAmpersand.parse("&9&lCOSMETICS &7>> &eOpening shop...")
                );

                player.closeContainer();


                ShopCategoryMenu.open(
                        player,
                        gui.getShopService(),
                        gui.getEcon(),
                        ranks,
                        "tokens:chatgradients",
                        0,
                        ShopSortType.NONE
                );

                return ItemStack.EMPTY;
            }

            if (gui.isNameMode()) {
                cosmetics.setNameGradient(player.getUUID(), key);
            } else {
                cosmetics.setGradient(player.getUUID(), key);
            }

            gui.reopenLast(player);
            return ItemStack.EMPTY;
        }


        if (animatedSlots.containsKey(slot)) {
            String key = animatedSlots.get(slot);
            var opt = config.animated.get(key);

            String perm = ChatCosmeticsService.buildPerm(
                    gui.isNameMode(),
                    opt.category,
                    key
            );

            if (!hasPerm(perm)) {
                player.sendSystemMessage(
                        LegacyAmpersand.parse("&9&lCOSMETICS &7>> &eOpening shop...")
                );

                player.closeContainer();


                ShopCategoryMenu.open(
                        player,
                        gui.getShopService(),
                        gui.getEcon(),
                        ranks,
                        "tokens:animated",
                        0,
                        ShopSortType.NONE
                );

                return ItemStack.EMPTY;
            }

            if (gui.isNameMode()) {
                cosmetics.setNameAnimated(
                        player.getUUID(),
                        new ChatCosmeticsService.AnimatedGradient(key, opt.frames)
                );
            } else {
                cosmetics.setAnimated(
                        player.getUUID(),
                        new ChatCosmeticsService.AnimatedGradient(key, opt.frames)
                );
            }

            gui.reopenLast(player);
            return ItemStack.EMPTY;
        }

        return ItemStack.EMPTY;
    }






    private boolean hasPerm(String perm) {
        return perm != null
                && !perm.isEmpty()
                && (player.hasPermissions(2) || ranks.hasPerm(player, perm));
    }
}