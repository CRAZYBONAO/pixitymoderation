package org.howie.pixity.moderation.neoforge.skills;

public class SkillColor {

    public static String get(SkillType type) {
        return switch (type) {

            case MINER -> "<gradient:#5D5D5D:#FFFFFF:#777777>&lMINER</gradient>";
            case BREWER -> "<gradient:#FFB000:#FFFFFF:#FFB000>&lBREWER</gradient>";
            case EXCAVATION -> "<gradient:#584212:#F1EF8D:#473308>&lEXCAVATOR</gradient>";
            case WOODCUTTER -> "<gradient:#584212:#2BCB12:#473308>&lWOODCUTTER</gradient>";
            case CRAFTER -> "<gradient:#E5AE37:#775C15:#E5AE37>&lCRAFTER</gradient>";
            case ENCHANTER -> "<gradient:#9537E5:#E918FF:#8E37E5>&lENCHANTER</gradient>";
            case HUNTER -> "<gradient:#4C0F0F:#FFAE18:#4C0F0F>&lHUNTER</gradient>";
            case KILLER -> "<gradient:#F61616:#4C0F0F:#F51717>&lKILLER</gradient>";
            case BUILDER -> "<gradient:#00FF83:#4F6FAB:#00FF83>&lBUILDER</gradient>";
            case FARMER -> "<gradient:#2DFF00:#14640C:#2DFF00>&lFARMER</gradient>";
            case TRAINER -> "<gradient:#EE8D8D:#FFFFFF:#EE8D8D>&lTRAINER</gradient>";
            case PROFESSOR -> "<gradient:#8DD6EE:#FFFFFF:#8DD6EE>&lPROFESSOR</gradient>";
        };
    }


    public static String getPlain(SkillType type) {
        return switch (type) {

            case MINER -> "<gradient:#5D5D5D:#FFFFFF:#777777>Miner</gradient>";
            case BREWER -> "<gradient:#FFB000:#FFFFFF:#FFB000>Brewer</gradient>";
            case EXCAVATION -> "<gradient:#584212:#F1EF8D:#473308>Excavator</gradient>";
            case WOODCUTTER -> "<gradient:#584212:#2BCB12:#473308>Woodcutter</gradient>";
            case CRAFTER -> "<gradient:#E5AE37:#775C15:#E5AE37>Crafter</gradient>";
            case ENCHANTER -> "<gradient:#9537E5:#E918FF:#8E37E5>Enchanter</gradient>";
            case HUNTER -> "<gradient:#4C0F0F:#FFAE18:#4C0F0F>Hunter</gradient>";
            case KILLER -> "<gradient:#F61616:#4C0F0F:#F51717>Killer</gradient>";
            case BUILDER -> "<gradient:#00FF83:#4F6FAB:#00FF83>Builder</gradient>";
            case FARMER -> "<gradient:#2DFF00:#14640C:#2DFF00>Farmer</gradient>";
            case TRAINER -> "<gradient:#EE8D8D:#FFFFFF:#EE8D8D>Trainer</gradient>";
            case PROFESSOR -> "<gradient:#8DD6EE:#FFFFFF:#8DD6EE>Professor</gradient>";
        };
    }
}