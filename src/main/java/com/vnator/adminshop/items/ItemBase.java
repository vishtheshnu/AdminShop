package com.vnator.adminshop.items;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemBase extends Item {

    protected String name;

    public ItemBase(String name){
        this.name = name;
        setUnlocalizedName(name);
        setRegistryName(name);
        ModItems.itemsList.add(this);
        setCreativeTab(AdminShop.creativeTab);
    }

    public void registerItemModel(){
        AdminShop.proxy.registerItemRenderer(this, 0, name);
    }

    @Override
    public ItemBase setCreativeTab(CreativeTabs tab){
        super.setCreativeTab(tab);
        return this;
    }
}
