package com.vnator.adminshop.capabilities;

import com.vnator.adminshop.capabilities.ledger.ILedger;
import com.vnator.adminshop.capabilities.ledger.LedgerProvider;
import com.vnator.adminshop.capabilities.money.MoneyProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;

/**
 * Acts as an entry point to access and modify a player's balance
 */
public class BalanceAdapter{

	public static float getMoneyServer(EntityPlayerMP player){
		return player.world.getCapability(LedgerProvider.LEDGER_CAPABILITY, null).getMoney(player.getName());
	}

	public static float getMoneyClient(EntityPlayer player){
		return player.getCapability(MoneyProvider.MONEY_CAPABILITY, null).getMoney();
	}

	public static boolean deposit(EntityPlayer player, float amount){
		return player.world.getCapability(LedgerProvider.LEDGER_CAPABILITY, null).deposit(player.getName(), amount);
	}

	public static boolean deposit(World world, String player, float amount){
		return world.getCapability(LedgerProvider.LEDGER_CAPABILITY, null).deposit(player, amount);
	}

	public static boolean withdraw(EntityPlayer player, float amount){
		return player.world.getCapability(LedgerProvider.LEDGER_CAPABILITY, null).withdraw(player.getName(), amount);
	}

	public static boolean withdraw(World world, String player, float amount){
		return world.getCapability(LedgerProvider.LEDGER_CAPABILITY, null).withdraw(player, amount);
	}
}
