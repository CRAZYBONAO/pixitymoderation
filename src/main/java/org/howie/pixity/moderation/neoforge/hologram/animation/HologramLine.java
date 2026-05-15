package org.howie.pixity.moderation.neoforge.hologram.animation;

import java.util.ArrayList;
import java.util.List;

public class HologramLine {

    private String text;

    private HologramAnimationType animation =
            HologramAnimationType.NONE;

    private int speed = 2;

    private int tick = 0;

    private int state = 0;

    public HologramLine(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public HologramAnimationType animation() {
        return animation;
    }

    public void setAnimation(
            HologramAnimationType animation
    ) {
        this.animation = animation;
    }

    public int speed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String render(
            net.minecraft.server.level.ServerPlayer player
    ) {

        tick++;

        if (tick < speed) {
            return currentFrame();
        }

        tick = 0;

        state++;

        return currentFrame();
    }

    private String currentFrame() {

        return switch (animation) {

            case RAINBOW ->
                    rainbow();

            case SCROLL ->
                    scroll();

            case ROTATE ->
                    rotate();

            default ->
                    text;
        };
    }

    private String rainbow() {

        StringBuilder out =
                new StringBuilder();

        char[] chars =
                text.toCharArray();

        for (int i = 0; i < chars.length; i++) {

            float hue =
                    ((i * 0.08f)
                            + (state * 0.03f))
                            % 1.0f;

            java.awt.Color color =
                    java.awt.Color.getHSBColor(
                            hue,
                            1.0f,
                            1.0f
                    );

            String hex =
                    String.format(
                            "&#%02x%02x%02x",
                            color.getRed(),
                            color.getGreen(),
                            color.getBlue()
                    );

            out.append(hex)
                    .append(chars[i]);
        }

        return out.toString();
    }

    private String scroll() {

        String padded =
                text + "     ";

        int len =
                padded.length();

        int offset =
                state % len;

        return padded.substring(offset)
                + padded.substring(0, offset);
    }

    private String rotate() {

        List<String> frames =
                new ArrayList<>();

        frames.add("&b" + text);
        frames.add("&3" + text);
        frames.add("&9" + text);
        frames.add("&d" + text);

        return frames.get(
                state % frames.size()
        );
    }
}