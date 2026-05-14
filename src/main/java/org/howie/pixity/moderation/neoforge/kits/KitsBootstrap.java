package org.howie.pixity.moderation.neoforge.kits;

import org.apache.logging.log4j.Logger;
import org.howie.pixity.moderation.neoforge.rank.RankService;
import org.howie.pixity.moderation.neoforge.economy.EconomyService;

import java.nio.file.Path;

public final class KitsBootstrap {

    private final Logger logger;
    private final Path dataDir;
    private final RankService perms;
    private final EconomyService economy;

    private final KitManager kitManager;

    public KitsBootstrap(final Logger logger,
                         final Path dataDir,
                         final RankService perms,
                         final EconomyService economy) {

        this.logger = logger;
        this.dataDir = dataDir;
        this.perms = perms;
        this.economy = economy;

        this.kitManager = new KitManager(
                logger,
                new KitStore(logger, dataDir),
                perms,
                economy
        );
    }

    public KitManager getManager() {
        return kitManager;
    }


}