package com.vnator.adminshop.blocks.shop;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Represents an item inside of the shop. Can be an item or an ore dictionary entry.
 * Handles checking capabilities too
 */
public class ShopItemStack{

	private ItemStack item;
	private String oreName;
	private NBTTagCompound nbt;
	private boolean isOreDict;

	public ShopItemStack(ItemStack item){
		this.item = item;
		isOreDict = false;
	}

	public ShopItemStack(String oreName, NBTTagCompound nbt){
		this.oreName = oreName;
		this.nbt = nbt;
		isOreDict = true;
	}

	public boolean itemEqual(ItemStack stack){
		if(isOreDict){
			for(ItemStack item : OreDictionary.getOres(oreName)){
				if(item.getItem().equals(stack.getItem()) && item.getMetadata() == stack.getMetadata()){
					boolean nbtSame = item.getTagCompound() == stack.getTagCompound() ||
							(item.getTagCompound() != null && item.getTagCompound().equals(stack.getTagCompound()));
					if(nbtSame)
						return true;
				}
			}
			return false;
		}else{
			boolean nbtSame = item.getTagCompound() == stack.getTagCompound() ||
					(item.getTagCompound() != null && item.getTagCompound().equals(stack.getTagCompound()));
			boolean itemSame = item.getItem().equals(stack.getItem()) && item.getMetadata() == stack.getMetadata();
			return nbtSame && itemSame;
		}
	}

	public boolean isOreDict(){
		return isOreDict;
	}

	public ItemStack getItem() {
		return item;
	}

	public String getOreName(){
		return oreName;
	}
}
