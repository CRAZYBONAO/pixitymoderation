package org.howie.pixity.moderation.neoforge.chat.cosmetics;

import java.util.LinkedHashMap;
import java.util.Map;

public class ChatCosmeticsConfig {

    public Map<String, ColorOption> colors = new LinkedHashMap<>();
    public Map<String, GradientOption> gradients = new LinkedHashMap<>();
    public Map<String, AnimatedOption> animated = new LinkedHashMap<>();
    public Map<String, GlowOption> glow = new LinkedHashMap<>();



    public static class GlowOption {
        public String name;
        public String color;
        public String permission;
        public int price;
        public int thickness;
    }



    public static class ColorOption {
        public String name;
        public String code;
        public String description;
        public String permission;
        public CosmeticCategory category;
        public double price = 500;

        public ColorOption() {}

        public ColorOption(String name,
                           String code,
                           String description,
                           CosmeticCategory category,
                           String permission,
                           double price) {
            this.name = name;
            this.code = code;
            this.description = description;
            this.category = category;
            this.permission = permission;
            this.price = price;
        }
    }


    public static class GradientOption {
        public String name;
        public String start;
        public String end;
        public String description;
        public String permission;
        public CosmeticCategory category;
        public double price = 500;

        public GradientOption() {}

        public GradientOption(String name,
                              String start,
                              String end,
                              String description,
                              CosmeticCategory category,
                              String permission,
                              double price) {
            this.name = name;
            this.start = start;
            this.end = end;
            this.description = description;
            this.category = category;
            this.permission = permission;
            this.price = price;
        }
    }



    public static class AnimatedOption {
        public String name;
        public String[][] frames;
        public String description;
        public CosmeticCategory category;
        public String permission;
        public int price;

        public AnimatedOption() {}

        public AnimatedOption(String name,
                              String[][] frames,
                              String description,
                              CosmeticCategory category,
                              String permission,
                              int price) {
            this.name = name;
            this.frames = frames;
            this.description = description;
            this.category = category;
            this.permission = permission;
            this.price = price;
        }
    }



    protected ColorOption color(
            String name,
            String code,
            String desc,
            CosmeticCategory cat,
            String perm,
            double price
    ) {
        return new ColorOption(name, code, desc, cat, perm, price);
    }

    protected GradientOption grad(
            String name,
            String start,
            String end,
            String desc,
            CosmeticCategory cat,
            String perm,
            double price
    ) {
        return new GradientOption(name, start, end, desc, cat, perm, price);
    }

    protected AnimatedOption animated(
            String name,
            String[][] frames,
            String desc,
            CosmeticCategory cat,
            String perm,
            int price
    ) {
        return new AnimatedOption(name, frames, desc, cat, perm, price);
    }

    private GlowOption glow(
            String name,
            String color,
            String perm,
            int price
    ) {
        GlowOption o = new GlowOption();
        o.name = name;
        o.color = color;
        o.permission = perm;
        o.price = price;
        return o;
    }


}