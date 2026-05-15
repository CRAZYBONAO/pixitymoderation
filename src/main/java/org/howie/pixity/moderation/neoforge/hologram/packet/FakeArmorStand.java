package org.howie.pixity.moderation.neoforge.hologram.packet;

public class FakeArmorStand {

    private static int NEXT_ID = 500000;

    private final int entityId;

    private final double x;
    private final double y;
    private final double z;

    private String text;

    private String lastRendered = "";

    public String lastRendered() {
        return lastRendered;
    }

    public void setLastRendered(String text) {
        this.lastRendered = text;
    }

    public FakeArmorStand(

            double x,
            double y,
            double z,

            String text
    ) {

        this.entityId =
                NEXT_ID++;

        this.x = x;
        this.y = y;
        this.z = z;

        this.text = text;
    }

    public int entityId() {
        return entityId;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public String text() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}