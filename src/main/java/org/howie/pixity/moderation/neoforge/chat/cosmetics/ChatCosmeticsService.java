package org.howie.pixity.moderation.neoforge.chat.cosmetics;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.text.LegacyAmpersand;

import java.util.*;

public class ChatCosmeticsService {



    private final Map<UUID, String> color = new HashMap<>();
    private final Map<UUID, String> gradient = new HashMap<>();
    private final Map<UUID, String> nameGradient = new HashMap<>();
    private final Map<UUID, AnimatedGradient> animated = new HashMap<>();
    private final Map<UUID, Integer> animIndex = new HashMap<>();
    private final Map<UUID, String> nameColor = new HashMap<>();

    private final Map<UUID, AnimatedGradient> nameAnimated = new HashMap<>();
    private final Map<UUID, Integer> nameAnimIndex = new HashMap<>();
    private final Set<UUID> glow = new HashSet<>();

    private final CosmeticsStorage storage;
    private final ChatCosmeticsConfig config;
    private final RankService ranks;



    public void clearName(UUID uuid) {
        nameColor.remove(uuid);
        nameGradient.remove(uuid);
        nameAnimated.remove(uuid);
        nameAnimIndex.remove(uuid);
    }

    public void clearChat(UUID uuid) {
        color.remove(uuid);
        gradient.remove(uuid);
        animated.remove(uuid);
        animIndex.remove(uuid);
    }

    public static String buildPerm(boolean nameMode, CosmeticCategory cat, String key) {

        String type = nameMode ? "namecolor" : "chatcolor";

        String category = switch (cat) {
            case COLORS -> "basic";
            case PASTEL -> "pastel";
            case NEON -> "neon";
            case DARK -> "dark";
            case METAL -> "metal";
            case NATURE -> "nature";
            case FOOD -> "food";
            default -> "misc";
        };

        return "pixity.cosmetics." + type + "." + category + "." + key;
    }



    public boolean hasGlow(UUID uuid) {
        return glow.contains(uuid);
    }


    public void toggleGlow(ServerPlayer player) {

        if (!ranks.hasPerm(player, "pixity.cosmetic.glow")) {
            player.sendSystemMessage(
                    LegacyAmpersand.parse("&9&lCOSMETICS &7&l➤ §cError! You have not unlocked glow.")
            );
            return;
        }

        UUID id = player.getUUID();

        if (glow.contains(id)) {
            glow.remove(id);
            player.setGlowingTag(false);
        } else {
            glow.add(id);
            player.setGlowingTag(true);
        }
    }

    public void clearGlow(ServerPlayer player) {
        glow.remove(player.getUUID());
        player.setGlowingTag(false);
    }



    public void setNameColor(UUID uuid, String hex) {
        nameColor.put(uuid, hex);
        nameGradient.remove(uuid);
        nameAnimated.remove(uuid);
    }

    public void setNameGradient(UUID uuid, String key) {
        nameGradient.put(uuid, key);

        nameColor.remove(uuid);
        nameAnimated.remove(uuid);
    }

    public void setNameAnimated(UUID uuid, AnimatedGradient anim) {
        nameAnimated.put(uuid, anim);
        nameAnimIndex.put(uuid, 0);

        nameColor.remove(uuid);
        nameGradient.remove(uuid);
    }

    public String getNameColor(UUID uuid) {
        return nameColor.get(uuid);
    }

    public ChatCosmeticsConfig.GradientOption getNameGradient(UUID uuid) {
        String key = nameGradient.get(uuid);
        return key != null ? config.gradients.get(key) : null;
    }

    public AnimatedGradient getNameAnimated(UUID uuid) {
        return nameAnimated.get(uuid);
    }

    public String[] nextNameFrame(UUID uuid) {

        AnimatedGradient g = nameAnimated.get(uuid);
        if (g == null) return null;

        int index = nameAnimIndex.getOrDefault(uuid, 0);

        String[] frame = g.frames()[index];

        index++;
        if (index >= g.frames().length) index = 0;

        nameAnimIndex.put(uuid, index);

        return frame;
    }

    public ChatCosmeticsService(CosmeticsStorage storage, ChatCosmeticsConfig config, RankService ranks) {
        this.storage = storage;
        this.config = config;
        this.ranks = ranks;
    }

    public void setColor(UUID uuid, String hex) {
        color.put(uuid, hex);
        gradient.remove(uuid);
        animated.remove(uuid);

        storage.setColor(uuid, hex);
    }

    public void setGradient(UUID uuid, String key) {
        gradient.put(uuid, key);

        color.remove(uuid);
        animated.remove(uuid);

        var opt = config.gradients.get(key);
        if (opt != null) {
            storage.setGradient(uuid, opt.start, opt.end);
        }
    }

    public void setAnimated(UUID uuid, AnimatedGradient anim) {
        animated.put(uuid, anim);
        animIndex.put(uuid, 0);

        color.remove(uuid);
        gradient.remove(uuid);

        storage.setAnimated(uuid, anim.key);
    }

    public void clear(UUID uuid) {
        color.remove(uuid);
        gradient.remove(uuid);
        animated.remove(uuid);
        animIndex.remove(uuid);
    }

    public String getColor(UUID uuid) {
        return color.get(uuid);
    }

    public ChatCosmeticsConfig.GradientOption getGradient(UUID uuid) {
        String key = gradient.get(uuid);
        return key != null ? config.gradients.get(key) : null;
    }

    public AnimatedGradient getAnimated(UUID uuid) {
        return animated.get(uuid);
    }

    public String getChatColor(UUID uuid) {
        return color.get(uuid);
    }



    public String[] nextFrame(UUID uuid) {

        AnimatedGradient g = animated.get(uuid);
        if (g == null) return null;

        int index = animIndex.getOrDefault(uuid, 0);

        String[] frame = g.frames[index];

        index++;
        if (index >= g.frames.length) index = 0;

        animIndex.put(uuid, index);

        return frame;
    }

    public record Gradient(String start, String end) {}

    public static class AnimatedGradient {

        private final String[][] frames;
        private final String key;

        public AnimatedGradient(String key, String[][] frames) {
            this.key = key;
            this.frames = frames;
        }

        public String[][] frames() {
            return frames;
        }

        public String key() {
            return key;
        }
    }

    public boolean hasPermission(ServerPlayer player, String perm) {
        return ranks.hasPerm(player, perm);
    }


    public void load(UUID uuid) {


        CosmeticsStorage.Active active = storage.loadActive(uuid);
        if (active == null) return;



        switch (active.type()) {

            case "color" -> setColor(uuid, active.v1());

            case "gradient" -> {

                for (var entry : config.gradients.entrySet()) {

                    var opt = entry.getValue();

                    if (opt.start.equalsIgnoreCase(active.v1())
                            && opt.end.equalsIgnoreCase(active.v2())) {

                        setGradient(uuid, entry.getKey());
                        break;
                    }
                }
            }

            case "animated" -> {

                String key = active.v1();

                var opt = config.animated.get(key);
                if (opt != null) {
                    setAnimated(uuid,
                            new AnimatedGradient(key, opt.frames)
                    );
                }
            }
        }
    }

    public String getNameGradientKey(UUID uuid) {
        return nameGradient.get(uuid);
    }

    public String getGradientKey(UUID uuid) {
        return gradient.get(uuid);
    }


}