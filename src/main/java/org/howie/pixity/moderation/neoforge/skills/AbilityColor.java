package org.howie.pixity.moderation.neoforge.skills;

public class AbilityColor {

    public static String get(AbilityType ability) {

        return switch (ability) {




            case MINERS_FRENZY, DRILL, MINERS_LUCK, MINERS_BEST_FRIEND ->
                    "<gradient:#5D5D5D:#FFFFFF:#777777>";




            case DIGGING_FRENZY, QUICK_DIG, EXCAVATORS_KEEP, EXCAVATORS_BEST_FRIEND ->
                    "<gradient:#584212:#F1EF8D:#473308>";




            case WOODCUTTER_FRENZY, TREECAPITATOR, WOODCUTTERS_KEEP, WOODCUTTERS_BEST_FRIEND ->
                    "<gradient:#584212:#2BCB12:#473308>";




            case FARMERS_KEEP, MAGNETIC_CROPS, REPLANTER, FARMERS_DREAM ->
                    "<gradient:#2DFF00:#14640C:#2DFF00>";




            case STEADY_HANDS, EFFICIENT_BREWMAN, BREW_EFFICIANDO ->
                    "<gradient:#FFB000:#FFFFFF:#FFB000>";




            case ENCHANTERS_KEEP ->
                    "<gradient:#9537E5:#E918FF:#8E37E5>";




            case DOUBLE_STRIKE, HUNTERS_KEEP, HUNTERS_DREAM ->
                    "<gradient:#4C0F0F:#FFAE18:#4C0F0F>";




            case VAMPIRE, TRUE_STRIKE, KILLERS_KEEP, KILLERS_DREAM ->
                    "<gradient:#F61616:#4C0F0F:#F51717>";




            case BUILDERS_KEEP ->
                    "<gradient:#00FF83:#4F6FAB:#00FF83>";

            default -> "<white>";
        };
    }
}