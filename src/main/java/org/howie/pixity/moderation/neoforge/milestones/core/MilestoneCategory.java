package org.howie.pixity.moderation.neoforge.milestones.core;

public enum MilestoneCategory {

    MINING(
            "Mining",
            "<gradient:#5D5D5D:#FFFFFF:#777777>&lMINING MILESTONES</gradient>"
    ),

    FISHING(
            "Fishing",
            "<gradient:#00CFFF:#0066FF>&lFISHING MILESTONES</gradient>"
    ),

    FARMING(
            "Farming",
            "<gradient:#2DFF00:#14640C:#2DFF00>&lFARMING MILESTONES</gradient>"
    ),

    MOBS(
            "Mob Kills",
            "<gradient:#4C0F0F:#FFAE18:#4C0F0F>&lMOB KILL MILESTONES</gradient>"
    ),

    PROFESSOR(
            "Professor",
            "<gradient:#8DD6EE:#FFFFFF:#8DD6EE>&lPOKEMON CAPTURE MILESTONES</gradient>"
    ),

    TRAINER(
            "Trainer",
            "<gradient:#EE8D8D:#FFFFFF:#EE8D8D>&lTRAINING MILESTONES</gradient>"
    ),
    EVENTS(
            "Events",
            "<rainbow>&lEVENT MILESTONES</rainbow>"
    );

    public final String display;
    public final String title;

    MilestoneCategory(String display, String title) {
        this.display = display;
        this.title = title;
    }
}