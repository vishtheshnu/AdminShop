package com.vnator.adminshop.blocks.atm;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class ContainerATM extends Container{
	public ContainerATM(InventoryPlayer playerInv, final TileEntityATM atm){

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
