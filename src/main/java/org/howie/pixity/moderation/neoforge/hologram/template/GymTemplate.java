package org.howie.pixity.moderation.neoforge.hologram.template;

import org.howie.pixity.moderation.neoforge.hologram.Hologram;
import org.howie.pixity.moderation.neoforge.hologram.animation.HologramAnimationType;

public class GymTemplate implements HologramTemplate {

    private final String glyph;
    private final String gymName;
    private final String leader;

    public GymTemplate(
            String glyph,
            String gymName,
            String leader
    ) {

        this.glyph = glyph;
        this.gymName = gymName;
        this.leader = leader;
    }

    @Override
    public void apply(Hologram hologram) {

        hologram.lines().clear();

        hologram.addLine(
                glyph + " &6&l" + gymName
        );

        hologram.addLine(
                "&7Leader: &f" + leader
        );

        hologram.addLine(
                "&8Battle Ready"
        );

        hologram.setLineSpacing(0.30);

        hologram.setAnimation(
                0,
                HologramAnimationType.RAINBOW
        );
    }
}