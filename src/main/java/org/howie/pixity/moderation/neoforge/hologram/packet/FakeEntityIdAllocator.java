package org.howie.pixity.moderation.neoforge.hologram.packet;

import java.util.concurrent.atomic.AtomicInteger;

public class FakeEntityIdAllocator {

    private static final AtomicInteger NEXT_ID =
            new AtomicInteger(500000);

    public static int next() {

        return NEXT_ID.getAndIncrement();
    }
}