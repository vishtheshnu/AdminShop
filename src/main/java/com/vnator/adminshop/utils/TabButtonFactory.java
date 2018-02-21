package com.vnator.adminshop.utils;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Factory class that generates GuiButtonTab instances depending on chosen parameters
 */
@SideOnly(Side.CLIENT)
public class TabButtonFactory {

	public static final int TYPE_BUYSELL = 0;
	public static final int TYPE_CATEGORY = 1;

	private static GuiButtonTab.ButtonGroup setGroup = null;
	private static int id;
	private static int prevCategory;

	private static GuiButtonTab getInstance(int type, int x, int y, String text){
		if(type != prevCategory){
			prevCategory = type;
			createNewGroup();
		}
		GuiButtonTab instance = new GuiButtonTab(getId(), x, y, text, false, getGroup());

		return instance;
	}

	/**
	 * Generating method for Buy and Sell buttons. Same as the generic generator, but contains parameter to specify
	 * whether the Buy or Sell button is being created.
	 * @param x x location to draw the button
	 * @param y y location to draw the button
	 * @param text text to display on the button
	 * @param isBuy true if creating the Buy button, false if creating the Sell button
	 * @return new GuiButtonTab instance
	 */
	public static GuiButtonTab createBuySellButton(int x, int y, String text, boolean isBuy){
		GuiButtonTab instance = getInstance(TYPE_BUYSELL, x, y, text);
		instance.isBSButton = true;
		instance.isBuy = isBuy;
		instance.isSell = !isBuy;
		instance.width = 25;
		instance.height = 12;
		return instance;
	}

	/**
	 * Generating method for Category buttons. Same as generic generator but sets specific fields.
	 * @param x
	 * @param y
	 * @param text
	 * @param catIndex
	 * @return
	 */
	public static GuiButtonTab createCategoryButton(int x, int y, String text, int catIndex){
		GuiButtonTab instance = getInstance(TYPE_CATEGORY, x, y, text);
		instance.isCategoryButton = true;
		instance.category = catIndex;
		instance.width = 50;
		instance.height = 16;
		return instance;
	}

	private static int getId(){
		return ++id;
	}

	private static GuiButtonTab.ButtonGroup getGroup(){
		if(setGroup == null)
			setGroup = new GuiButtonTab.ButtonGroup();
		return setGroup;
	}

	public static void createNewGroup(){
		setGroup = new GuiButtonTab.ButtonGroup();
	}
}
