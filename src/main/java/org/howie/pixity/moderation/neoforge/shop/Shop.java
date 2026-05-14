package org.howie.pixity.moderation.neoforge.shop;

import java.util.ArrayList;
import java.util.List;

public class Shop {

    public String id;
    public int rows;

    public List<ShopItem> items = new ArrayList<>();
    public List<ShopSubCategory> subcategories = new ArrayList<>();
}
