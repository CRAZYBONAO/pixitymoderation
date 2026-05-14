package org.howie.pixity.moderation.neoforge.chat.cosmetics;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.howie.pixity.moderation.chat.TextFormatter;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.joml.Vector3f;

public class CosmeticService {



    private static final String KEY_WINGS = "pixity_wings";
    private static final String KEY_TRAIL = "pixity_trail";
    private static final String KEY_TAG = "pixity_tag";
    private static final String KEY_AURA = "pixity_aura";
    private static double tick = 0;
    private static final Map<UUID, String> activeWings = new ConcurrentHashMap<>();
    private static final Map<UUID, String> activeTrail = new ConcurrentHashMap<>();
    private static final Map<UUID, String> activeTag = new ConcurrentHashMap<>();
    private static final Map<UUID, String> activeAura = new ConcurrentHashMap<>();




    public static void tick(ServerPlayer player) {

        if (player.level().isClientSide()) return;

        tick += 0.15;

        String wings = activeWings.get(player.getUUID());
        if (wings != null) {
            renderWings(player, wings);
        }

        String trail = activeTrail.get(player.getUUID());
        if (trail != null) {
            renderTrail(player, trail);
        }

        String aura = activeAura.get(player.getUUID());
        if (aura != null) {
            renderAura(player, aura);
        }
    }


    public static void setWings(ServerPlayer player, String type) {

        activeWings.put(player.getUUID(), type);

        player.getPersistentData().putString(KEY_WINGS, type);
    }

    public static void setTrail(ServerPlayer player, String type) {

        activeTrail.put(player.getUUID(), type);

        player.getPersistentData().putString(KEY_TRAIL, type);
    }

    public static void setTag(ServerPlayer player, String tag) {

        activeTag.put(player.getUUID(), tag);

        player.getPersistentData().putString(KEY_TAG, tag);
    }

    public static Component buildTag(ServerPlayer p) {

        String type = getTag(p);

        if (type == null) return Component.empty();

        return switch (type.toLowerCase()) {

            case "charmander" ->
                    gradientTag("CHARMANDER", "#ff7a00", "#ff0000");

            case "squirtle" ->
                    gradientTag("SQUIRTLE", "#4facfe", "#00f2fe");

            case "bulbasaur" ->
                    gradientTag("BULBASAUR", "#56ab2f", "#a8e063");

            case "mewtwo" ->
                    gradientTag("MEWTWO", "#a18cd1", "#fbc2eb");

            case "mew" ->
                    gradientTag("MEW", "#ff9a9e", "#fad0c4");

            default -> Component.literal(type);
        };
    }

    public static Component gradientTag(String text, String startHex, String endHex) {

        boolean legendary =
                text.equalsIgnoreCase("MEWTWO") ||
                        text.equalsIgnoreCase("MEW") ||
                        text.equalsIgnoreCase("ARTICUNO") ||
                        text.equalsIgnoreCase("ZAPDOS") ||
                        text.equalsIgnoreCase("MOLTRES");

        Component closing = Component.literal("]");

        if (legendary) {
            closing = closing.copy().withStyle(style ->
                    style.withBold(true)
                            .withColor(net.minecraft.network.chat.TextColor.fromRgb(0xFFD700))
            );
        }

        Component full = Component.empty()
                .append(Component.literal("["))
                .append(TextFormatter.gradient(text, startHex, endHex))
                .append(Component.literal("]"));

        if (legendary) {
            full = full.copy().withStyle(style ->
                    style.withBold(true)
                            .withColor(net.minecraft.network.chat.TextColor.fromRgb(0xFFD700))
            );
        }

        return full;
    }

    public static void setAura(ServerPlayer player, String type) {
        activeAura.put(player.getUUID(), type);
        player.getPersistentData().putString(KEY_AURA, type);
    }

    public static String getTag(ServerPlayer player) {
        return activeTag.get(player.getUUID());
    }

    public static String getWings(ServerPlayer player) {
        return activeWings.get(player.getUUID());
    }

    public static String getTrail(ServerPlayer player) {
        return activeTrail.get(player.getUUID());
    }

    public static String getAura(ServerPlayer player) {
        return activeAura.get(player.getUUID());
    }



    private static boolean has(ServerPlayer p, String perm) {
        return new RankService().hasPerm(p, perm);
    }

    public static boolean has(ServerPlayer p, String type, RankService ranks) {
        return ranks.hasPerm(p, "pixity.cosmetic." + type);
    }



    private static void renderDNAWings(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        double yaw = Math.toRadians(p.getYRot());

        double backOffset = 0.6;
        double backX = Math.sin(yaw) * backOffset;
        double backZ = -Math.cos(yaw) * backOffset;

        Vec3 base = new Vec3(pos.x + backX, pos.y, pos.z + backZ);

        double baseY = base.y + 1.25;

        for (int i = 0; i < 12; i++) {

            double t = i * 0.4 + tick;

            double xOffset = Math.cos(t) * 0.4;
            double zOffset = Math.sin(t) * 0.4;

            double y = baseY + (i * 0.08);

            spawnDust(level, base.x + xOffset, y, base.z + zOffset, new Vector3f(1f, 0f, 1f));
            spawnDust(level, base.x - xOffset, y, base.z - zOffset, new Vector3f(0f, 1f, 1f));
        }
    }

    private static void renderShardWings(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        double yaw = Math.toRadians(p.getYRot());

        double backOffset = 0.6;
        double backX = Math.sin(yaw) * backOffset;
        double backZ = -Math.cos(yaw) * backOffset;

        Vec3 base = new Vec3(pos.x + backX, pos.y, pos.z + backZ);
        double baseY = base.y + 1.25;

        for (int i = 0; i < 12; i++) {

            double progress = i / 12.0;

            double spread = progress * 1.2;
            double jagged = ((i % 2 == 0) ? 0.3 : -0.3);

            double x = base.x + Math.cos(yaw) * (spread + jagged);
            double z = base.z + Math.sin(yaw) * (spread + jagged);
            double y = baseY + progress * 0.7;

            spawnDust(level, x, y, z, new Vector3f(0.3f, 0.8f, 1f));
        }
    }

    private static void renderWaveWings(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        double yaw = Math.toRadians(p.getYRot());

        double backOffset = 0.6;
        double backX = Math.sin(yaw) * backOffset;
        double backZ = -Math.cos(yaw) * backOffset;

        Vec3 base = new Vec3(pos.x + backX, pos.y, pos.z + backZ);
        double baseY = base.y + 1.25;

        for (int i = 0; i < 12; i++) {

            double progress = i / 12.0;

            double spread = progress * 1.0;
            double wave = Math.sin(tick + i) * 0.3;

            double x = base.x + Math.cos(yaw) * spread;
            double z = base.z + Math.sin(yaw) * spread;
            double y = baseY + wave + progress * 0.5;

            spawnDust(level, x, y, z, new Vector3f(0.2f, 0.6f, 1f));
        }
    }

    private static void renderSpiralWings(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        double yaw = Math.toRadians(p.getYRot());

        double backOffset = 0.6;
        double backX = Math.sin(yaw) * backOffset;
        double backZ = -Math.cos(yaw) * backOffset;

        Vec3 base = new Vec3(pos.x + backX, pos.y, pos.z + backZ);
        double baseY = base.y + 1.25;

        for (int i = 0; i < 20; i++) {

            double angle = i * 0.3 + tick;
            double radius = 0.5 + i * 0.02;

            double x = base.x + Math.cos(angle) * radius;
            double z = base.z + Math.sin(angle) * radius;
            double y = baseY + i * 0.05;

            spawnDust(level, x, y, z, new Vector3f(1f, 0.3f, 0.8f));
        }
    }

    private static void renderWings(ServerPlayer p, String type) {

        if (!has(p, "pixity.cosmetic.wings." + type)) return;

        var level = p.serverLevel();
        var pos = p.position();

        if (type.equalsIgnoreCase("poke_charmander")) { renderCharmanderWings(p); return; }

        if (type.equalsIgnoreCase("poke_squirtle")) { renderSquirtleWings(p); return; }

        if (type.equalsIgnoreCase("poke_bulbasaur")) { renderBulbasaurWings(p); return; }

        if (type.equalsIgnoreCase("dna")) {
            renderDNAWings(p);
            return;
        }

        if (type.equalsIgnoreCase("shard")) {
            renderShardWings(p);
            return;
        }

        if (type.equalsIgnoreCase("wave")) {
            renderWaveWings(p);
            return;
        }

        if (type.equalsIgnoreCase("spiral")) {
            renderSpiralWings(p);
            return;
        }

        if (type.equalsIgnoreCase("poke_pikachu")) { renderPikachuWings(p); return; }
        if (type.equalsIgnoreCase("poke_charizard")) { renderCharizardWings(p); return; }
        if (type.equalsIgnoreCase("poke_gengar")) { renderGengarWings(p); return; }
        if (type.equalsIgnoreCase("poke_mew")) { renderMewWings(p); return; }
        if (type.equalsIgnoreCase("poke_rayquaza")) { renderRayquazaWings(p); return; }


        double yaw = Math.toRadians(p.getYRot());

        double backOffset = 0.6;

        double backX = Math.sin(yaw) * backOffset;
        double backZ = -Math.cos(yaw) * backOffset;

        Vec3 base = new Vec3(
                pos.x + backX,
                pos.y,
                pos.z + backZ
        );

        double baseY = base.y + 1.25;

        double flap = Math.sin(tick) * 0.25;

        for (int i = 0; i < 12; i++) {

            double progress = i / 12.0;

            double spreadOuter = progress * 1.1;
            double heightOuter = Math.sin(progress * Math.PI) * 0.75;

            spawnWingPair(level, base, yaw,
                    spreadOuter,
                    baseY + heightOuter + flap,
                    type,
                    "edge"
            );


            double spreadMid = progress * 0.8;
            double heightMid = Math.sin(progress * Math.PI) * 0.55;

            spawnWingPair(level, base, yaw,
                    spreadMid,
                    baseY + heightMid + flap,
                    type,
                    "mid"
            );


            double spreadInner = progress * 0.45;
            double heightInner = Math.sin(progress * Math.PI) * 0.35;

            spawnWingPair(level, base, yaw,
                    spreadInner,
                    baseY + heightInner + flap,
                    type,
                    "core"
            );
        }
    }

    private static void spawnWingPair(ServerLevel level, Vec3 pos, double yaw,
                                      double spread, double y,
                                      String type, String layer) {

        double offsetX = Math.cos(yaw) * spread;
        double offsetZ = Math.sin(yaw) * spread;

        double depth = spread * 0.15;

        spawnWingParticle(level,
                pos.x - offsetX,
                y,
                pos.z - offsetZ + depth,
                type,
                layer
        );

        spawnWingParticle(level,
                pos.x + offsetX,
                y,
                pos.z + offsetZ + depth,
                type,
                layer
        );
    }


    private static void renderTrail(ServerPlayer p, String type) {

        if (!has(p, "pixity.cosmetic.trails." + type)) return;

        var level = p.serverLevel();
        var pos = p.position();

        if (p.getDeltaMovement().lengthSqr() < 0.01) return;

        switch (type.toLowerCase()) {

            case "basic" -> renderBasicTrail(level, pos);

            case "flame" -> renderFlameTrail(level, pos);

            case "magic" -> renderMagicTrail(level, pos);

            case "cloud" -> renderCloudTrail(level, pos);

            case "rainbow" -> renderRainbowTrail(level, pos);

            case "wave" -> renderWaveTrail(p);

            case "spiral" -> renderSpiralTrail(p);

            case "pulse" -> renderPulseTrail(p);

            case "velocity" -> renderVelocityTrail(p);

            case "ribbon" -> renderRibbonTrail(p);

            case "burst" -> renderBurstTrail(p);

            case "electric" -> renderElectricTrail(p);

            case "ice" -> renderIceTrail(p);

            case "poison" -> renderPoisonTrail(p);

            case "holy" -> renderHolyTrail(p);

            case "dark" -> renderDarkTrail(p);

            case "nature" -> renderNatureTrail(p);

            case "spark" -> renderSparkTrail(p);

            case "embers" -> renderEmberTrail(p);

            case "pikachu" -> renderPikachuTrail(p);

            case "charizard" -> renderCharizardTrail(p);

            case "gengar" -> renderGengarTrail(p);

            case "mew" -> renderMewTrail(p);

            case "rayquaza" -> renderRayquazaTrail(p);

            case "charmander" -> renderCharmanderTrail(p);

            case "squirtle" -> renderSquirtleTrail(p);

            case "bulbasaur" -> renderBulbasaurTrail(p);

        }
    }

    private static void renderBasicTrail(ServerLevel level, Vec3 pos) {
        spawnDust(level, pos.x, pos.y + 0.1, pos.z,
                new Vector3f(1f, 1f, 1f));
    }

    private static void renderFlameTrail(ServerLevel level, Vec3 pos) {
        spawnDust(level, pos.x, pos.y + 0.1, pos.z,
                new Vector3f(1f, 0.3f, 0f));
    }

    private static void renderMagicTrail(ServerLevel level, Vec3 pos) {
        spawnDust(level, pos.x, pos.y + 0.1, pos.z,
                new Vector3f(0.7f, 0.2f, 1f));
    }

    private static void renderCloudTrail(ServerLevel level, Vec3 pos) {
        spawnDust(level, pos.x, pos.y + 0.1, pos.z,
                new Vector3f(0.9f, 0.9f, 0.9f));
    }

    private static void renderRainbowTrail(ServerLevel level, Vec3 pos) {

        float r = (float)(Math.sin(tick) * 0.5 + 0.5);
        float g = (float)(Math.sin(tick + 2) * 0.5 + 0.5);
        float b = (float)(Math.sin(tick + 4) * 0.5 + 0.5);

        spawnDust(level, pos.x, pos.y + 0.1, pos.z,
                new Vector3f(r, g, b));
    }

    private static void renderRibbonTrail(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        Vec3 forward = getForward(p);
        Vec3 right = getRight(p);

        double wave = Math.sin(tick * 2) * 0.3;

        double x = pos.x - forward.x * 0.5 + right.x * wave;
        double z = pos.z - forward.z * 0.5 + right.z * wave;

        spawnDust(level, x, pos.y, z,
                new Vector3f(0.8f, 0.2f, 1f));
    }

    private static void renderBurstTrail(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        Vec3 forward = getForward(p);

        for (int i = 0; i < 6; i++) {

            double spread = (Math.random() - 0.5) * 0.6;

            double x = pos.x - forward.x * 0.3 + spread;
            double z = pos.z - forward.z * 0.3 + spread;

            spawnDust(level, x, pos.y, z,
                    new Vector3f(1f, 0.5f, 0f));
        }
    }




    private static void renderAura(ServerPlayer p, String type) {

        if (!has(p, "pixity.cosmetic.aura." + type)) return;

        var level = p.serverLevel();
        var pos = p.position();

        if (type.equalsIgnoreCase("aura_charmander")) { renderCharmanderAura(p); return; }

        if (type.equalsIgnoreCase("aura_squirtle")) { renderSquirtleAura(p); return; }

        if (type.equalsIgnoreCase("aura_bulbasaur")) { renderBulbasaurAura(p); return; }

        for (int i = 0; i < 6; i++) {

            double angle = (tick + i) * 0.5;

            double radius = 0.6;

            double x = pos.x + Math.cos(angle) * radius;
            double z = pos.z + Math.sin(angle) * radius;
            double y = pos.y + (i * 0.25);

            spawnAuraParticle(level, x, y, z, type);
        }
    }

    private static void renderWaveTrail(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        Vec3 forward = getForward(p);
        Vec3 right = getRight(p);

        for (int i = 0; i < 5; i++) {

            double offset = i * 0.2;

            double wave = Math.sin(tick + offset) * 0.3;

            double x = pos.x - forward.x * (0.4 + offset) + right.x * wave;
            double z = pos.z - forward.z * (0.4 + offset) + right.z * wave;

            spawnDust(level, x, pos.y + 0.1, z,
                    new Vector3f(0.2f, 0.6f, 1f));
        }
    }

    private static void renderSpiralTrail(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        Vec3 forward = getForward(p);
        Vec3 right = getRight(p);

        for (int i = 0; i < 6; i++) {

            double angle = tick + i * 0.5;
            double radius = 0.3;

            double side = Math.cos(angle) * radius;
            double back = Math.sin(angle) * radius;

            double x = pos.x - forward.x * (0.4 + back) + right.x * side;
            double z = pos.z - forward.z * (0.4 + back) + right.z * side;
            double y = pos.y + (i * 0.05);

            spawnDust(level, x, y, z,
                    new Vector3f(1f, 0.4f, 0.8f));
        }
    }

    private static void renderHelixAura(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        for (int i = 0; i < 12; i++) {

            double angle = tick + i * 0.5;

            double x1 = pos.x + Math.cos(angle) * 0.5;
            double z1 = pos.z + Math.sin(angle) * 0.5;

            double x2 = pos.x - Math.cos(angle) * 0.5;
            double z2 = pos.z - Math.sin(angle) * 0.5;

            double y = pos.y + i * 0.15;

            spawnDust(level, x1, y, z1, new Vector3f(0.6f, 0.2f, 1f));
            spawnDust(level, x2, y, z2, new Vector3f(0.2f, 0.8f, 1f));
        }
    }

    private static void renderPulseAura(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        double radius = 0.5 + Math.sin(tick * 2) * 0.3;

        for (int i = 0; i < 16; i++) {

            double angle = (Math.PI * 2 * i) / 16;

            double x = pos.x + Math.cos(angle) * radius;
            double z = pos.z + Math.sin(angle) * radius;

            spawnDust(level, x, pos.y + 0.1, z,
                    new Vector3f(1f, 0.3f, 0.3f));
        }
    }

    private static void renderBlackHoleAura(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        for (int i = 0; i < 20; i++) {

            double angle = tick * 2 + i;

            double radius = 1.0 - (i * 0.03);

            double x = pos.x + Math.cos(angle) * radius;
            double z = pos.z + Math.sin(angle) * radius;
            double y = pos.y + 0.5;

            spawnDust(level, x, y, z,
                    new Vector3f(0.2f, 0f, 0.3f));
        }
    }

    private static void renderVelocityTrail(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();
        var vel = p.getDeltaMovement();

        if (vel.lengthSqr() < 0.001) return;

        Vec3 dir = vel.normalize();

        double x = pos.x - dir.x * 0.6;
        double z = pos.z - dir.z * 0.6;

        spawnDust(level, x, pos.y + 0.1, z,
                new Vector3f(0.3f, 1f, 0.9f));
    }




    private static void renderPulseTrail(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        double radius = 0.2 + (Math.sin(tick * 2) * 0.2);

        for (int i = 0; i < 8; i++) {

            double angle = (Math.PI * 2 * i) / 8;

            double x = pos.x + Math.cos(angle) * radius;
            double z = pos.z + Math.sin(angle) * radius;

            spawnDust(level, x, pos.y + 0.1, z,
                    new Vector3f(1f, 0.2f, 0.2f));
        }
    }

    private static void renderElectricTrail(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        Vec3 forward = getForward(p);

        double x = pos.x - forward.x * 0.4;
        double z = pos.z - forward.z * 0.4;

        spawnDust(level, x, pos.y + 0.1, z,
                new Vector3f(0.6f, 0.9f, 1f));

        if (Math.random() < 0.2) {
            level.sendParticles(ParticleTypes.CRIT,
                    x, pos.y + 0.1, z,
                    1, 0, 0, 0, 0);
        }
    }

    private static void renderIceTrail(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        Vec3 forward = getForward(p);

        double x = pos.x - forward.x * 0.4;
        double z = pos.z - forward.z * 0.4;

        spawnDust(level, x, pos.y + 0.1, z,
                new Vector3f(0.7f, 0.9f, 1f));

        if (Math.random() < 0.1) {
            level.sendParticles(ParticleTypes.SNOWFLAKE,
                    x, pos.y + 0.1, z,
                    1, 0, 0, 0, 0);
        }
    }

    private static void renderPoisonTrail(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        Vec3 forward = getForward(p);

        double x = pos.x - forward.x * 0.4;
        double z = pos.z - forward.z * 0.4;

        spawnDust(level, x, pos.y + 0.1, z,
                new Vector3f(0.3f, 0.8f, 0.2f));

        if (Math.random() < 0.15) {
            level.sendParticles(ParticleTypes.WITCH,
                    x, pos.y + 0.1, z,
                    1, 0, 0, 0, 0);
        }
    }

    private static void renderHolyTrail(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        Vec3 forward = getForward(p);

        double x = pos.x - forward.x * 0.4;
        double z = pos.z - forward.z * 0.4;

        spawnDust(level, x, pos.y + 0.1, z,
                new Vector3f(1f, 1f, 0.8f));

        if (Math.random() < 0.2) {
            level.sendParticles(ParticleTypes.END_ROD,
                    x, pos.y + 0.2, z,
                    1, 0, 0, 0, 0);
        }
    }

    private static void renderDarkTrail(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        Vec3 forward = getForward(p);

        double x = pos.x - forward.x * 0.4;
        double z = pos.z - forward.z * 0.4;

        spawnDust(level, x, pos.y + 0.1, z,
                new Vector3f(0.2f, 0f, 0.3f));

        if (Math.random() < 0.2) {
            level.sendParticles(ParticleTypes.SMOKE,
                    x, pos.y + 0.1, z,
                    1, 0, 0, 0, 0);
        }
    }

    private static void renderNatureTrail(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        Vec3 forward = getForward(p);

        double x = pos.x - forward.x * 0.4;
        double z = pos.z - forward.z * 0.4;

        spawnDust(level, x, pos.y + 0.1, z,
                new Vector3f(0.2f, 0.7f, 0.2f));

        if (Math.random() < 0.15) {
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    x, pos.y + 0.2, z,
                    1, 0, 0, 0, 0);
        }
    }

    private static void renderSparkTrail(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        Vec3 forward = getForward(p);

        double x = pos.x - forward.x * 0.4;
        double z = pos.z - forward.z * 0.4;

        spawnDust(level, x, pos.y + 0.1, z,
                new Vector3f(1f, 0.8f, 0.3f));

        level.sendParticles(ParticleTypes.CRIT,
                x, pos.y + 0.1, z,
                1, 0, 0, 0, 0);
    }

    private static void renderEmberTrail(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        Vec3 forward = getForward(p);

        double x = pos.x - forward.x * 0.4;
        double z = pos.z - forward.z * 0.4;

        spawnDust(level, x, pos.y + 0.1, z,
                new Vector3f(1f, 0.4f, 0.1f));

        if (Math.random() < 0.2) {
            level.sendParticles(ParticleTypes.FLAME,
                    x, pos.y + 0.1, z,
                    1, 0, 0, 0, 0);
        }
    }

    private static void renderPikachuTrail(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();
        Vec3 forward = getForward(p);

        double x = pos.x - forward.x * 0.4;
        double z = pos.z - forward.z * 0.4;

        spawnDust(level, x, pos.y + 0.1, z,
                new Vector3f(1f, 0.9f, 0f));

        if (Math.random() < 0.25) {
            level.sendParticles(ParticleTypes.CRIT,
                    x, pos.y + 0.2, z,
                    1, 0, 0, 0, 0);
        }
    }

    private static void renderPikachuAura(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        for (int i = 0; i < 10; i++) {

            double angle = tick * 2 + i;

            double x = pos.x + Math.cos(angle) * 0.6;
            double z = pos.z + Math.sin(angle) * 0.6;

            spawnDust(level, x, pos.y + 0.2, z,
                    new Vector3f(1f, 0.9f, 0f));
        }
    }

    private static void renderPikachuWings(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        double yaw = Math.toRadians(p.getYRot());

        Vec3 base = new Vec3(
                pos.x + Math.sin(yaw) * 0.6,
                pos.y + 1.25,
                pos.z - Math.cos(yaw) * 0.6
        );

        for (int i = 0; i < 10; i++) {

            double offset = (i % 2 == 0 ? 0.4 : -0.4);

            double x = base.x + Math.cos(yaw) * offset;
            double z = base.z + Math.sin(yaw) * offset;
            double y = base.y + i * 0.05;

            spawnDust(level, x, y, z,
                    new Vector3f(1f, 0.9f, 0f));
        }
    }
    private static void renderCharizardWings(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        double yaw = Math.toRadians(p.getYRot());

        Vec3 base = new Vec3(
                pos.x + Math.sin(yaw) * 0.6,
                pos.y + 1.25,
                pos.z - Math.cos(yaw) * 0.6
        );

        for (int i = 0; i < 12; i++) {

            double spread = i * 0.08;

            double x = base.x + Math.cos(yaw) * spread;
            double z = base.z + Math.sin(yaw) * spread;
            double y = base.y + Math.sin(i * 0.5) * 0.4;

            spawnDust(level, x, y, z,
                    new Vector3f(1f, 0.3f, 0f));
        }
    }

    private static void renderGengarWings(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        double yaw = Math.toRadians(p.getYRot());

        Vec3 base = new Vec3(
                pos.x + Math.sin(yaw) * 0.6,
                pos.y + 1.25,
                pos.z - Math.cos(yaw) * 0.6
        );

        for (int i = 0; i < 10; i++) {

            double offset = Math.sin(tick + i) * 0.3;

            double x = base.x + Math.cos(yaw) * offset;
            double z = base.z + Math.sin(yaw) * offset;
            double y = base.y + i * 0.05;

            spawnDust(level, x, y, z,
                    new Vector3f(0.5f, 0f, 0.7f));
        }
    }

    private static void renderMewWings(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        double yaw = Math.toRadians(p.getYRot());

        Vec3 base = new Vec3(
                pos.x + Math.sin(yaw) * 0.6,
                pos.y + 1.25,
                pos.z - Math.cos(yaw) * 0.6
        );

        for (int i = 0; i < 12; i++) {

            double floatY = Math.sin(tick + i) * 0.2;

            spawnDust(level,
                    base.x,
                    base.y + floatY + i * 0.05,
                    base.z,
                    new Vector3f(1f, 0.7f, 1f)
                    );
        }
    }
    private static void renderRayquazaWings(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        double yaw = Math.toRadians(p.getYRot());

        Vec3 base = new Vec3(
                pos.x + Math.sin(yaw) * 0.6,
                pos.y + 1.25,
                pos.z - Math.cos(yaw) * 0.6
        );

        for (int i = 0; i < 15; i++) {

            double angle = tick + i * 0.3;

            double x = base.x + Math.cos(angle) * 0.4;
            double z = base.z + Math.sin(angle) * 0.4;
            double y = base.y + i * 0.04;

            spawnDust(level, x, y, z,
                    new Vector3f(0.1f, 0.9f, 0.3f)
                    );
        }
    }

    private static void renderGengarAura(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        for (int i = 0; i < 10; i++) {

            double angle = tick + i;

            double x = pos.x + Math.cos(angle) * 0.5;
            double z = pos.z + Math.sin(angle) * 0.5;

            spawnDust(level, x, pos.y + 0.2, z,
                    new Vector3f(0.5f, 0f, 0.7f)
                    );
        }
    }

    private static void renderMewAura(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        for (int i = 0; i < 10; i++) {

            double angle = tick + i;

            double x = pos.x + Math.cos(angle) * 0.6;
            double z = pos.z + Math.sin(angle) * 0.6;

            double y = pos.y + Math.sin(tick + i) * 0.2;

            spawnDust(level, x, y, z,
                    new Vector3f(1f, 0.7f, 1f)
                   );
        }
    }

    private static void renderRayquazaAura(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        for (int i = 0; i < 15; i++) {

            double angle = tick * 2 + i;

            double x = pos.x + Math.cos(angle) * 0.7;
            double z = pos.z + Math.sin(angle) * 0.7;

            spawnDust(level, x, pos.y + 0.3, z,
                    new Vector3f(0.1f, 0.9f, 0.3f)
                    );
        }
    }

    private static void renderCharizardTrail(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();
        Vec3 forward = getForward(p);

        double x = pos.x - forward.x * 0.4;
        double z = pos.z - forward.z * 0.4;

        spawnDust(level, x, pos.y + 0.1, z,
                new Vector3f(1f, 0.4f, 0f));

        if (Math.random() < 0.3) {
            level.sendParticles(ParticleTypes.FLAME,
                    x, pos.y + 0.1, z,
                    1, 0, 0, 0, 0);
        }
    }

    private static void renderCharizardAura(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        for (int i = 0; i < 12; i++) {

            double angle = tick + i;

            double x = pos.x + Math.cos(angle) * 0.7;
            double z = pos.z + Math.sin(angle) * 0.7;

            spawnDust(level, x, pos.y + 0.3, z,
                    new Vector3f(1f, 0.3f, 0f));
        }
    }

    private static void renderGengarTrail(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();
        Vec3 forward = getForward(p);

        double x = pos.x - forward.x * 0.4;
        double z = pos.z - forward.z * 0.4;

        spawnDust(level, x, pos.y + 0.1, z,
                new Vector3f(0.5f, 0f, 0.7f));

        if (Math.random() < 0.2) {
            level.sendParticles(ParticleTypes.SMOKE,
                    x, pos.y + 0.1, z,
                    1, 0, 0, 0, 0);
        }
    }

    private static void renderMewTrail(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        double floatY = Math.sin(tick) * 0.2;

        spawnDust(level,
                pos.x,
                pos.y + floatY,
                pos.z,
                new Vector3f(1f, 0.7f, 1f));
    }

    private static void renderRayquazaTrail(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        for (int i = 0; i < 6; i++) {

            double angle = tick + i;

            double x = pos.x + Math.cos(angle) * 0.5;
            double z = pos.z + Math.sin(angle) * 0.5;

            spawnDust(level, x, pos.y + 0.2, z,
                    new Vector3f(0.1f, 0.9f, 0.3f));
        }
    }



    private static void spawn(ServerLevel level, double x, double y, double z, net.minecraft.core.particles.ParticleOptions particle) {
        level.sendParticles(
                particle,
                x, y, z,
                1,
                0, 0, 0,
                0
        );
    }

    private static void spawnAuraParticle(ServerLevel level,
                                          double x, double y, double z,
                                          String type) {

        switch (type.toLowerCase()) {


            case "flame" ->
                    level.sendParticles(ParticleTypes.FLAME, x, y, z, 1, 0, 0, 0, 0);

            case "smoke" ->
                    level.sendParticles(ParticleTypes.SMOKE, x, y, z, 1, 0, 0, 0, 0);

            case "magic" ->
                    level.sendParticles(ParticleTypes.WITCH, x, y, z, 1, 0, 0, 0, 0);

            case "cloud" ->
                    level.sendParticles(ParticleTypes.CLOUD, x, y, z, 1, 0, 0, 0, 0);

            case "rainbow" -> {
                float r = (float)(Math.sin(tick) * 0.5 + 0.5);
                float g = (float)(Math.sin(tick + 2) * 0.5 + 0.5);
                float b = (float)(Math.sin(tick + 4) * 0.5 + 0.5);

                level.sendParticles(
                        new DustParticleOptions(new Vector3f(r, g, b), 1.2f),
                        x, y, z,
                        1,
                        0, 0, 0,
                        0
                );
            }
        }
    }

    private static void spawnWingParticle(ServerLevel level,
                                          double x, double y, double z,
                                          String type, String layer) {

        float size = switch (layer) {
            case "core" -> 1.4f;
            case "mid" -> 1.1f;
            default -> 0.85f;
        };

        Vector3f color;

        switch (type.toLowerCase()) {


            case "rainbow" -> {
                float r = (float)(Math.sin(tick) * 0.5 + 0.5);
                float g = (float)(Math.sin(tick + 2) * 0.5 + 0.5);
                float b = (float)(Math.sin(tick + 4) * 0.5 + 0.5);
                color = new Vector3f(r, g, b);
            }


            case "inferno" -> {
                color = switch (layer) {
                    case "core" -> new Vector3f(1f, 0.3f, 0.0f);
                    case "mid" -> new Vector3f(1f, 0.15f, 0.0f);
                    default -> new Vector3f(0.6f, 0.05f, 0.0f);
                };
            }


            case "frost" -> {
                color = switch (layer) {
                    case "core" -> new Vector3f(0.8f, 0.95f, 1f);
                    case "mid" -> new Vector3f(0.6f, 0.85f, 1f);
                    default -> new Vector3f(0.4f, 0.7f, 1f);
                };
            }


            case "storm" -> {
                color = switch (layer) {
                    case "core" -> new Vector3f(0.9f, 0.9f, 1f);
                    case "mid" -> new Vector3f(0.5f, 0.6f, 1f);
                    default -> new Vector3f(0.2f, 0.3f, 0.8f);
                };
            }


            case "void" -> {
                color = switch (layer) {
                    case "core" -> new Vector3f(0.2f, 0f, 0.3f);
                    case "mid" -> new Vector3f(0.1f, 0f, 0.2f);
                    default -> new Vector3f(0.05f, 0f, 0.1f);
                };
            }


            case "celestial" -> {
                color = switch (layer) {
                    case "core" -> new Vector3f(1f, 1f, 1f);
                    case "mid" -> new Vector3f(1f, 0.95f, 0.7f);
                    default -> new Vector3f(1f, 0.85f, 0.5f);
                };
            }


            case "galaxy" -> {
                float r = (float)(Math.sin(tick * 0.6) * 0.5 + 0.5);
                float g = (float)(Math.sin(tick * 0.6 + 2) * 0.5 + 0.5);
                float b = (float)(Math.sin(tick * 0.6 + 4) * 0.5 + 0.5);
                color = new Vector3f(r * 0.6f, g * 0.6f, b);
            }


            case "neon" -> {
                color = new Vector3f(0.0f, 1.0f, 0.8f);
            }


            case "pulse" -> {
                float pulse = (float)(Math.sin(tick) * 0.5 + 0.5);
                size *= (0.8f + pulse * 0.6f);
                color = new Vector3f(1f, 0.2f + pulse * 0.5f, 0.2f);
            }


            case "phantom" -> {
                color = new Vector3f(0.6f, 0.6f, 0.8f);


                if ((int)(tick * 10) % 2 == 0) return;
            }


            case "hologram" -> {
                color = new Vector3f(0.3f, 1f, 1f);


                if (Math.random() < 0.3) return;
            }

            default -> {
                color = new Vector3f(1f, 1f, 1f);
            }
        }

        level.sendParticles(
                new DustParticleOptions(color, size),
                x, y, z,
                1,
                0, 0, 0,
                0
        );
    }

    private static void renderCharmanderTrail(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();
        Vec3 forward = getForward(p);

        String evo = getEvolution(p, "charmander");

        double x = pos.x - forward.x * 0.4;
        double z = pos.z - forward.z * 0.4;

        switch (evo) {

            case "base" -> {
                spawnDust(level, x, pos.y + 0.1, z,
                        new Vector3f(1f, 0.4f, 0.1f));

                if (Math.random() < 0.2) {
                    level.sendParticles(ParticleTypes.FLAME,
                            x, pos.y + 0.1, z,
                            1, 0, 0, 0, 0);
                }
            }

            case "evolved" -> {
                spawnDust(level, x, pos.y + 0.1, z,
                        new Vector3f(1f, 0.25f, 0f));

                for (int i = 0; i < 2; i++) {
                    level.sendParticles(ParticleTypes.FLAME,
                            x, pos.y + 0.1, z,
                            1, 0, 0, 0, 0);
                }
            }

            case "final" -> {
                spawnDust(level, x, pos.y + 0.1, z,
                        new Vector3f(1f, 0.2f, 0f));

                for (int i = 0; i < 4; i++) {
                    level.sendParticles(ParticleTypes.FLAME,
                            x, pos.y + 0.1, z,
                            1, 0, 0, 0, 0);
                }

                if (Math.random() < 0.2) {
                    level.sendParticles(ParticleTypes.SMOKE,
                            x, pos.y + 0.1, z,
                            1, 0, 0, 0, 0);
                }
            }
        }
    }

    private static void renderCharmanderAura(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        for (int i = 0; i < 10; i++) {

            double angle = tick + i;

            double x = pos.x + Math.cos(angle) * 0.6;
            double z = pos.z + Math.sin(angle) * 0.6;

            spawnDust(level, x, pos.y + 0.3, z,
                    new Vector3f(1f, 0.3f, 0f));
        }
    }

    private static void renderCharmanderWings(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        double yaw = Math.toRadians(p.getYRot());
        String evo = getEvolution(p, "charmander");

        double scale = switch (evo) {
            case "evolved" -> 1.2;
            case "final" -> 1.6;
            default -> 1.0;
        };

        Vec3 base = new Vec3(
                pos.x + Math.sin(yaw) * 0.6,
                pos.y + 1.25,
                pos.z - Math.cos(yaw) * 0.6
        );

        int count = switch (evo) {
            case "final" -> 16;
            case "evolved" -> 12;
            default -> 8;
        };

        for (int i = 0; i < count; i++) {

            double spread = i * 0.08 * scale;

            double x = base.x + Math.cos(yaw) * spread;
            double z = base.z + Math.sin(yaw) * spread;
            double y = base.y + Math.sin(i * 0.5) * (0.4 * scale);

            spawnDust(level, x, y, z,
                    new Vector3f(1f, 0.3f, 0f));
        }
    }

    private static void renderSquirtleTrail(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();
        Vec3 forward = getForward(p);

        String evo = getEvolution(p, "squirtle");

        double x = pos.x - forward.x * 0.4;
        double z = pos.z - forward.z * 0.4;

        switch (evo) {

            case "base" -> {
                spawnDust(level, x, pos.y + 0.1, z,
                        new Vector3f(0.2f, 0.6f, 1f));

                if (Math.random() < 0.15) {
                    level.sendParticles(ParticleTypes.SPLASH,
                            x, pos.y + 0.1, z,
                            1, 0, 0, 0, 0);
                }
            }

            case "evolved" -> {
                spawnDust(level, x, pos.y + 0.1, z,
                        new Vector3f(0.1f, 0.5f, 1f));

                for (int i = 0; i < 2; i++) {
                    level.sendParticles(ParticleTypes.SPLASH,
                            x, pos.y + 0.1, z,
                            1, 0, 0, 0, 0);
                }
            }

            case "final" -> {
                spawnDust(level, x, pos.y + 0.1, z,
                        new Vector3f(0.1f, 0.4f, 1f));

                for (int i = 0; i < 4; i++) {
                    level.sendParticles(ParticleTypes.SPLASH,
                            x, pos.y + 0.1, z,
                            1, 0, 0, 0, 0);
                }
            }
        }
    }

    private static void renderSquirtleAura(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        String evo = getEvolution(p, "squirtle");

        int points = switch (evo) {
            case "final" -> 18;
            case "evolved" -> 14;
            default -> 10;
        };

        double radius = switch (evo) {
            case "final" -> 0.9;
            case "evolved" -> 0.75;
            default -> 0.6;
        };

        for (int i = 0; i < points; i++) {

            double angle = tick + i;

            double x = pos.x + Math.cos(angle) * radius;
            double z = pos.z + Math.sin(angle) * radius;

            spawnDust(level, x, pos.y + 0.2, z,
                    new Vector3f(0.2f, 0.6f, 1f));

            if (evo.equals("final") && Math.random() < 0.2) {
                level.sendParticles(ParticleTypes.SPLASH,
                        x, pos.y + 0.2, z,
                        1, 0, 0, 0, 0);
            }
        }
    }

    private static void renderSquirtleWings(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        double yaw = Math.toRadians(p.getYRot());
        String evo = getEvolution(p, "squirtle");

        double scale = switch (evo) {
            case "final" -> 1.6;
            case "evolved" -> 1.3;
            default -> 1.0;
        };

        int count = switch (evo) {
            case "final" -> 16;
            case "evolved" -> 12;
            default -> 8;
        };

        Vec3 base = new Vec3(
                pos.x + Math.sin(yaw) * 0.6,
                pos.y + 1.25,
                pos.z - Math.cos(yaw) * 0.6
        );

        for (int i = 0; i < count; i++) {

            double wave = Math.sin(tick + i) * 0.2;

            double x = base.x + Math.cos(yaw) * (i * 0.06 * scale);
            double z = base.z + Math.sin(yaw) * (i * 0.06 * scale);
            double y = base.y + wave + i * 0.05;

            spawnDust(level, x, y, z,
                    new Vector3f(0.2f, 0.6f, 1f));
        }
    }

private static void renderBulbasaurTrail(ServerPlayer p) {

    var level = p.serverLevel();
    var pos = p.position();
    Vec3 forward = getForward(p);

    String evo = getEvolution(p, "bulbasaur");

    double x = pos.x - forward.x * 0.4;
    double z = pos.z - forward.z * 0.4;

    switch (evo) {

        case "base" -> {
            spawnDust(level, x, pos.y + 0.1, z,
                    new Vector3f(0.2f, 0.8f, 0.2f));
        }

        case "evolved" -> {
            spawnDust(level, x, pos.y + 0.1, z,
                    new Vector3f(0.2f, 0.9f, 0.2f));

            if (Math.random() < 0.2) {
                level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        x, pos.y + 0.2, z,
                        1, 0, 0, 0, 0);
            }
        }

        case "final" -> {
            spawnDust(level, x, pos.y + 0.1, z,
                    new Vector3f(0.1f, 0.7f, 0.1f));

            for (int i = 0; i < 3; i++) {
                level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        x, pos.y + 0.2, z,
                        1, 0, 0, 0, 0);
            }
        }
    }
}

    private static void renderBulbasaurAura(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        String evo = getEvolution(p, "bulbasaur");

        int points = switch (evo) {
            case "final" -> 16;
            case "evolved" -> 12;
            default -> 10;
        };

        double radius = switch (evo) {
            case "final" -> 0.85;
            case "evolved" -> 0.7;
            default -> 0.6;
        };

        for (int i = 0; i < points; i++) {

            double angle = tick + i;

            double x = pos.x + Math.cos(angle) * radius;
            double z = pos.z + Math.sin(angle) * radius;

            spawnDust(level, x, pos.y + 0.25, z,
                    new Vector3f(0.2f, 0.8f, 0.2f));

            if (evo.equals("final") && Math.random() < 0.25) {
                level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        x, pos.y + 0.3, z,
                        1, 0, 0, 0, 0);
            }
        }
    }

    private static void renderBulbasaurWings(ServerPlayer p) {

        var level = p.serverLevel();
        var pos = p.position();

        double yaw = Math.toRadians(p.getYRot());
        String evo = getEvolution(p, "bulbasaur");

        double scale = switch (evo) {
            case "final" -> 1.6;
            case "evolved" -> 1.3;
            default -> 1.0;
        };

        int count = switch (evo) {
            case "final" -> 16;
            case "evolved" -> 12;
            default -> 8;
        };

        Vec3 base = new Vec3(
                pos.x + Math.sin(yaw) * 0.6,
                pos.y + 1.25,
                pos.z - Math.cos(yaw) * 0.6
        );

        for (int i = 0; i < count; i++) {

            double spread = i * 0.08 * scale;

            double x = base.x + Math.cos(yaw) * spread;
            double z = base.z + Math.sin(yaw) * spread;
            double y = base.y + i * 0.05;

            spawnDust(level, x, y, z,
                    new Vector3f(0.2f, 0.8f, 0.2f));
        }
    }



    private static String getEvolution(ServerPlayer p, String base) {


        if (has(p, "pixity.cosmetic." + base + ".final")) {
            return "final";
        }

        if (has(p, "pixity.cosmetic." + base + ".evolved")) {
            return "evolved";
        }

        return "base";
    }

    private static Vec3 getForward(ServerPlayer p) {
        double yaw = Math.toRadians(p.getYRot());
        return new Vec3(-Math.sin(yaw), 0, Math.cos(yaw));
    }

    private static Vec3 getRight(ServerPlayer p) {
        double yaw = Math.toRadians(p.getYRot());
        return new Vec3(Math.cos(yaw), 0, Math.sin(yaw));
    }

    public static Component buildTagPreview(String type) {

        return switch (type.toLowerCase()) {

            case "charmander" ->
                    gradientTag("CHARMANDER", "#ff7a00", "#ff0000");

            case "squirtle" ->
                    gradientTag("SQUIRTLE", "#4facfe", "#00f2fe");

            case "bulbasaur" ->
                    gradientTag("BULBASAUR", "#56ab2f", "#a8e063");

            case "charizard" ->
                    gradientTag("CHARIZARD", "#ff4500", "#ff0000");

            case "blastoise" ->
                    gradientTag("BLASTOISE", "#1e90ff", "#00bfff");

            case "venusaur" ->
                    gradientTag("VENUSAUR", "#228b22", "#7fff00");

            case "mewtwo" ->
                    gradientTag("MEWTWO", "#a18cd1", "#fbc2eb");

            case "mew" ->
                    gradientTag("MEW", "#ff9a9e", "#fad0c4");

            case "articuno" ->
                    gradientTag("ARTICUNO", "#74ebd5", "#9face6");

            case "zapdos" ->
                    gradientTag("ZAPDOS", "#fddb92", "#f6d365");

            case "moltres" ->
                    gradientTag("MOLTRES", "#ff512f", "#dd2476");

            default -> Component.literal(type.toUpperCase());
        };
    }






    public static void clear(ServerPlayer player) {

        UUID id = player.getUUID();

        activeWings.remove(id);
        activeTrail.remove(id);
        activeTag.remove(id);
        activeAura.remove(id);

        var data = player.getPersistentData();

        data.remove(KEY_WINGS);
        data.remove(KEY_TRAIL);
        data.remove(KEY_TAG);
        data.remove(KEY_AURA);
    }

    private static void spawnDust(ServerLevel level, double x, double y, double z, Vector3f color) {
        level.sendParticles(
                new DustParticleOptions(color, 1.1f),
                x, y, z,
                1,
                0, 0, 0,
                0
        );
    }

    public static void load(ServerPlayer player) {

        var data = player.getPersistentData();

        if (data.contains(KEY_WINGS)) {
            activeWings.put(player.getUUID(), data.getString(KEY_WINGS));
        }

        if (data.contains(KEY_TRAIL)) {
            activeTrail.put(player.getUUID(), data.getString(KEY_TRAIL));
        }

        if (data.contains(KEY_TAG)) {
            activeTag.put(player.getUUID(), data.getString(KEY_TAG));
        }

        if (data.contains(KEY_AURA)) {
            activeAura.put(player.getUUID(), data.getString(KEY_AURA));
        }
    }
}