package com.vnator.adminshop.blocks.autoShop;

import com.vnator.adminshop.blocks.shop.ShopItem;
import com.vnator.adminshop.blocks.shop.ShopStock;
import com.vnator.adminshop.capabilities.BalanceAdapter;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.BlockTNT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class TileEntityAutoBuyer extends TileEntity implements ITickable, IInventory {

	private String player;
	private ItemStack toBuy = ItemStack.EMPTY;
	private boolean isBuyingItem = false; //True if buying item, false if buying fluid
	private int quantity = 1; //how much to buy at once at the specified rate
	private int rate = 0; //0 = per tick, 1 = per sec, 2 = per min
	private boolean isLocked = true;

	//Ghost inventory slot to record what to buy
	private ItemStackHandler inventory = new GhostItemStackHandler(1);
	private ItemStack tobuyItem;
	private FluidStack tobuyFluid;
	private float price;
	private int ticks = 0;

	public boolean getIsBuyingItem(){return isBuyingItem;}
	public int getQuantity(){return quantity;}
	public int getRate(){return rate;}

	public void setPlayer(String player){
		this.player = player;
	}

	public void setItem(ItemStack stack){
		toBuy = stack.copy();
	}

	public void setIsBuyingItem(boolean b){
		isBuyingItem = b;
	}

	public void setQuantity(int i){
		quantity = i;
	}

	public void incrementRate(){
		rate++;
		rate %= 3;
	}

	public String getPlayer(){
		return player;
	}

	public boolean isLocked(){
		return isLocked;
	}

	public void toggleLock(){
		isLocked = !isLocked;
		markDirty();
	}

	public void refreshBuyingItem(){
		tobuyFluid = null;
		tobuyItem = null;
	}

	@Override
	public void update(){
		if(!world.isRemote){
			//Wait until next purchase tick
			ticks++;
			switch(rate){
				case 0: ticks = 0; break;
				case 1: if(ticks >= 20){ticks = 0; break;}
				case 2: if(ticks >= 1200){ticks = 0; break;}
				default: return;
			}

			//Check if redstone signal is received
			if(!world.isBlockPowered(this.pos))
				return;

			//Refresh item being bought if needed
			ItemStack slotted = inventory.getStackInSlot(0);
			if(slotted == null || slotted.isEmpty())
				return;

			//if(FluidUtil.getFluidHandler(slotted) == null)
			//	isBuyingItem = true;

			if(isBuyingItem){
				if(tobuyItem == null || !slotted.isItemEqual(tobuyItem)){
					String itemName = getBuyItemName(slotted);
					System.out.println(itemName);
					if(itemName.equals("")) return; //Non-buyable item

					ShopItem shopItem = ShopStock.buyMap.get(itemName);
					tobuyItem = shopItem.getItem().copy();
					tobuyItem.setCount(quantity);
					price = shopItem.getPrice();
				}
			}
			else{
				if(tobuyFluid == null || !FluidUtil.getFluidContained(slotted).isFluidEqual(tobuyFluid)){
					if(FluidUtil.getFluidHandler(slotted) == null) return;
					String fluidName = getBuyFluidName(FluidUtil.getFluidContained(slotted));
					if(fluidName.equals("")) return; //Non-buyable fluid

					ShopItem shopItem = ShopStock.buyMap.get(fluidName);
					tobuyFluid = shopItem.getFluid().copy();
					tobuyFluid.amount = quantity;
					price = shopItem.getPrice();
				}
			}

			//Purchase the item/fluid if possible
			if(isBuyingItem){
				tobuyItem.setCount(quantity);
				TileEntity ent = world.getTileEntity(this.pos.up());
				if(ent == null || !ent.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN)) return;
				IItemHandler handler = ent.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
				float cost = price * quantity;
				boolean isTransactionSuccess = BalanceAdapter.canWithdraw(world, player, cost) &&
						ItemHandlerHelper.insertItem(handler, tobuyItem, true).isEmpty();
				System.out.println("Can buy: "+isTransactionSuccess+"; Cost: "+cost+"; can insert item: "+ItemHandlerHelper.insertItem(handler, toBuy, true).isEmpty());
				if(isTransactionSuccess){
					BalanceAdapter.withdraw(world, player, cost);
					ItemHandlerHelper.insertItem(handler, tobuyItem.copy(), false);
					System.out.println("Inserted item");
				}
			}else{
				tobuyFluid.amount = quantity;
				TileEntity ent = world.getTileEntity(this.pos.up());
				if(ent == null || !ent.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN)) return;
				IFluidHandler handler = ent.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN);
				float cost = price * quantity;
				System.out.println("cost: "+cost);
				boolean isTransactionSuccess = BalanceAdapter.canWithdraw(world, player, cost) &&
						handler.fill(tobuyFluid, false) == tobuyFluid.amount;
				System.out.println("Can buy fluid: "+isTransactionSuccess+"; filling amt: "+handler.fill(tobuyFluid, false));
				if(isTransactionSuccess){
					BalanceAdapter.withdraw(world, player, cost);
					handler.fill(tobuyFluid.copy(), true);
				}
			}
		}
	}

	private String getBuyItemName(ItemStack item){
		ArrayList<String> names = new ArrayList<>();
		String name = item.getItem().getRegistryName() + ":" + item.getMetadata();
		System.out.println("name of to buy:" + name);
		if (item.getTagCompound() != null) {
			if(item.getTagCompound().hasNoTags()) //add without tags if empty nbt
				names.add(name);
			name += " " + item.getTagCompound().toString();
			System.out.println("Other name: "+name);
		}
		names.add(name);


		//Reverse order so it checks tags before without tags
		for(int i = names.size()-1; i >= 0; i--){
			String s = names.get(i);
			if(ShopStock.buyMap.containsKey(s))
				return s;
		}
		return "";
	}

	private String getBuyFluidName(FluidStack fluid){
		String name = ShopStock.getFluidName(fluid);
		if(ShopStock.buyMap.containsKey(name))
			return name;
		return "";
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound){
		if(player != null)
			compound.setString("player", player);
		compound.setTag("inventory", inventory.serializeNBT());
		compound.setTag("tobuy", toBuy.serializeNBT());
		compound.setInteger("quantity", quantity);
		compound.setInteger("rate", rate);
		compound.setBoolean("isBuyingItem", isBuyingItem);
		compound.setBoolean("isLocked", isLocked);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		player = compound.getString("player");
		if(player == null || player.equals(""))
			player = null;
		inventory.deserializeNBT(compound.getCompoundTag("inventory"));
		toBuy.deserializeNBT(compound.getCompoundTag("tobuy"));
		quantity = compound.getInteger("quantity");
		rate = compound.getInteger("rate");
		isBuyingItem = compound.getBoolean("isBuyingItem");
		isLocked = compound.getBoolean("isLocked");
		super.readFromNBT(compound);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ||
				super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T)inventory;
		else
			return super.getCapability(capability, facing);
	}

	public int getField(int id){
		switch (id){
			case 0: return isBuyingItem ? 1 : 0;
			case 1: return quantity;
			case 2: return rate;
			default: return 0;
		}
	}

	public void setField(int id, int value){
		switch (id){
			case 0:
				isBuyingItem = (value == 1);
				break;
			case 1:
				quantity = value;
				break;
			case 2:
				rate = value;
				break;
			default: break;
		}
	}

	public int getFieldCount(){
		return 3;
	}

	//
	// Dummy impl of methods used by IInventory
	//

	@Override
	public int getSizeInventory() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int i1) {
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int i) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemStack) {

	}

	@Override
	public int getInventoryStackLimit() {
		return 0;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer entityPlayer) {
		return false;
	}

	@Override
	public void openInventory(EntityPlayer entityPlayer) {

	}

	@Override
	public void closeInventory(EntityPlayer entityPlayer) {

	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemStack) {
		return false;
	}

	@Override
	public void clear() {

	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}
}
