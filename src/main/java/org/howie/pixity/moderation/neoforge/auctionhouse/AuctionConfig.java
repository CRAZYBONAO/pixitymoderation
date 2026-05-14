package org.howie.pixity.moderation.neoforge.auctionhouse;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class AuctionConfig {

    private static final Path FILE = Paths.get("config/pixity_auction.json");

    private static double defaultMin = 10;
    private static final Map<String, Double> ITEM_MIN = new HashMap<>();

    public static void load() {

        try {

            if (!Files.exists(FILE)) {
                saveDefault();
            }

            JsonObject json = JsonParser.parseReader(new FileReader(FILE.toFile())).getAsJsonObject();

            defaultMin = json.get("default_min_price").getAsDouble();

            ITEM_MIN.clear();

            JsonObject items = json.getAsJsonObject("item_min_prices");

            for (String key : items.keySet()) {
                ITEM_MIN.put(key, items.get(key).getAsDouble());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveDefault() {
        try {
            Files.createDirectories(FILE.getParent());

            try (FileWriter writer = new FileWriter(FILE.toFile())) {
                writer.write("""
{
  "default_min_price": 10,
  "item_min_prices": {}
}
""");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double getMinPrice(ItemStack stack) {

        ResourceLocation id = stack.getItem().builtInRegistryHolder().key().location();

        String key = id.toString();

        return ITEM_MIN.getOrDefault(key, defaultMin);
    }
}