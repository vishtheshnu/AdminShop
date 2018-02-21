package com.vnator.adminshop.blocks.shop;

import com.vnator.adminshop.AdminShop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Level;

public class GuiButtonShop extends GuiButton {

	private ItemStack item; //Amounts of 1, 16, and 64
	public short category;
	public int index;
	private float price;
	private boolean isBuying;
	private RenderItem itemRender;

	/**
	 * A GuiButton that can be clicked to initiate a buy or sell action.
	 * @param buttonId ID of the button
	 * @param x x coordinate to draw the button
	 * @param y y coordinate to draw the button
	 * @param item ItemStack representing the item to be bought/sold
	 * @param price Price to buy/sell item for
	 * @param isBuying Whether the button is to initiate buying or selling. True = buying, False = selling
	 * @param itemRender RenderItem instance from GuiContainer to render item
	 */
	public GuiButtonShop(int buttonId, int x, int y, ItemStack item, float price, boolean isBuying, RenderItem itemRender) {
		super(buttonId, x, y, 16, 16, "");
		this.price = price;
		this.item = item;
		this.isBuying = isBuying;
		this.itemRender = itemRender;

		//Initialize ItemStacks from item
		/*
		String [] itemParts = itemString.split(":");
		if(itemParts.length == 2){
			constructItemStacks(itemString, 0);
		}else if(itemParts.length == 3){
			int meta = 0;
			try {
				meta = Integer.parseInt(itemParts[2]);
			}catch (NumberFormatException e){
				AdminShop.logger.log(Level.ERROR, "Incorrectly formatted item: metadata must be int, not "+itemParts[2]);
			}
			constructItemStacks(itemParts[0]+":"+itemParts[1], meta);
		}else{
			constructItemStacks("", 0);
		}
		*/
	}

	private void constructItemStacks(String itemName, int meta){
		Item baseItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
		item = new ItemStack(baseItem, 1, meta);
	}


	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouoseY, float partialTicks){
		if(!visible)
			return;
		RenderHelper.enableGUIStandardItemLighting();
		itemRender.renderItemAndEffectIntoGUI(item, x, y);
		if(GuiScreen.isShiftKeyDown()){
			drawString(mc.fontRenderer, "64", x+17-mc.fontRenderer.getStringWidth("64"), y+9, 0xFFFFFF);
		}else if(GuiScreen.isCtrlKeyDown()){
			drawString(mc.fontRenderer, "16", x+17-mc.fontRenderer.getStringWidth("16"), y+9, 0xFFFFFF);
		}
	}

	/**
	 * Calculates the total cost/gain of a transaction
	 * @return Amount of money to be exchanged by transaction
	 */
	public float getPrice(int quant){
		return price*quant;
	}

	/**
	 *
	 * @return ItemStack to be added/removed from player inventory
	 */
	public ItemStack getItemStack(){
		int quant = 1;
		if(GuiScreen.isShiftKeyDown()){
			quant = 64;
		}else if(GuiScreen.isCtrlKeyDown()){
			quant = 16;
		}

		System.out.println(item.getItem().getUnlocalizedName()+" : "+item.getMetadata());
		return new ItemStack(item.getItem(), quant, item.getMetadata());
	}

	public boolean isBuying(){
		return isBuying;
	}

}
