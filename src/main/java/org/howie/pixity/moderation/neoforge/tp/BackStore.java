package org.howie.pixity.moderation.neoforge.tp;

import java.util.Map;
import java.util.UUID;

public interface BackStore {

    Map<UUID, WarpPos> load();

    void save(Map<UUID, WarpPos> map);
}