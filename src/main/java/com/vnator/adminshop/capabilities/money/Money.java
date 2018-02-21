package com.vnator.adminshop.capabilities.money;

public class Money implements IMoney {

	private float money;
	private String moneyFormatted;

	public Money(){
		money = 100f;
		moneyFormatted = "$100.00";
	}

	/**
	 * Formats the private field moneyFormatted based on new value of money
	 */
	private void formatMoney(){
		if(money < 1000000)
			moneyFormatted = "$"+String.format("%.2f", money);
		else if(money < 1000000000)
			moneyFormatted = "$"+money/1000000+" M"; //Here comes the money, baby
		else
			moneyFormatted = "$"+money/1000000000+" B"; //I'm a Billionaire, Baybee!!!
	}

	/**
	 * Deposit money into account. Must be >= 0
	 * @param amt The amount of money to deposit into the account
	 * @return Success of operation. Will return false only if amt < 0
	 */
	@Override
	public boolean deposit(float amt) {
		if(amt < 0){
			System.err.println("Error: Attempting to deposit value < 0!");
			return false;
		}
		money += amt;
		formatMoney();
		return true;
	}

	/**
	 * Withdraw money from an account
	 * @param amt How much money to withdraw
	 * @return Success of operation. Will use "canPerformWithdraw()" to determine capability
	 */
	@Override
	public boolean withdraw(float amt) {
		if(canPerformWithdraw(amt)) {
			System.out.println("\n\nWithdrawing Money!!!\nAt $"+money+"\n\n");
			money -= amt;
			formatMoney();
			return true;
		}
		return false;
	}

	/**
	 * Determines whether a particular withdraw operation is possible
	 * @param amt How much money is to be withdrawn
	 * @return True if there is enough money in the account to perform the transaction and amt is >= 0. False otherwise
	 */
	@Override
	public boolean canPerformWithdraw(float amt) {
		return money >= amt && amt >= 0;
	}

	/**
	 *
	 * @return How much money is stored in this account
	 */
	@Override
	public float getMoney() {
		return money;
	}

	/**
	 *
	 * @return Stored money as a formatted String
	 */
	@Override
	public String getFormattedMoney(){return moneyFormatted;}

	/**
	 * Set the money in the account. Used only with loading from NBT
	 * @param amt How much money to set the account balance at
	 */
	@Override
	public void setMoney(float amt){
		money = amt;
		formatMoney();
	}
}
