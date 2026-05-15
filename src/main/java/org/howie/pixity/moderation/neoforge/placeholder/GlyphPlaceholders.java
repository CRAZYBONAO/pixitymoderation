package org.howie.pixity.moderation.neoforge.placeholder;

import org.howie.pixity.moderation.neoforge.hologram.GlyphComponent;

public class GlyphPlaceholders {

    public static void register() {

        register("glyph_owner", "\uE000");
        register("glyph_manager", "\uE001");
        register("glyph_developer", "\uE002");
        register("glyph_headadmin", "\uE003");
        register("glyph_admin", "\uE004");
        register("glyph_trialadmin", "\uE005");
        register("glyph_mod", "\uE006");

        register("glyph_legendary", "\uE007");
        register("glyph_master", "\uE008");
        register("glyph_support", "\uE009");
        register("glyph_mystic", "\uE009");
        register("glyph_elite", "\uE00A");
        register("glyph_shiny", "\uE00B");

        register("glyph_elite4", "\uE00C");

        register("glyph_normalgym", "\uE00D");
        register("glyph_firegym", "\uE00E");
        register("glyph_watergym", "\uE00F");
        register("glyph_grassgym", "\uE010");
        register("glyph_electricgym", "\uE011");
        register("glyph_icegym", "\uE012");
        register("glyph_fightinggym", "\uE013");
        register("glyph_poisongym", "\uE014");
        register("glyph_groundgym", "\uE015");
        register("glyph_flyinggym", "\uE016");
        register("glyph_psychicgym", "\uE017");
        register("glyph_buggym", "\uE018");
        register("glyph_rockgym", "\uE019");
        register("glyph_ghostgym", "\uE01A");
        register("glyph_dragongym", "\uE01B");
        register("glyph_darkgym", "\uE01C");
        register("glyph_steelgym", "\uE01D");
        register("glyph_fairygym", "\uE01E");

        System.out.println(
                "[Pixity Glyphs] Loaded glyph placeholders."
        );
    }

    private static void register(
            String placeholder,
            String glyph
    ) {

        ComponentPlaceholderRegistry.register(

                "%" + placeholder + "%",

                () -> GlyphComponent.glyph(glyph)
        );
    }
}