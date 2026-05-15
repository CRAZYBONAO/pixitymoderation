package org.howie.pixity.moderation.neoforge.hologram.template;

import java.util.HashMap;
import java.util.Map;

public class HologramTemplateRegistry {

    private static final Map<
            String,
            HologramTemplate
            > TEMPLATES =
            new HashMap<>();

    public static void register(
            String id,
            HologramTemplate template
    ) {

        TEMPLATES.put(
                id.toLowerCase(),
                template
        );
    }

    public static HologramTemplate get(
            String id
    ) {

        return TEMPLATES.get(
                id.toLowerCase()
        );
    }

    public static void registerDefaults() {

        register(
                "votecrate",
                new VoteCrateTemplate()
        );

        register(
                "firegym",
                new GymTemplate(
                        "%glyph_firegym%",
                        "Fire Gym",
                        "Blaze"
                )
        );

        register(
                "watergym",
                new GymTemplate(
                        "%glyph_watergym%",
                        "Water Gym",
                        "Aqua"
                )
        );

        System.out.println(
                "[Pixity Holograms] Loaded templates."
        );
    }
}