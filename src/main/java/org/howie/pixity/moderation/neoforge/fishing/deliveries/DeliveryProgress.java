package org.howie.pixity.moderation.neoforge.fishing.deliveries;

public class DeliveryProgress {

    public int fishCaught;
    public int squidKilled;
    public int dolphinKilled;

    public void resetFish() {
        this.fishCaught = 0;
    }

    public void resetSquid() {
        this.squidKilled = 0;
    }

    public void resetDolphin() {
        this.dolphinKilled = 0;
    }
}