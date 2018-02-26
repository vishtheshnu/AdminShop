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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
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

	public static FluidStack [] buyFluids;
	public static float [] buyFluidPrices;

	public static HashMap<String, Float> sellItemMap = new HashMap<String, Float>();
	public static HashMap<String, Float> sellFluidMap = new HashMap<String, Float>();

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
				String itemName = itemNames.get(i)[j];
				sellItems.get(i)[j] = parseItemString(itemName);
				AdminShop.logger.log(Level.INFO, "Sellable item: "+itemName);

				ItemStack stack = sellItems.get(i)[j];
				String myname = stack.getItem().getRegistryName() + ":" + stack.getMetadata();
				if(stack.getTagCompound() != null)
					myname += " "+stack.getTagCompound().toString();
				if(!sellItemMap.containsKey(myname))
					sellItemMap.put(myname, itemPrices.get(i)[j]);
				else
					sellItemMap.put(myname, Math.max(sellItemMap.get(myname), itemPrices.get(i)[j]));
			}
		}
	}
	public static void setShopLiquids(String [] buyNames, float [] buyPrices, String [] sellNames, float [] sellPrices){
		buyFluids = new FluidStack[buyNames.length];
		for(int i = 0; i < buyNames.length; i++){
			buyFluids[i] = parseFluidString(buyNames[i]);
		}
		buyFluidPrices = buyPrices;

		for(int i = 0; i < sellNames.length; i++){
			sellFluidMap.put(sellNames[i], sellPrices[i]);
		}
	}

	private static FluidStack parseFluidString(String s){
		String [] split = s.split(" ");

		NBTTagCompound tag = new NBTTagCompound();

		if(split.length == 2){ //NBT present
			try {
				tag = JsonToNBT.getTagFromJson(split[1]);
			}catch (NBTException e){
				AdminShop.logger.log(Level.ERROR, "Improperly formatted fluid nbt: "+s);
			}
		}else if(split.length > 2){
			AdminShop.logger.log(Level.ERROR, "Improperly formatted fluid nbt: "+s);
		}

		return new FluidStack(FluidRegistry.getFluid(split[0]), 1, tag);
	}

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
