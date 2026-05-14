package org.howie.pixity.moderation.neoforge.auctionhouse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.MinecraftServer;
import org.howie.pixity.moderation.neoforge.auctionhouse.gui.AuctionMenuState;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;

public class AuctionDatabase {

    private static final File FILE = new File("config/pixity/auction.json");

    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().create();

    private static final Map<UUID, AuctionListing> LISTINGS =
            new ConcurrentHashMap<>();

    private static volatile List<AuctionListing> ACTIVE = List.of();
    private static volatile List<AuctionListing> SORT_LOW = List.of();
    private static volatile List<AuctionListing> SORT_HIGH = List.of();
    private static volatile List<AuctionListing> SORT_NEW = List.of();

    private static final ExecutorService ASYNC =
            Executors.newSingleThreadExecutor();

    private static volatile boolean dirty = false;
    private static volatile boolean saving = false;


    public static void load() {
        try {

            if (!FILE.exists()) {
                saveSync();
                return;
            }

            Reader reader = new FileReader(FILE);
            Type type = new TypeToken<List<AuctionListing>>(){}.getType();

            List<AuctionListing> list = GSON.fromJson(reader, type);
            reader.close();

            if (list == null) return;

            LISTINGS.clear();

            for (AuctionListing l : list) {
                LISTINGS.put(l.id, l);
            }

            rebuildAsync();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static synchronized AuctionListing create(AuctionListing listing, MinecraftServer server) {


        LISTINGS.put(listing.id, listing);

        rebuildAsync();
        dirty = true;
        saveAsync();

        AuctionExpiryScheduler.schedule(server, listing);

        return listing;
    }


    public static AuctionListing get(UUID id) {
        return LISTINGS.get(id);
    }

    public static List<AuctionListing> getActive() {
        return ACTIVE;
    }

    public static List<AuctionListing> getSorted(AuctionMenuState.SortType sort) {
        return switch (sort) {
            case PRICE_LOW -> SORT_LOW;
            case PRICE_HIGH -> SORT_HIGH;
            default -> SORT_NEW;
        };
    }

    public static int getActiveCount(UUID uuid) {

        int count = 0;

        for (AuctionListing l : ACTIVE) {
            if (l.seller.equals(uuid)) {
                count++;
            }
        }

        return count;
    }


    public static void remove(UUID id) {
        LISTINGS.remove(id);
        rebuildAsync();
        dirty = true;
        saveAsync();
    }

    public static void expire(AuctionListing listing) {

        LISTINGS.remove(listing.id);
        addExpired(listing);

        rebuildAsync();
        dirty = true;
        saveAsync();
    }


    private static void rebuildAsync() {

        ASYNC.execute(() -> {

            long now = System.currentTimeMillis();

            List<AuctionListing> active = new ArrayList<>();

            for (AuctionListing l : LISTINGS.values()) {

                if (!l.sold && now < l.expiresAt) {
                    active.add(l);
                }
            }

            List<AuctionListing> low = new ArrayList<>(active);
            low.sort(Comparator.comparingDouble(l -> l.price));

            List<AuctionListing> high = new ArrayList<>(active);
            high.sort((a, b) -> Double.compare(b.price, a.price));

            List<AuctionListing> newest = new ArrayList<>(active);
            newest.sort((a, b) -> Long.compare(b.createdAt, a.createdAt));

            ACTIVE = active;
            SORT_LOW = low;
            SORT_HIGH = high;
            SORT_NEW = newest;
        });
    }


    public static void saveAsync() {

        if (!dirty || saving) return;

        saving = true;
        dirty = false;

        ASYNC.execute(() -> {
            try {
                saveSync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                saving = false;
            }
        });
    }

    private static void saveSync() {
        try {
            FILE.getParentFile().mkdirs();
            Writer writer = new FileWriter(FILE);
            GSON.toJson(LISTINGS.values(), writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static final Map<UUID, List<AuctionListing>> EXPIRED =
            new ConcurrentHashMap<>();

    public static void addExpired(AuctionListing listing) {
        EXPIRED.computeIfAbsent(listing.seller, k -> new ArrayList<>()).add(listing);
    }

    public static List<AuctionListing> getExpired(UUID uuid) {
        return new ArrayList<>(EXPIRED.getOrDefault(uuid, Collections.emptyList()));
    }

    public static void removeExpired(UUID uuid, AuctionListing listing) {
        List<AuctionListing> list = EXPIRED.get(uuid);
        if (list != null) {
            list.remove(listing);
            if (list.isEmpty()) EXPIRED.remove(uuid);
        }
    }

    public static void addExpired(UUID uuid, AuctionListing listing) {

        EXPIRED.computeIfAbsent(uuid, k -> new ArrayList<>()).add(listing);

        saveAsync();
    }
}