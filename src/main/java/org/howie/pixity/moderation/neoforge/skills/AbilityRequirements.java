package org.howie.pixity.moderation.neoforge.skills;

public class AbilityRequirements {


    public static int getRequiredLevel(AbilityType ability) {
        return switch (ability) {


            case MINERS_LUCK -> 1;
            case MINERS_FRENZY -> 25;
            case DRILL -> 50;
            case MINERS_BEST_FRIEND -> 75;


            case EXCAVATORS_KEEP -> 1;
            case DIGGING_FRENZY -> 25;
            case QUICK_DIG -> 50;
            case EXCAVATORS_BEST_FRIEND -> 75;


            case WOODCUTTERS_KEEP -> 1;
            case WOODCUTTER_FRENZY -> 25;
            case TREECAPITATOR -> 50;
            case WOODCUTTERS_BEST_FRIEND -> 75;


            case HUNTERS_KEEP -> 1;
            case DOUBLE_STRIKE -> 35;
            case HUNTERS_DREAM -> 75;


            case KILLERS_KEEP -> 1;
            case VAMPIRE -> 25;
            case TRUE_STRIKE -> 50;
            case KILLERS_DREAM -> 75;


            case BUILDERS_KEEP -> 1;


            case STEADY_HANDS -> 1;
            case EFFICIENT_BREWMAN -> 50;
            case BREW_EFFICIANDO -> 75;


            case CRAFTERS_KEEP -> 1;


            case ENCHANTERS_KEEP -> 1;


            case TRAINERS_KEEP -> 1;
            case TRAINERS_INSIGHT -> 25;
            case ADRENALINE_RUSH -> 50;
            case INFUSION -> 75;


            case PROFESSORS_KEEP -> 1;
            case PRECISION_THROW -> 25;
            case CAPTURE_AURA -> 50;
            case GOTTA_CATCH_EM_ALL -> 75;

            default -> 0;
        };
    }

    public static SkillType getSkill(AbilityType ability) {
        return switch (ability) {

            case MINERS_LUCK, MINERS_FRENZY, DRILL, MINERS_BEST_FRIEND -> SkillType.MINER;

            case DIGGING_FRENZY, QUICK_DIG, EXCAVATORS_BEST_FRIEND -> SkillType.EXCAVATION;

            case WOODCUTTER_FRENZY, TREECAPITATOR, WOODCUTTERS_BEST_FRIEND -> SkillType.WOODCUTTER;

            default -> SkillType.MINER;
        };
    }
}