package com.vnator.adminshop.blocks.atm;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerATM extends Container{
	public ContainerATM(InventoryPlayer playerInv, final TileEntityATM atm){
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
		return ItemStack.EMPTY;
	}
}
