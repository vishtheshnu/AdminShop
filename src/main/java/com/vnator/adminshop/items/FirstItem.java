package com.vnator.adminshop.items;

import com.vnator.adminshop.AdminShop;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class FirstItem extends ItemBase {

    public FirstItem(){
        super("firstItem");
        setCreativeTab(CreativeTabs.BREWING);
    }
}
