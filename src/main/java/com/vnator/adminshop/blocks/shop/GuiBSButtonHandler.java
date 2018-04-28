package com.vnator.adminshop.blocks.shop;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.utils.GuiButtonShop;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;
import scala.Int;

import java.util.ArrayList;

/**
 * Manages the buy and sell buttons for GuiShop, as well as relevant methods
 */
@SideOnly(Side.CLIENT)
public class GuiBSButtonHandler {

	private ArrayList<GuiButtonShop[]> buttons;
	private int category;
	private ArrayList<Integer> buttonScroll;

	private int nextSpace;

	public GuiBSButtonHandler(){
		buttons = new ArrayList<GuiButtonShop[]>();
		category = 0;
		buttonScroll = new ArrayList<Integer>();

		nextSpace = 0;
	}

	//ArrayList modification methods

	public void addCategory(int size){
		buttons.add(new GuiButtonShop[size]);
		buttonScroll.add(0);
		nextSpace = 0;
	}

	/**
	 * Adds the parameter button to the end of the latest created category.
	 * Also sets the button to be disabled and invisible by default
	 * @param button
	 */
	public void addButton(GuiButtonShop button){
		if(nextSpace == -1) {
			AdminShop.logger.log(Level.ERROR,
					"Trying to add a buy/sell button to a full category. this shouldn't be getting called");
			return;
		}
		buttons.get(buttons.size()-1)[nextSpace] = button;
		button.enabled = false;
		button.visible = false;
		nextSpace++;
		if(nextSpace > buttons.get(buttons.size()-1).length)
			nextSpace = -1;
	}

	public void setCategory(int cat){
		category = cat;
	}

	/**
	 * Attempts to "scroll" through the current category.
	 * @param up Whether it's scrolling up (true) or down (false)
	 */
	public void scroll(boolean up){
		int nscroll = buttonScroll.get(category) + (up ? -1 : 1);
		if(!canScroll(up)){
			return; //Do nothing if out of bounds
		}else{
			setVisible(false);
			buttonScroll.set(category, nscroll);
			setVisible(true);
		}
	}

	/**
	 * Returns whether or not the current menu can be scrolled in the specified direction
	 * @param up Whether the scroll is up or not
	 * @return Whether it's possible to scroll
	 */
	public boolean canScroll(boolean up){
		int nscroll = buttonScroll.get(category) + (up ? -1 : 1);
		System.out.println(nscroll);
		if(nscroll < 0 || nscroll*GuiShop.SHOP_BUTTONS_PER_PAGE >= buttons.get(category).length)
			return false;
		return true;
	}

	//Mass button access methods

	/**
	 * Returns the active button the mouse is over, if any at all
	 * @param mouseX x coordinate of the mouse
	 * @param mouseY y coordinate of the mouse
	 * @return Held GuiShopButton that the mouse is over. Null if it isn't over anything
	 */
	public GuiButtonShop isMouseOn(int mouseX, int mouseY){
		for(int i = 0; i < GuiShop.SHOP_BUTTONS_PER_PAGE; i++){
			int index = getButtonIndex(i);
			if(index >= buttons.get(category).length)
				break;
			GuiButtonShop but = buttons.get(category)[index];
			if(but.x < mouseX && but.x+but.width > mouseX && but.y < mouseY && but.y+but.height > mouseY){
				return but;
			}
		}
		return null;
	}

	/**
	 * Called to fully enable or disable the buttons in this object.
	 * Calling with true will only enable buttons of the previously set category
	 * @param visible What to set the buttons to. True = enabled, False = disabled
	 */
	public void setVisible(boolean visible){
		for(int i = 0; i < GuiShop.SHOP_BUTTONS_PER_PAGE; i++){
			int index = getButtonIndex(i);
			if(index >= buttons.get(category).length)
				break;
			buttons.get(category)[index].visible = visible;
			buttons.get(category)[index].enabled = visible;
		}
	}

	/**
	 * Returns the index of the ith button currently displayed. Selected category
	 * and number of scrolls affects this.
	 * @param i The ith button currently displayed by this handler whose index is to be returned
	 * @return The index of the ith button currently displayed
	 */
	private int getButtonIndex(int i){
		return buttonScroll.get(category)*GuiShop.SHOP_BUTTONS_PER_PAGE+i;
	}
}
