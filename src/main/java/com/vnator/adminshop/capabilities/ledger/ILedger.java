package com.vnator.adminshop.capabilities.ledger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;

public interface ILedger {

	//Capabilities shared with IMoney
	public boolean deposit(String username, float amount);
	public boolean withdraw(String username, float amount);
	public boolean canPerformWithdraw(String username, float money);
	public float getMoney(String username);
	public void setMoney(String username, float money);

	//New capabilities
	public void addPlayer(String username);
	public HashMap<String, Float> getMap();
	public void loadFromNBT(NBTTagCompound tag);

}
