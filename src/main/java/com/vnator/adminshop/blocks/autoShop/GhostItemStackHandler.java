package com.vnator.adminshop.blocks.autoShop;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

/**
 * ItemStackHandler that only stores ghost copies of items, cannot insert or extract actual items
 */
public class GhostItemStackHandler extends ItemStackHandler {

	public GhostItemStackHandler(int size){
		super(size);
	}

	public int getSlotLimit(int slot) {
		return 1;
	}

	public void setStackInSlot(int slot, @Nonnull ItemStack item){
		insertItem(slot, item, false);
	}

	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if(stack.isEmpty()){
			return ItemStack.EMPTY;
		}

		//Determine if item to insert is valid


		validateSlotIndex(slot);
		ItemStack ghostStack = stack.copy();
		ghostStack.setCount(1);
		this.stacks.set(slot, ghostStack);
		return stack;
	}

	@Nonnull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		this.validateSlotIndex(slot);
		this.stacks.set(slot, ItemStack.EMPTY);
		return ItemStack.EMPTY;
	}
}
