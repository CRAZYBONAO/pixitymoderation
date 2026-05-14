package org.howie.pixity.moderation.neoforge.pokemon;

public class HologramData {

    public int id;
    public String world;
    public int x, y, z;
    public String type;

    public HologramData(int id, String world, int x, int y, int z, String type) {
        this.id = id;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
    }
}