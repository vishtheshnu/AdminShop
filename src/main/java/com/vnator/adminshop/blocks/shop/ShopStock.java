package com.vnator.adminshop.blocks.shop;

import com.vnator.adminshop.AdminShop;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate;
import net.minecraftforge.fluids.capability.templates.FluidHandlerFluidMap;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import scala.Int;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Static class that stores category names and everything sold in the shop.
 */
public class ShopStock {

	public static ArrayList<ArrayList<ShopItem>> buyStock;
	public static ArrayList<ArrayList<ShopItem>> sellStock;

	public static String [] buyCategories;
	public static String [] sellCategories;

	public static FluidStack [] buyFluids;
	public static float [] buyFluidPrices;

	public static HashMap<String, ShopItem> sellMap = new HashMap<String, ShopItem>();
	public static HashMap<String, ShopItem> buyMap = new HashMap<String, ShopItem>();
	//public static HashMap<String, Float> sellItemMap = new HashMap<String, Float>();
	//public static HashMap<String, Float> sellFluidMap = new HashMap<String, Float>();
	//public static HashMap<Integer, Float> sellItemOredictMap = new HashMap<Integer, Float>();

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

	public static void setShopStock(ArrayList<ArrayList<ShopItem>> buyItems, ArrayList<ArrayList<ShopItem>> sellItems){
		if(buyStock != null)
			buyStock.clear();
		if(sellStock != null)
			sellStock.clear();

		for(ArrayList<ShopItem> cat : buyItems){
			System.out.println("Category:");
			for(ShopItem item : cat){
				System.out.println("\t"+item.toString());
			}
		}

		buyStock = buyItems;
		sellStock = sellItems;

		//Add to sell map
		sellMap.clear();
		for(ArrayList<ShopItem> siList : sellStock){
			for(ShopItem si : siList){
				String str = si.toString();
				System.out.println(str);
				sellMap.put(str, si);
			}
		}

		//Add to buy map
		buyMap.clear();
		for(ArrayList<ShopItem> siList : buyStock){
			for(ShopItem si : siList){
				String str = si.toString();
				System.out.println(str);
				buyMap.put(str, si);
			}
		}
	}

	/**
	 * Returns an ItemStack representing the parameter string. Returns null if string is an oredict entry
	 * @param s String to parse
	 * @return ItemStack that s represents. Null if s is an oredict entry
	 */
	private static ItemStack parseItemString(String s){
		//Remove trailing < and > if present
		if(s.charAt(0) == '<')
			s = s.substring(1);
		if(s.charAt(s.length()-1) == '>')
			s = s.substring(0, s.length()-1);

		NBTTagCompound nbt = null;
		//Retrieve NBT
		if(s.contains("{")){
			AdminShop.logger.log(Level.INFO, "Parsing item with NBT!");
			String nbtText = s.substring(s.indexOf('{'), s.lastIndexOf('}'));
			nbtText += '}';
			try {
				nbt = JsonToNBT.getTagFromJson(nbtText);
			}catch (NBTException e){
				AdminShop.logger.log(Level.ERROR, "Improperly formatted NBT in config!\n"+s);
			}
			s = s.substring(0, s.indexOf('{'));
			s = s.trim();
			AdminShop.logger.log(Level.INFO, "Split Strings:\n"+s+"\n"+nbtText);
		}

		//Parse String
		ItemStack toret = ItemStack.EMPTY;
		String[] parts = s.split(":");
		if (parts.length == 2){
			if(parts[0].equals("ore")){ //Oredict entry, return null to show that
				return null;
			}
			toret = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(s)), 1, 0, nbt);
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
			toret = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(name)), 1, meta, nbt);
		}else {
			AdminShop.logger.log(Level.ERROR, "Item string improperly formatted! " +
					"Improper number of fields separated by colons! " + s);
			return ItemStack.EMPTY;
		}

		//Merge custom nbt with toret's current nbt tag
		//for(String key : nbt.getKeySet()){
		if(nbt != null)
			toret.setTagCompound(nbt);
		//}
		AdminShop.logger.log(Level.INFO, "Item NBT: "+toret.getTagCompound());
		return toret;
	}

	/**
	 * Parse an oredict representing string into a ShopItemStack.
	 * @param s String to parse
	 * @return ShopItemStack made from the parsed string
	 */
	public static ShopItemStack parseOredict(String s){
		NBTTagCompound nbt = null;
		if(s.contains("{")){
			AdminShop.logger.log(Level.INFO, "Parsing item with NBT!");
			String nbtText = s.substring(s.indexOf('{'), s.lastIndexOf('}'));
			nbtText += '}';
			try {
				nbt = JsonToNBT.getTagFromJson(nbtText);
			}catch (NBTException e){
				AdminShop.logger.log(Level.ERROR, "Improperly formatted NBT in config!\n"+s);
			}
			s = s.substring(0, s.indexOf('{'));
			s = s.trim();
			AdminShop.logger.log(Level.INFO, "Split Strings:\n"+s+"\n"+nbtText);
		}

		String odString = s.split(":")[1];
		ShopItemStack toret = new ShopItemStack(odString, nbt);
		return toret;
	}

	public static String getItemName(ItemStack item){
		String name = item.getItem().getRegistryName()+":"+item.getMetadata();
		if(item.getTagCompound() != null)
			name += " "+item.getTagCompound().toString();
		return name;
	}

	public static String getFluidName(FluidStack fluid){
		String name = fluid.getFluid().getName();
		if(fluid.tag != null)
			name += " "+fluid.tag.toString();

		return name;
	}

}
