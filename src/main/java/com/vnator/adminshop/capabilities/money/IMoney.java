package com.vnator.adminshop.capabilities.money;

public interface IMoney {

	public boolean deposit(float money);
	public boolean withdraw(float money);
	public boolean canPerformWithdraw(float money);
	public float getMoney();
	public String getFormattedMoney();
	public void setMoney(float money);

}
