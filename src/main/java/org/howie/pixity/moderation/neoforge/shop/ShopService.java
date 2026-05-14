package org.howie.pixity.moderation.neoforge.shop;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.chat.cosmetics.ChatCosmeticsConfig;
import org.howie.pixity.moderation.neoforge.chat.cosmetics.CosmeticCategory;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

public class ShopService {

    private final Map<String, ShopCategory> categories = new HashMap<>();
    private final Map<String, Shop> shops = new HashMap<>();

    private final Path path =
            Paths.get("config/pixity/shops.json");

    private final Gson gson =
            new GsonBuilder().setPrettyPrinting().create();

    private static final String[] COLORS = {
            "white","orange","magenta","light_blue","yellow","lime","pink",
            "gray","light_gray","cyan","purple","blue","brown","green","red","black"
    };

    public Collection<Shop> getAllShops() {
        return shops.values();
    }

    public List<ShopItem> search(String query) {

        List<ShopItem> results = new ArrayList<>();

        String q = query.toLowerCase();

        for (Shop shop : shops.values()) {

            for (ShopItem item : shop.items) {

                String name = item.name.toLowerCase();
                String id = item.item.toLowerCase();

                if (name.contains(q) || id.contains(q)) {
                    results.add(item);
                }
            }
        }

        return results;
    }

    public List<ShopItem> sort(List<ShopItem> items, ShopSortType type) {

        List<ShopItem> sorted = new ArrayList<>(items);

        switch (type) {

            case PRICE_HIGH ->
                    sorted.sort((a, b) -> Double.compare(b.buy, a.buy));

            case PRICE_LOW ->
                    sorted.sort((a, b) -> Double.compare(a.buy, b.buy));

            case A_Z ->
                    sorted.sort(Comparator.comparing(a -> a.name));

            case Z_A ->
                    sorted.sort((a, b) -> b.name.compareTo(a.name));

            case NONE -> {
            }
        }

        return sorted;
    }


    public void load() {

        try {

            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.writeString(path, "{}");
            }

            JsonObject json =
                    JsonParser.parseString(
                            Files.readString(path)
                    ).getAsJsonObject();


            categories.clear();

            if (json.has("categories")) {

                JsonObject cats = json.getAsJsonObject("categories");

                for (String key : cats.keySet()) {

                    JsonObject obj = cats.getAsJsonObject(key);

                    ShopCategory cat = new ShopCategory();
                    cat.id = key;
                    cat.name = obj.get("name").getAsString();
                    cat.icon = obj.get("icon").getAsString();
                    cat.slot = obj.get("slot").getAsInt();

                    cat.description = new ArrayList<>();

                    if (obj.has("description")) {
                        for (JsonElement e : obj.getAsJsonArray("description")) {
                            cat.description.add(e.getAsString());
                        }
                    }

                    categories.put(key, cat);
                }
            }



            shops.clear();

            if (json.has("shops")) {

                JsonObject sh = json.getAsJsonObject("shops");

                for (String key : sh.keySet()) {

                    JsonObject obj = sh.getAsJsonObject(key);

                    Shop shop = new Shop();
                    shop.id = key;
                    shop.rows = obj.get("rows").getAsInt();



                    if (obj.has("generator")) {

                        JsonObject gen = obj.getAsJsonObject("generator");

                        String type = gen.get("type").getAsString();

                        String namespace = gen.has("namespace")
                                ? gen.get("namespace").getAsString()
                                : "minecraft";

                        double buy = gen.has("buy") ? gen.get("buy").getAsDouble() : 0;
                        double sell = gen.has("sell") ? gen.get("sell").getAsDouble() : 0;
                        String currency = gen.get("currency").getAsString();

                        int slot = 0;

                        if (type.equalsIgnoreCase("colored_block")) {

                            String base = gen.get("base").getAsString();

                            for (String color : COLORS) {

                                String itemId;

                                if (base.contains("stained_glass_pane")) {
                                    itemId = "minecraft:" + color + "_stained_glass_pane";
                                }
                                else if (base.contains("stained_glass")) {
                                    itemId = "minecraft:" + color + "_stained_glass";
                                }
                                else if (base.contains("glazed_terracotta")) {
                                    itemId = "minecraft:" + color + "_glazed_terracotta";
                                }
                                else {
                                    itemId = "minecraft:" + color + "_" + base.replace("minecraft:", "");
                                }

                                shop.items.add(createItem(slot++, itemId,
                                        capitalize(color) + " " + cleanName(base),
                                        buy, sell, currency, key));
                            }
                        }


                        else if (type.equalsIgnoreCase("material_set")) {

                            JsonArray mats = gen.getAsJsonArray("materials");
                            JsonArray vars = gen.getAsJsonArray("variants");

                            for (JsonElement m : mats) {

                                String material = m.getAsString();

                                for (JsonElement v : vars) {

                                    String variant = v.getAsString();

                                    String itemId;

                                    if (variant.equalsIgnoreCase("stripped_log")) {
                                        itemId = namespace + ":stripped_" + material + "_log";
                                    }
                                    else if (variant.equalsIgnoreCase("stripped_stem")) {
                                        itemId = namespace + ":stripped_" + material + "_stem";
                                    }
                                    else {
                                        itemId = namespace + ":" + material + "_" + variant;
                                    }

                                    shop.items.add(createItem(slot++, itemId,
                                            capitalize(material) + " " + capitalize(variant),
                                            buy, sell, currency, key));
                                }
                            }
                        }


                        else if (type.equalsIgnoreCase("colored")) {

                            String base = gen.get("base").getAsString();
                            JsonArray colors = gen.getAsJsonArray("colors");

                            for (JsonElement c : colors) {

                                String color = c.getAsString();

                                String itemId = namespace + ":" + color + "_" + base;

                                shop.items.add(createItem(slot++, itemId,
                                        capitalize(color) + " " + capitalize(base),
                                        buy, sell, currency, key));
                            }
                        }
                    }



                    if (obj.has("subcategories")) {

                        JsonObject subs = obj.getAsJsonObject("subcategories");

                        for (String subKey : subs.keySet()) {

                            JsonObject sub = subs.getAsJsonObject(subKey);

                            ShopSubCategory sc = new ShopSubCategory();

                            sc.id = key + ":" + subKey;
                            sc.name = sub.get("name").getAsString();
                            sc.icon = sub.get("icon").getAsString();
                            sc.slot = sub.get("slot").getAsInt();

                            shop.subcategories.add(sc);
                        }
                    }


                    if (obj.has("items")) {

                        for (JsonElement el : obj.getAsJsonArray("items")) {

                            JsonObject i = el.getAsJsonObject();

                            ShopItem item = new ShopItem();
                            item.commands = new ArrayList<>();

                            if (i.has("mob")) {
                                item.mob = i.get("mob").getAsString();
                            }

                            item.slot = i.get("slot").getAsInt();
                            item.item = i.get("item").getAsString();
                            item.name = i.has("name")
                                    ? i.get("name").getAsString()
                                    : "&e" + formatName(item.item);
                            item.buy = i.has("buy") ? i.get("buy").getAsDouble() : 0;
                            item.sell = i.has("sell") ? i.get("sell").getAsDouble() : 0;
                            item.currency = i.has("currency") ? i.get("currency").getAsString() : "money";

                            item.lore = new ArrayList<>();




                            if (i.has("lore")) {

                                String symbol = getCurrencySymbol(item.currency);

                                for (JsonElement l : i.getAsJsonArray("lore")) {

                                    String line = l.getAsString();


                                    line = line.replace("{buy}", formatNumber(item.buy) + symbol);
                                    line = line.replace("{sell}", formatNumber(item.sell) + symbol);

                                    item.lore.add(line);
                                }

                            } else {


                                String symbol = getCurrencySymbol(item.currency);

                                item.lore.add("&#BACBFFShop");
                                item.lore.add("");

                                if (item.buy > 0)
                                    item.lore.add("&fBuy: &a" + formatNumber(item.buy) + symbol);

                                if (item.sell > 0)
                                    item.lore.add("&fSell: &c" + formatNumber(item.sell) + symbol);

                                item.lore.add("");
                                item.lore.add("&e&n&lCLICK&e to Purchase");

                                if (item.sell > 0)
                                    item.lore.add("&e&n&lRIGHT CLICK&e to Sell");
                            }




                            item.commands = new ArrayList<>();

                            if (i.has("category")) {
                                item.category = i.get("category").getAsString();
                            } else {
                                item.category = key;
                            }

                            if (i.has("commands")) {
                                for (JsonElement c : i.getAsJsonArray("commands")) {
                                    item.commands.add(c.getAsString());
                                }
                            }

                            if (i.has("permission")) {
                                item.permission = i.get("permission").getAsString();
                            }

                            shop.items.add(item);
                        }
                    }

                    shops.put(key, shop);
                }
            }





            generateCosmetics(new ChatCosmeticsConfig());



            System.out.println("[Pixity] Shops loaded!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public void generateCosmetics(ChatCosmeticsConfig cfg) {

        Shop chatColors     = shops.get("tokens:chat_colors");
        Shop chatGradients  = shops.get("tokens:chat_gradients");
        Shop nameColors     = shops.get("tokens:name_colors");
        Shop nameGradients  = shops.get("tokens:name_gradients");

        if (chatColors == null || chatGradients == null
                || nameColors == null || nameGradients == null) {
            System.out.println("[Pixity] Missing cosmetic shops!");
            return;
        }

        chatColors.items.clear();
        chatGradients.items.clear();
        nameColors.items.clear();
        nameGradients.items.clear();

        int ccSlot = 0;
        int cgSlot = 0;
        int ncSlot = 0;
        int ngSlot = 0;




        for (var entry : cfg.colors.entrySet()) {



            String key = entry.getKey();
            var opt = entry.getValue();


            {
                ShopItem item = new ShopItem();

                item.commands = new ArrayList<>();

                item.slot = ccSlot++;
                item.item = "minecraft:name_tag";
                item.name = opt.name;
                item.colorCode = opt.code;

                item.buy = opt.price;
                item.currency = "tokens";

                item.category = mapCategory(opt.category, false);


                item.commands.add(
                        "lp user %player% permission set pixity.cosmetics.chatcolor." + key + " true"
                );

                chatColors.items.add(item);
            }


            {
                ShopItem item = new ShopItem();
                item.commands = new ArrayList<>();

                item.slot = ncSlot++;
                item.item = "minecraft:name_tag";
                item.name = opt.name;
                item.colorCode = opt.code;

                item.buy = opt.price;
                item.currency = "tokens";

                item.category = mapCategory(opt.category, false);



                item.commands.add(
                        "lp user %player% permission set pixity.cosmetics.namecolor." + key + " true"
                );

                nameColors.items.add(item);
            }
        }




        for (var entry : cfg.gradients.entrySet()) {

            String key = entry.getKey();
            var opt = entry.getValue();


            {
                ShopItem item = new ShopItem();
                item.commands = new ArrayList<>();

                item.slot = cgSlot++;
                item.item = "minecraft:name_tag";
                item.name = opt.name;
                item.gradientStart = opt.start;
                item.gradientEnd = opt.end;
                item.category = mapCategory(opt.category, true);



                item.buy = opt.price;
                item.currency = "tokens";

                item.category = mapCategory(opt.category, true);


                item.commands.add(
                        "lp user %player% permission set pixity.cosmetics.gradient." + key + " true"
                );

                chatGradients.items.add(item);
            }


            {
                ShopItem item = new ShopItem();
                item.commands = new ArrayList<>();

                item.slot = ngSlot++;
                item.item = "minecraft:name_tag";
                item.name = opt.name;
                item.gradientStart = opt.start;
                item.gradientEnd = opt.end;
                item.buy = opt.price;
                item.currency = "tokens";
                item.category = mapCategory(opt.category, true);




                item.commands.add(
                        "lp user %player% permission set pixity.cosmetics.namegradient." + key + " true"
                );

                nameGradients.items.add(item);
            }
        }

        System.out.println("[Pixity] Cosmetics generated into 4 shops!");
    }







    private String mapCategory(CosmeticCategory cat, boolean gradient) {

        String base = gradient ? "chat_gradients." : "chat_colors.";

        return switch (cat) {
            case NEON -> base + "neon";
            case NATURE -> base + "nature";
            case FOOD -> base + "food";
            case PASTEL -> base + "pastel";
            case DARK -> base + "dark";
            case METAL -> base + "metal";
            case COLORS -> base + "basic";
            case SEASONAL -> base + "seasonal";
            case EVENT -> base + "events";
            case POKEMON -> base + "pokemon";
            default -> base + "misc";
        };
    }

    private static String formatNumber(double value) {

        if (value >= 1_000_000_000) {
            return formatCompact(value, 1_000_000_000, "B");
        }

        if (value >= 1_000_000) {
            return formatCompact(value, 1_000_000, "M");
        }

        if (value >= 1_000) {
            return formatCompact(value, 1_000, "K");
        }

        return String.valueOf((int) value);
    }

    private static String formatCompact(double value, double divisor, String suffix) {
        double result = value / divisor;


        if (result % 1 == 0) {
            return (int) result + suffix;
        } else {
            return String.format("%.2f", result)
                    .replaceAll("\\.?0+$", "") + suffix;
        }
    }

    private ShopItem createItem(int slot, String id, String name,
                                double buy, double sell, String currency, String category) {

        ShopItem item = new ShopItem();

        item.slot = slot;
        item.item = id;
        item.category = category;
        item.commands = new ArrayList<>();


        String clean = id.replace("minecraft:", "").replace("_", " ");
        clean = Character.toUpperCase(clean.charAt(0)) + clean.substring(1);

        item.name = "&e" + formatName(id);


        item.buy = buy;
        item.sell = sell;
        item.currency = currency;
        String symbol = getCurrencySymbol(currency);


        item.lore = new ArrayList<>();


        item.lore.add("&#BACBFFShop");
        item.lore.add("");
        item.lore.add("&fBuy: &a" + formatNumber(buy) + symbol);;
        if (sell > 0) {
            item.lore.add("&fSell: &c" + formatNumber(sell) + symbol);
        }
        item.lore.add("");
        item.lore.add("&e&n&lCLICK&e to Purchase");
        item.lore.add("&e&n&lRIGHT CLICK&e to Sell");

        item.commands = new ArrayList<>();

        return item;
    }

    private static String capitalize(String s) {
        s = s.replace("_", " ");
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String cleanName(String s) {
        return s.replace("minecraft:", "").replace("_", " ");
    }





    private static String formatName(String id) {

        String clean = id.contains(":") ? id.split(":")[1] : id;




        if (id.startsWith("cobblemon:")) {

            switch (clean) {


                case "poke_ball": return "Poke Ball";
                case "great_ball": return "Great Ball";
                case "ultra_ball": return "Ultra Ball";
                case "master_ball": return "Master Ball";
                case "premier_ball": return "Premier Ball";
                case "luxury_ball": return "Luxury Ball";
                case "heal_ball": return "Heal Ball";
                case "dusk_ball": return "Dusk Ball";
                case "quick_ball": return "Quick Ball";
                case "timer_ball": return "Timer Ball";
                case "repeat_ball": return "Repeat Ball";
                case "net_ball": return "Net Ball";
                case "dive_ball": return "Dive Ball";
                case "nest_ball": return "Nest Ball";


                case "fast_ball": return "Fast Ball";
                case "level_ball": return "Level Ball";
                case "lure_ball": return "Lure Ball";
                case "heavy_ball": return "Heavy Ball";
                case "love_ball": return "Love Ball";
                case "friend_ball": return "Friend Ball";
                case "moon_ball": return "Moon Ball";


                case "dream_ball": return "Dream Ball";
                case "sport_ball": return "Sport Ball";
                case "park_ball": return "Park Ball";


                case "exp_share": return "EXP Share";
                case "lucky_egg": return "Lucky Egg";
                case "poke_rod": return "Poke Rod";


                case "healing_machine": return "Healing Machine";
                case "pc": return "PC";
                case "fossil_analyzer": return "Fossil Analyzer";
                case "restoration_tank": return "Restoration Tank";
                case "pasture_block": return "Pasture Block";
                case "display_case": return "Display Case";
                case "monitor": return "Monitor";


                case "pomeg_berry": return "Pomeg Berry";
                case "kelpsy_berry": return "Kelpsy Berry";
                case "qualot_berry": return "Qualot Berry";
                case "hondew_berry": return "Hondew Berry";
                case "grepa_berry": return "Grepa Berry";
                case "tamato_berry": return "Tamato Berry";

                case "health_feather": return "Health Feather";
                case "muscle_feather": return "Muscle Feather";
                case "resist_feather": return "Resist Feather";
                case "genius_feather": return "Genius Feather";
                case "clever_feather": return "Clever Feather";
                case "swift_feather": return "Swift Feather";

                case "power_anklet": return "Power Anklet";
                case "power_band": return "Power Band";
                case "power_belt": return "Power Belt";
                case "power_bracer": return "Power Bracer";
                case "power_lens": return "Power Lens";
                case "power_weight": return "Power Weight";


            }
        }





        clean = clean.replace("_", " ");

        String[] parts = clean.split(" ");
        StringBuilder formatted = new StringBuilder();

        for (String part : parts) {
            if (part.isEmpty()) continue;


            if (part.equalsIgnoreCase("xp")) {
                formatted.append("XP ");
                continue;
            }

            formatted.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1))
                    .append(" ");
        }

        return formatted.toString().trim();
    }

    private static final List<String> RANK_ORDER = List.of(
            "shiny", "elite", "mystic", "master", "legendary"
    );

    private static final List<Double> BOOST_ORDER = List.of(
            1.25, 1.5, 1.75, 2.0, 2.5
    );





    public boolean purchase(ServerPlayer player,
                            ShopItem item,
                            int amount,
                            EconomyBridge econ,
                            RankService rankService) {





        if (item.permission != null && item.permission.startsWith("group.")) {

            String newRank = item.permission.replace("group.", "");
            int newIndex = RANK_ORDER.indexOf(newRank);

            for (int i = newIndex + 1; i < RANK_ORDER.size(); i++) {
                String higher = RANK_ORDER.get(i);

                if (rankService.hasPerm(player, "group." + higher)) {
                    player.sendSystemMessage(Component.literal("§cYou already own a higher rank!"));
                    return false;
                }
            }
        }





        if (item.permission != null && !item.permission.isBlank()) {
            if (rankService.hasPerm(player, item.permission)) {

                String prefix;

                switch (item.currency.toLowerCase()) {
                    case "coins":
                        prefix = "&e&lCOIN SHOP ";
                        break;
                    case "tokens":
                        prefix = "&b&lTOKEN SHOP ";
                        break;
                    default:
                        prefix = "&e&lSHOP ";
                        break;
                }

                player.sendSystemMessage(
                        LegacyAmpersand.parse(prefix + "&7&l➤ &cError! You already own this.")
                );

                return false;
            }
        }

        double totalCost = item.buy * amount;





        if (!econ.has(player, totalCost, item.currency)) {
            player.sendSystemMessage(
                    Component.literal("§cNot enough " + item.currency + "!")
            );
            return false;
        }





        econ.take(player, totalCost, item.currency);






        if (item.permission != null && item.permission.startsWith("group.")) {

            String newRank = item.permission.replace("group.", "");

            int newIndex = RANK_ORDER.indexOf(newRank);

            if (newIndex != -1) {
                for (int i = 0; i < newIndex; i++) {

                    String oldRank = RANK_ORDER.get(i);

                    player.getServer().getCommands().performPrefixedCommand(
                            player.getServer().createCommandSourceStack(),
                            "lp user " + player.getGameProfile().getName() + " parent remove " + oldRank
                    );
                }
            }
        }


        if (item.permission != null && item.permission.startsWith("pixity.money.boost.")) {

            double newBoost = Double.parseDouble(
                    item.permission.replace("pixity.money.boost.", "")
            );

            int newIndex = BOOST_ORDER.indexOf(newBoost);

            if (newIndex != -1) {
                for (int i = 0; i < newIndex; i++) {

                    double oldBoost = BOOST_ORDER.get(i);

                    player.getServer().getCommands().performPrefixedCommand(
                            player.getServer().createCommandSourceStack(),
                            "lp user " + player.getGameProfile().getName()
                                    + " permission unset pixity.money.boost." + oldBoost
                    );
                }
            }
        }





        if (item.commands != null && !item.commands.isEmpty()) {
            for (String cmd : item.commands) {

                String parsed = cmd.replace("%player%", player.getGameProfile().getName());

                player.getServer().getCommands().performPrefixedCommand(
                        player.getServer().createCommandSourceStack(),
                        parsed
                );
            }
        }



        if (item.permission != null && item.permission.startsWith("pixity.cosmetic")) {


            player.getServer().getCommands().performPrefixedCommand(
                    player.getServer().createCommandSourceStack(),
                    "lp user " + player.getGameProfile().getName()
                            + " permission set " + item.permission + " true"
            );


            if (item.permission.contains("tag.")) {

                String tag = item.permission.replace("pixity.cosmetic.tag.", "");


                player.sendSystemMessage(Component.literal("§aTag unlocked: " + tag));
            }
        } else {







            if (item.mob != null && !item.mob.isBlank()) {

                for (int i = 0; i < amount; i++) {
                    player.getInventory().add(
                            org.howie.pixity.moderation.neoforge.spawners.SpawnerAPI.create(item.mob)
                    );
                }

            } else {




                var give = new net.minecraft.world.item.ItemStack(
                        net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
                                net.minecraft.resources.ResourceLocation.parse(item.item)
                        ),
                        amount
                );

                player.getInventory().add(give);
            }
    }

        player.sendSystemMessage(
                Component.literal("§aPurchased §e" + amount + "x " + item.name)
        );

        return true;
    }





    public Collection<ShopCategory> getCategories() {
        return categories.values();
    }

    public Shop getShop(String id) {
        return shops.get(id);
    }

    public ShopCategory getCategory(String id) {
        return categories.get(id);
    }

    private static String getCurrencySymbol(String currency) {
        if (currency == null) return "$";

        switch (currency.toLowerCase()) {
            case "money":
                return "$";
            case "tokens":
                return " Tokens";
            case "coins":
                return " Coins";
            default:
                return "$";
        }
    }


}



