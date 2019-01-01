package com.vnator.adminshop.blocks.shop;

import com.vnator.adminshop.AdminShop;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;

/**
 * Represents an item or fluid that is buyable and/or sellable in the shop
 */
public class ShopItem {

	private ItemStack item;
	private FluidStack fluid;
	private String oredict;
	private NBTTagCompound nbt; //nbt tag used along with oredict

	private float price;

	public ShopItem(ItemStack item){
		this.item = item;
	}

	public ShopItem(FluidStack liquid){
		this.fluid = liquid;
	}

	public ShopItem(String oredict, NBTTagCompound nbt){
		this.oredict = oredict;
		this.nbt = nbt;
	}

	public boolean itemEqual(ItemStack myitem){
		if(oredict != null){
			for(ItemStack istack : OreDictionary.getOres(oredict)){
				if(istack.getItem().equals(myitem.getItem()) && istack.getMetadata() == myitem.getMetadata()){
					boolean nbtSame = nbt == myitem.getTagCompound() ||
							(istack.getTagCompound() != null && istack.getTagCompound().equals(myitem.getTagCompound()));
					if(nbtSame)
						return true;
				}
			}
			return false;
		}else if(item != null){
			boolean nbtSame = item.getTagCompound() == myitem.getTagCompound() ||
					(item.getTagCompound() != null && item.getTagCompound().equals(myitem.getTagCompound()));
			boolean itemSame = item.getItem().equals(myitem.getItem()) && item.getMetadata() == myitem.getMetadata();
			return nbtSame && itemSame;
		}else if(fluid != null){
			FluidStack contFluid = FluidUtil.getFluidContained(myitem);
			return contFluid != null && contFluid.getFluid().equals(fluid.getFluid());
		}else{
			return false;
		}
	}

	/**
	 * Converts the contents of this object into a string representation based on what is stored
	 * @return A String representation of the contents of this ShopItem
	 */
	public String toString(){
		if(item != null){
			String name = item.getItem().getRegistryName()+":"+item.getMetadata();
			if(item.getTagCompound() != null)
				name += " "+item.getTagCompound().toString();
			return name;
		}else if(fluid != null){
			String name = fluid.getFluid().getName();
			if(fluid.tag != null)
				name += " "+fluid.tag.toString();
			return name;
		}else if(oredict != null){
			String name = ""+OreDictionary.getOreID(oredict);
			if(nbt != null)
				name += " "+nbt.toString();
			return name;
		}else{ //No values set, return "null" string
			return "null";
		}
	}

	//Getters
	public boolean isItem(){
		return item != null;
	}

	public boolean isFluid(){
		return fluid != null;
	}

	public boolean isOredict(){
		return oredict != null;
	}

	public ItemStack getItem(){
		if(item == null)
			AdminShop.logger.log(Level.ERROR, "Trying to get ItemStack from ShopItem that isn't an ItemStack!");
		return item;
	}

	public FluidStack getFluid(){
		if(fluid == null)
			AdminShop.logger.log(Level.ERROR, "Trying to get Fluid from ShopItem that isn't a Fluid!");
		return fluid;
	}

	public String getOredict(){
		if(oredict == null)
			AdminShop.logger.log(Level.ERROR, "Trying to get OreDict from ShopItem that isn't an OreDict!");
		return oredict;
	}

	public NBTTagCompound getNbt() {
		return nbt;
	}

	public float getPrice(){
		return price;
	}

	//Setters
	public void setItem(ItemStack i){
		item = i;
		oredict = null;
		fluid = null;
	}

	public void setFluid(FluidStack f){
		fluid = f;
		item = null;
		oredict = null;
	}

	/**
	 * Set the nbt tag n to null if there is no nbt
	 * @param o string that represents the oredict entry name
	 * @param n nbt tag to filter with
	 */
	public void setOredict(String o, NBTTagCompound n){
		oredict = o;
		nbt = n;
		item = null;
		fluid = null;
	}

	public void setPrice(float p){
		price = p;
	}
}
