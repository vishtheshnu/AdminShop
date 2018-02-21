package com.vnator.adminshop.blocks.shop;

import com.vnator.adminshop.AdminShop;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Static class that loads shop contents on startup.
 */
public class ShopStock {

	public static ArrayList<ItemStack[]> buyItems;
	public static ArrayList<Float[]> buyItemPrices;

	public static ArrayList<ItemStack[]> sellItems;
	public static ArrayList<Float[]> sellItemPrices;

	public static String [] buyCategories;
	public static String [] sellCategories;

	public static void setShopCategories(String[] buyCats, String[] sellCats){
		if(buyCats.length != 0)
			buyCategories = buyCats;
		else
			buyCategories = new String[]{"Items"};

		if(sellCats.length != 0)
			sellCategories = sellCats;
		else
			sellCategories = new String[]{"Items"};
	}

	public static void setShopStockBuy(ArrayList<String[]> itemNames, ArrayList<Float[]> itemPrices){
		buyItems = new ArrayList<ItemStack[]>();
		buyItemPrices = itemPrices;

		for(int i = 0; i < itemNames.size(); i++){
			buyItems.add(new ItemStack[itemNames.get(i).length]);
			for(int j = 0; j < itemNames.get(i).length; j++){
				buyItems.get(i)[j] = parseItemString(itemNames.get(i)[j]);
			}
		}
	}

	public static void setShopStockSell(ArrayList<String[]> itemNames, ArrayList<Float[]> itemPrices){
		sellItems = new ArrayList<ItemStack[]>();
		sellItemPrices = itemPrices;

		for(int i = 0; i < itemNames.size(); i++){
			sellItems.add(new ItemStack[itemNames.get(i).length]);
			for(int j = 0; j < itemNames.get(i).length; j++){
				sellItems.get(i)[j] = parseItemString(itemNames.get(i)[j]);
			}
		}
	}

	private static ItemStack parseItemString(String s){
		//Remove trailing < and > if present
		if(s.charAt(0) == '<')
			s = s.substring(1);
		if(s.charAt(s.length()-1) == '>')
			s = s.substring(0, s.length()-1);

		//Parse String
		String[] parts = s.split(":");
		if (parts.length == 2){
			return new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(s)), 1, 0);
		}
		else if(parts.length == 3){
			String name = parts[0]+":"+parts[1];
			int meta = 0;
			try {
				meta = Integer.parseInt(parts[2]);
			}catch (NumberFormatException e){
				AdminShop.logger.log(Level.ERROR, "Item string metadata improperly formatted! "+s);
				return ItemStack.EMPTY;
			}
			return new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(name)), 1, meta);
		}
		AdminShop.logger.log(Level.ERROR, "Item string improperly formatted! " +
				"Improper number of fields separated by colons! "+s);
		return ItemStack.EMPTY;
	}

}
