package org.howie.pixity.moderation.neoforge.hologram.template;

import org.howie.pixity.moderation.neoforge.hologram.Hologram;
import org.howie.pixity.moderation.neoforge.hologram.animation.HologramAnimationType;

public class VoteCrateTemplate implements HologramTemplate {

    @Override
    public void apply(Hologram hologram) {

        hologram.lines().clear();

        hologram.addLine(
                "%glyph_vote% &#00ffff&lVOTE CRATE"
        );

        hologram.addLine(
                "&7Right Click to Open"
        );

        hologram.addLine(
                "&8Preview Rewards"
        );

        hologram.setLineSpacing(0.28);

        hologram.setAnimation(
                0,
                HologramAnimationType.RAINBOW
        );
    }
}