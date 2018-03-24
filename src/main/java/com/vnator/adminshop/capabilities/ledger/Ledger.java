package com.vnator.adminshop.capabilities.ledger;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.ConfigHandler;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.logging.Logger;

public class Ledger implements ILedger{

	private HashMap<String, Float> balances; //Maps player username to their balance

	public Ledger(){
		balances = new HashMap<String, Float>();
	}

	@Override
	public boolean deposit(String username, float amount) {
		if(amount < 0){
			AdminShop.logger.log(Level.ERROR, "Trying to deposit value less than 0 into ledger!");
			return false;
		}
		addPlayer(username);
		balances.put(username, balances.get(username)+amount);
		return true;
	}

	@Override
	public boolean withdraw(String username, float amount) {
		if(!canPerformWithdraw(username, amount)){
			return false;
		}
		balances.put(username, balances.get(username)-amount);
		return true;
	}

	@Override
	public boolean canPerformWithdraw(String username, float money) {
		return balances.get(username) >= money;
	}

	@Override
	public float getMoney(String username) {
		if(balances == null || username == null)
			return 0;
		return balances.get(username);
	}

	@Override
	public void setMoney(String username, float money) {
		balances.put(username, money);
	}

	@Override
	public void addPlayer(String username) {
		if(!balances.containsKey(username))
			balances.put(username, ConfigHandler.GENERAL_CONFIGS.startingMoney);
	}

	@Override
	public HashMap<String, Float> getMap() {
		return balances;
	}

	@Override
	public void loadFromNBT(NBTTagCompound tag) {
		balances = new HashMap<String, Float>(tag.getKeySet().size());
		for(String key : tag.getKeySet()){
			balances.put(key, tag.getFloat(key));
		}
	}
}
