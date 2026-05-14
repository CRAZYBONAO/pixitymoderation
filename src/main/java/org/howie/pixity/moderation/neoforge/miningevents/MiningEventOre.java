package org.howie.pixity.moderation.neoforge.miningevents;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public enum MiningEventOre {





    COAL(
            "Coal Ore",
            "coal_ore_mined",
            Items.COAL_ORE,
            "&8"
    ),

    COPPER(
            "Copper Ore",
            "copper_ore_mined",
            Items.COPPER_ORE,
            "&#c77d4a"
    ),

    IRON(
            "Iron Ore",
            "iron_ore_mined",
            Items.IRON_ORE,
            "&7"
    ),

    GOLD(
            "Gold Ore",
            "gold_ore_mined",
            Items.GOLD_ORE,
            "&#baa114"
    ),

    REDSTONE(
            "Redstone Ore",
            "redstone_ore_mined",
            Items.REDSTONE_ORE,
            "&#66120c"
    ),

    LAPIS(
            "Lapis Ore",
            "lapis_ore_mined",
            Items.LAPIS_ORE,
            "&#121375"
    ),

    DIAMOND(
            "Diamond Ore",
            "diamond_ore_mined",
            Items.DIAMOND_ORE,
            "&b"
    );





    public final String display;

    public final String statColumn;

    public final Item icon;

    public final String color;





    MiningEventOre(
            String display,
            String statColumn,
            Item icon,
            String color
    ) {

        this.display = display;
        this.statColumn = statColumn;
        this.icon = icon;
        this.color = color;
    }





    public static MiningEventOre random() {

        MiningEventOre[] values =
                values();

        return values[
                new java.util.Random()
                        .nextInt(values.length)
                ];
    }





    public static MiningEventOre fromString(
            String s
    ) {

        for (MiningEventOre ore : values()) {

            if (
                    ore.name()
                            .equalsIgnoreCase(s)
            ) {
                return ore;
            }
        }

        return null;
    }
}