package org.howie.pixity.moderation.neoforge.chat.cosmetics;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.howie.pixity.moderation.chat.TextFormatter;

import java.util.*;

public class AnimatedChatManager {

    private static class AnimatedMessage {
        MinecraftServer server;
        Component name;
        String text;
        String[][] frames;
        int index = 0;
        int ticks = 0;
        int life;
    }

    private final List<AnimatedMessage> active = new ArrayList<>();

    public void send(ServerPlayer player,
                     Component name,
                     String text,
                     String[][] frames) {

        AnimatedMessage msg = new AnimatedMessage();
        msg.server = player.server;
        msg.name = name;
        msg.text = text;
        msg.frames = frames;
        msg.life = 200;

        active.add(msg);
    }

    public void tick() {

        Iterator<AnimatedMessage> it = active.iterator();

        while (it.hasNext()) {

            AnimatedMessage m = it.next();

            m.ticks++;
            if (m.ticks > m.life) {
                it.remove();
                continue;
            }

            String[] frame = m.frames[m.index];

            Component msg = TextFormatter.gradient(
                    m.text,
                    frame[0],
                    frame[1]
            );

            Component finalMsg = Component.empty()
                    .append(m.name)
                    .append(Component.literal(" » "))
                    .append(msg);

            for (ServerPlayer sp :
                    m.server.getPlayerList().getPlayers()) {

                sp.sendSystemMessage(finalMsg, true);
            }

            m.index++;
            if (m.index >= m.frames.length)
                m.index = 0;
        }
    }
}