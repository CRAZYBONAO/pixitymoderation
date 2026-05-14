package org.howie.pixity.moderation.neoforge.kits;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.Items;
import org.apache.logging.log4j.Logger;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.economy.*;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class KitManager {


    private final Logger logger;
    private final KitStore store;
    private final RankService perms;
    private final EconomyService economy;

    private final Map<String, Kit> kits = new ConcurrentHashMap<>();
    private final Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

    public KitManager(final Logger logger,
                      final KitStore store,
                      final RankService perms,
                      final EconomyService economy) {

        this.logger = logger;
        this.store = store;
        this.perms = perms;
        this.economy = economy;

        this.kits.putAll(store.loadKits());

        Map<UUID, Map<String, Long>> loaded = store.loadCooldowns();
        for (Map.Entry<UUID, Map<String, Long>> e : loaded.entrySet()) {
            this.cooldowns.put(e.getKey(), new ConcurrentHashMap<>(e.getValue()));
        }
    }



    public Collection<Kit> allKits() {
        return Collections.unmodifiableCollection(kits.values());
    }

    public Optional<Kit> getKit(final String name) {
        return Optional.ofNullable(kits.get(KitText.normalizeName(name)));
    }



    public boolean deleteKit(final String name) {
        String key = KitText.normalizeName(name);
        Kit removed = kits.remove(key);
        if (removed != null) {
            store.saveKits(new HashMap<>(kits));
            return true;
        }
        return false;
    }

    public Kit createFromPlayerInventory(final ServerPlayer player,
                                         final String kitNameRaw,
                                         final long cooldownSeconds,
                                         final String displayNameRaw,
                                         final String categoryRaw,
                                         final double price,
                                         final String currency) {

        final String key = KitText.normalizeName(kitNameRaw);

        Kit kit = new Kit();
        kit.name = key;
        kit.displayNameRaw = displayNameRaw == null ? kitNameRaw : displayNameRaw;
        kit.cooldownSeconds = Math.max(0, cooldownSeconds);
        kit.createdBy = player.getUUID();
        kit.createdAtEpochMs = System.currentTimeMillis();
        kit.itemsSnbt = new ArrayList<>();

        kit.category = KitCategory.from(categoryRaw).name();
        kit.price = Math.max(0, price);
        kit.currency = currency == null ? "MONEY" : currency.toUpperCase();



        for (ItemStack stack : player.getInventory().items) {
            if (stack == null || stack.isEmpty()) continue;
            kit.itemsSnbt.add(stack.save(player.registryAccess()).toString());
        }

        for (ItemStack stack : player.getInventory().armor) {
            if (stack == null || stack.isEmpty()) continue;
            kit.itemsSnbt.add(stack.save(player.registryAccess()).toString());
        }

        for (ItemStack stack : player.getInventory().offhand) {
            if (stack == null || stack.isEmpty()) continue;
            kit.itemsSnbt.add(stack.save(player.registryAccess()).toString());
        }

        kits.put(key, kit);
        store.saveKits(new HashMap<>(kits));

        return kit;
    }



    public boolean canUseKit(final ServerPlayer player, final Kit kit) {
        if (player == null || kit == null) return false;

        String specific = "pixity.kits.claim." + kit.name;

        return perms.hasPerm(player, specific)
                || perms.hasPerm(player, "pixity.kits.claim.*")
                || perms.hasPerm(player, "pixity.kits.admin");
    }

    private boolean hasFreeAccess(ServerPlayer player, Kit kit) {
        return perms.hasPerm(player, "pixity.kit." + kit.name)
                || perms.hasPerm(player, "pixity.kit.*")
                || perms.hasPerm(player, "pixity.kit." + kit.name + ".bypass")
                || perms.hasPerm(player, "pixity.kit." + kit.name + ".free")
                || perms.hasPerm(player, "pixity.kit.free");
    }



    public long remainingCooldownSeconds(final ServerPlayer player, final Kit kit) {

        if (perms.hasPerm(player, "pixity.kit.cooldown.bypass")) {
            return 0L;
        }

        long base = kit.cooldownSeconds;

        if (perms.hasPerm(player, "pixity.kit.cooldown.50")) {
            base *= 0.5;
        } else if (perms.hasPerm(player, "pixity.kit.cooldown.25")) {
            base *= 0.75;
        }

        if (base <= 0) return 0L;

        Map<String, Long> user = cooldowns.get(player.getUUID());
        if (user == null) return 0L;

        Long next = user.get(kit.name);
        if (next == null) return 0L;

        long remaining = next - System.currentTimeMillis();
        if (remaining <= 0) return 0L;

        return (remaining + 999) / 1000;
    }



    public boolean tryClaimKit(final ServerPlayer player, final Kit kit) {

        if (!canUseKit(player, kit)) {
            player.sendSystemMessage(LegacyAmpersand.parse("&c&lKITS &7&l➤ &cError! No permission."));
            return false;
        }

        long rem = remainingCooldownSeconds(player, kit);
        if (rem > 0) {
            player.sendSystemMessage(LegacyAmpersand.parse("&c&lKITS &7&l➤ &cCooldown: &e" + formatTime(rem)));
            return false;
        }

        if (hasFreeAccess(player, kit)) {
            boolean ok = giveItems(player, kit);
            if (ok) {
                setCooldown(player, kit);
                player.sendSystemMessage(LegacyAmpersand.parse("&c&lKITS &7&l➤ &aClaimed kit: &e" + kit.displayNameRaw));
            }
            return ok;
        }

        double finalPrice = getFinalPrice(player, kit);

        if (finalPrice > 0) {
            CurrencyType currency = parseCurrency(kit);

            double bal = economy.get(player, currency);

            if (bal < finalPrice) {
                player.sendSystemMessage(LegacyAmpersand.parse(
                        "&c&lKITS &7&l➤ &cError! Not enough " + CurrencyFormatter.name(currency) +
                                " &c(Need &e" + CurrencyFormatter.format(currency, finalPrice) + "&c)"
                ));
                return false;
            }

            if (!economy.remove(player, currency, finalPrice)) {
                player.sendSystemMessage(LegacyAmpersand.parse("&c&lKITS &7&l➤ &cError! Transaction failed."));
                return false;
            }

            player.sendSystemMessage(LegacyAmpersand.parse(
                    "&c&lKITS &7&l➤ &aPaid &e" + CurrencyFormatter.format(currency, finalPrice)
            ));
        }

        boolean ok = giveItems(player, kit);

        if (ok) {
            setCooldown(player, kit);
            player.sendSystemMessage(LegacyAmpersand.parse("&c&lKITS &7&l➤ &aClaimed kit: &e" + kit.displayNameRaw));
        }

        return ok;
    }

    public static String formatTime(long seconds) {

        long d = seconds / 86400;
        seconds %= 86400;

        long h = seconds / 3600;
        seconds %= 3600;

        long m = seconds / 60;
        long s = seconds % 60;

        StringBuilder out = new StringBuilder();

        if (d > 0) out.append(d).append("d ");
        if (h > 0) out.append(h).append("h ");
        if (m > 0) out.append(m).append("m ");
        if (s > 0 || out.length() == 0) out.append(s).append("s");

        return out.toString().trim();
    }



    public double getFinalPrice(ServerPlayer player, Kit kit) {
        if (kit.price <= 0) return 0;
        double discount = getDiscount(player);
        return Math.max(0, kit.price * (1.0 - discount));
    }

    private double getDiscount(ServerPlayer player) {

        double best = 0;

        if (perms.hasPerm(player, "pixity.kit.discount.50")) best = 0.50;
        if (perms.hasPerm(player, "pixity.kit.discount.25")) best = Math.max(best, 0.25);
        if (perms.hasPerm(player, "pixity.kit.discount.20")) best = Math.max(best, 0.20);
        if (perms.hasPerm(player, "pixity.kit.discount.15")) best = Math.max(best, 0.15);
        if (perms.hasPerm(player, "pixity.kit.discount.10")) best = Math.max(best, 0.10);
        if (perms.hasPerm(player, "pixity.kit.discount.5"))  best = Math.max(best, 0.05);

        return best;
    }



    private boolean giveItems(ServerPlayer player, Kit kit) {
        for (String snbt : kit.itemsSnbt) {
            try {
                CompoundTag tag = TagParser.parseTag(snbt);
                ItemStack stack = ItemStack.parseOptional(player.registryAccess(), tag);
                player.getInventory().add(stack);
            } catch (Exception e) {
                logger.warn("&c&lKITS &7&l➤ Failed kit item", e);
            }
        }
        return true;
    }

    private void setCooldown(ServerPlayer player, Kit kit) {
        if (kit.cooldownSeconds <= 0) return;

        long next = System.currentTimeMillis() + (kit.cooldownSeconds * 1000L);

        cooldowns.computeIfAbsent(player.getUUID(), k -> new ConcurrentHashMap<>())
                .put(kit.name, next);

        store.saveCooldowns(snapshotCooldowns());
    }

    private CurrencyType parseCurrency(Kit kit) {
        try {
            return CurrencyType.valueOf(kit.currency);
        } catch (Exception e) {
            return CurrencyType.MONEY;
        }
    }

    private Map<UUID, Map<String, Long>> snapshotCooldowns() {
        Map<UUID, Map<String, Long>> out = new HashMap<>();
        for (var e : cooldowns.entrySet()) {
            out.put(e.getKey(), new HashMap<>(e.getValue()));
        }
        return out;
    }

    public boolean setDisplay(String kit, String display) {
        Kit k = kits.get(KitText.normalizeName(kit));
        if (k == null) return false;

        k.displayNameRaw = display;
        store.saveKits(new HashMap<>(kits));
        return true;
    }

    public boolean setPrice(String kit, double price, String currency) {
        Kit k = kits.get(KitText.normalizeName(kit));
        if (k == null) return false;

        k.price = Math.max(0, price);
        k.currency = currency == null ? "MONEY" : currency.toUpperCase();

        store.saveKits(new HashMap<>(kits));
        return true;
    }

    public boolean setIcon(ServerPlayer player, String kit) {

        Kit k = kits.get(KitText.normalizeName(kit));
        if (k == null) return false;

        ItemStack held = player.getMainHandItem();
        if (held.isEmpty()) return false;

        k.iconSnbt = held.save(player.registryAccess()).toString();

        store.saveKits(new HashMap<>(kits));
        return true;
    }

    public Component renderDisplayName(final String raw) {
        return KitText.renderDisplayName(raw);
    }

    public ItemStack getIcon(ServerPlayer player, Kit kit) {

        try {

            if (kit.iconSnbt != null && !kit.iconSnbt.isEmpty()) {
                return ItemStack.parse(
                        player.registryAccess(),
                        net.minecraft.nbt.TagParser.parseTag(kit.iconSnbt)
                ).orElse(new ItemStack(Items.CHEST));
            }

            if (kit.itemsSnbt != null && !kit.itemsSnbt.isEmpty()) {
                return ItemStack.parse(
                        player.registryAccess(),
                        net.minecraft.nbt.TagParser.parseTag(kit.itemsSnbt.get(0))
                ).orElse(new ItemStack(Items.CHEST));
            }

        } catch (Exception ignored) {}

        return new ItemStack(Items.CHEST);
    }


}
