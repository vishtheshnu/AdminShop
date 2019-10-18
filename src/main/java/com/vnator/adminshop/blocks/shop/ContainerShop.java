package com.vnator.adminshop.blocks.shop;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ContainerShop extends Container {

	public ContainerShop(InventoryPlayer playerInv){
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 9; j++){
				addSlotToContainer(new Slot(playerInv, j + i*9 + 9, 16+j*18, 140+i*18));
			}
		}

		for(int k = 0; k < 9; k++){
			addSlotToContainer(new Slot(playerInv, k, 16+k*18, 198));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index){
		return ItemStack.EMPTY;
	}
}
