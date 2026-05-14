package org.howie.pixity.moderation.neoforge.shop;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ShopItem {

    public int slot;
    public String item;
    public String name;


    public double buy;
    public double sell;

    public String currency;
    public String permission;
    public String category;

    public List<String> lore;
    public List<String> commands = new ArrayList<>();
    public String mob;
    public String gradientStart;
    public String gradientEnd;
    public String colorCode;
}
