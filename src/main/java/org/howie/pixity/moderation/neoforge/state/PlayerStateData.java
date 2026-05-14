package org.howie.pixity.moderation.neoforge.state;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public final class PlayerStateData {
    public Set<UUID> vanished = new HashSet<>();
    public Set<UUID> flying = new HashSet<>();
    public Set<UUID> god = new HashSet<>();
}
