package com.vnator.adminshop.blocks.itemseller;

import com.vnator.adminshop.blocks.shop.ShopStock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraft.inventory.Container;

import java.util.UUID;

public class ContainerItemSeller extends Container {

	public UUID owner;

	public ContainerItemSeller(InventoryPlayer playerInv, final TileEntityItemSeller seller){
		owner = seller.getPlayer();
		IItemHandler inventory = seller.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
		addSlotToContainer(new SlotItemHandler(inventory, 0, 80, 35){
			@Override
			public void onSlotChanged(){
				seller.markDirty();
			}

			@Override
			public boolean isItemValid(ItemStack stack){
				String id = stack.getItem().getRegistryName() + ":" + stack.getMetadata();
				return ShopStock.sellItemMap.containsKey(id);
			}
		});

		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 9; j++){
				addSlotToContainer(new Slot(playerInv, j + i*9 + 9, 8+j*18, 84+i*18));
			}
		}

		for(int k = 0; k < 9; k++){
			addSlotToContainer(new Slot(playerInv, k, 8+k*18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index){
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if(slot != null && slot.getHasStack()){
			ItemStack itemStack1 = slot.getStack();
			itemstack = itemStack1.copy();

			int containerSlots = inventorySlots.size() - player.inventory.mainInventory.size();

			if(index < containerSlots){
				if(!this.mergeItemStack(itemStack1, containerSlots, inventorySlots.size(), true)){
					return ItemStack.EMPTY;
				}
			}else if(!this.mergeItemStack(itemStack1, 0, containerSlots, false)){
				return ItemStack.EMPTY;
			}

			if(itemStack1.getCount() == 0){
				slot.putStack(ItemStack.EMPTY);
			}else{
				slot.onSlotChanged();
			}

			if(itemStack1.getCount() == itemstack.getCount()){
				return ItemStack.EMPTY;
			}
			slot.onTake(player, itemStack1);
		}
		return itemstack;
	}
}
