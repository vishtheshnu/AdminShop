package com.vnator.adminshop.blocks.autoShop;

import com.vnator.adminshop.blocks.shop.ShopStock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.oredict.OreDictionary;

public class ContainerAutoBuyer extends Container {

	TileEntityAutoBuyer autoBuyer;
	public boolean isBuyingItem;
	public int quantity, rate;

	public ContainerAutoBuyer(InventoryPlayer playerInv, final TileEntityAutoBuyer autoBuyer) {
		this.autoBuyer = autoBuyer;
		IItemHandler inventory = autoBuyer.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
		addSlotToContainer(new SlotItemHandler(inventory, 0, 80, 35){
			@Override
			public void onSlotChanged(){
				autoBuyer.markDirty();
				autoBuyer.refreshBuyingItem();
			}

			@Override
			public boolean isItemValid(ItemStack stack){
				//Check if the item itself is buyable
				String id = stack.getItem().getRegistryName() + ":" + stack.getMetadata();
				boolean itemIn = ShopStock.buyMap.containsKey(id);
				System.out.println(id);
				if(stack.getTagCompound() != null) {
					id += " " + stack.getTagCompound().toString();
					itemIn = itemIn || ShopStock.buyMap.containsKey(id);
					System.out.println(id);
				}
				if(itemIn)
					return true;

				//Check if oredict of the item is buyable
				boolean oreIn;
				int [] oreIDs = OreDictionary.getOreIDs(stack);
				for(int i : oreIDs) {
					System.out.println(i);
					if(ShopStock.buyMap.containsKey(""+i)) {
						System.out.println("Matches!");
						return true;
					}
					if(stack.hasTagCompound()){
						String checkKey = i+" "+stack.getTagCompound().toString();
						System.out.println(checkKey);
						if(ShopStock.buyMap.containsKey(checkKey))
							return true;
					}
				}

				//Check if item contains buyable fluid
				FluidStack fluid = FluidUtil.getFluidContained(stack);
				if(fluid != null){
					String name = fluid.getFluid().getName();
					if(ShopStock.buyMap.containsKey(name)) {
						System.out.println("Matches!");
						return true;
					}
					if(fluid.tag != null)
						name += " "+fluid.tag.toString();
					if(ShopStock.buyMap.containsKey(name))
						return true;
				}

				//Not a buyable item
				return false;
			}

		});

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int k = 0; k < 9; k++) {
			addSlotToContainer(new Slot(playerInv, k, 8 + k * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityPlayer) {
		return true;
	}

	@Override
	public void addListener(IContainerListener listener){
		super.addListener(listener);
		listener.sendAllWindowProperties(this, autoBuyer);
		quantity = autoBuyer.getField(1);
	}

	@Override
	public void detectAndSendChanges(){
		super.detectAndSendChanges();

		for(int i = 0; i < this.listeners.size(); i++){
			IContainerListener listener = this.listeners.get(i);
			if((isBuyingItem ? 1 : 0) != autoBuyer.getField(0))
				listener.sendWindowProperty(this, 0, autoBuyer.getField(0));
			if(quantity != autoBuyer.getField(1))
				listener.sendWindowProperty(this, 1, autoBuyer.getField(1));
			if(rate != autoBuyer.getField(2))
				listener.sendWindowProperty(this, 2, autoBuyer.getField(2));

			isBuyingItem = autoBuyer.getField(0) == 1;
			quantity = autoBuyer.getField(1);
			rate = autoBuyer.getField(2);
		}
	}

	@Override
	public void updateProgressBar(int id, int data){
		autoBuyer.setField(id, data);
	}


	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		return ItemStack.EMPTY;
		/*
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			int containerSlots = inventorySlots.size() - player.inventory.mainInventory.size();

			if (index < containerSlots) {
				if (!this.mergeItemStack(itemstack1, containerSlots, inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, containerSlots, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.getCount() == 0) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, itemstack1);
		}

		return itemstack;
		*/
	}
}
