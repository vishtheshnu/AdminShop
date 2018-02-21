package com.vnator.adminshop.client;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class AdminshopTab extends CreativeTabs {

	public AdminshopTab(){
		super(AdminShop.MODID);
	}

	@Override
	public ItemStack getTabIconItem(){
		return new ItemStack(ModItems.ingotCopper);
	}
}
