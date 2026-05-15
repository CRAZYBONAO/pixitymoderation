package org.howie.pixity.moderation.neoforge.contribution;

public class ContributionData {

    private double current;

    private double lifetime;

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = Math.max(0, current);
    }

    public double getLifetime() {
        return lifetime;
    }

    public void setLifetime(double lifetime) {
        this.lifetime = Math.max(0, lifetime);
    }

    public void add(double amount) {

        if (amount <= 0)
            return;

        current += amount;
        lifetime += amount;
    }

    public void remove(double amount) {

        if (amount <= 0)
            return;

        current = Math.max(0, current - amount);
    }
}