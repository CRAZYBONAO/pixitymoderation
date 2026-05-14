package org.howie.pixity.moderation.neoforge.rollback;

import java.util.List;

public final class RollbackEntry {

    public long ts;


    public String action;

    public String playerUuid;
    public String playerName;

    public String dim;
    public int x, y, z;


    public String beforeBlockState;


    public List<String> beforeItems;


    public List<String> beforeCapItems;


    public String beforeBlockEntityNbt;


    public String blockId;



    public String networkKind;


    public String terminalType;


    public String terminalName;


    public List<String> networkDelta;


    public List<String> networkFluidDelta;

    public RollbackEntry() {}
}
