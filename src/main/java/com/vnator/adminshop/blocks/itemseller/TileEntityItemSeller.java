package com.vnator.adminshop.blocks.itemseller;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.blocks.shop.ShopStock;
import com.vnator.adminshop.capabilities.BalanceAdapter;
import com.vnator.adminshop.capabilities.ledger.LedgerProvider;
import com.vnator.adminshop.capabilities.money.MoneyProvider;
import com.vnator.adminshop.packets.PacketHandler;
import com.vnator.adminshop.packets.PacketUpdateMoney;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.UUID;

public class TileEntityItemSeller extends TileEntity implements ITickable {

	private String player;

	FluidTank tank = new FluidTank(16000);
	private ItemStackHandler inventory = new ItemStackHandler(1){
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			String id = stack.getItem().getRegistryName() + ":" + stack.getMetadata();
			if(ShopStock.sellItemMap.containsKey(id))
				return super.insertItem(slot, stack, simulate);
			else
				return stack;
		}
	};

	@Override
	public void update() {
		if(!world.isRemote && player != null && !inventory.getStackInSlot(0).isEmpty()){
			//Check if player is accessible

			//Sell the item
			ItemStack item = inventory.getStackInSlot(0);
			String name = item.getItem().getRegistryName() + ":" + item.getMetadata();
			float money = ShopStock.sellItemMap.get(name)*item.getCount();
			BalanceAdapter.deposit(world, player, money);
			//world.getCapability(LedgerProvider.LEDGER_CAPABILITY, null).deposit(player, money);
			inventory.setStackInSlot(0, ItemStack.EMPTY);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound){
		compound.setTag("inventory", inventory.serializeNBT());
		compound.setString("player", player.toString());
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound){
		inventory.deserializeNBT(compound.getCompoundTag("inventory"));
		player = compound.getString("player");
		if(player == null || player.equals("")){
			player = null;
		}
		//System.out.println("Reading NBT for ItemSeller! player name string = "+compound.getString("player"));
		//if(world.getPlayerEntityByUUID(player) == null)
		//	player = null;
		super.readFromNBT(compound);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ||
				capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ||
				super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T)inventory;
		else if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return (T)tank;
		else
			return super.getCapability(capability, facing);
	}

	public void setPlayer(String player){
		this.player = player;
	}

	public String getPlayer(){
		return player;
	}
}
