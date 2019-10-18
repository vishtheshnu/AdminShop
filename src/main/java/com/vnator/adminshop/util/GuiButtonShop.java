package com.vnator.adminshop.util;

import com.vnator.adminshop.blocks.shop.ShopItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;

public class GuiButtonShop extends GuiButton {

	private ShopItem item;
	private ItemStack displayItem;
	public short category;
	public int index;
	private float price;
	private boolean isBuying;
	private RenderItem itemRender;
	private int color;

	private LinkedList<String> ttl1, ttl16, ttl64;

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
	public GuiButtonShop(int buttonId, int x, int y, ShopItem item, float price, boolean isBuying, RenderItem itemRender) {
		super(buttonId, x, y, 16, 16, "");
		this.price = price;
		this.item = item;
		this.isBuying = isBuying;
		this.itemRender = itemRender;

		String itemName = "";
		if(item.isItem()){
			itemName = item.getItem().getDisplayName();
			displayItem = item.getItem();
		}
		else if (item.isFluid()){
			itemName = item.getFluid().getLocalizedName();
			displayItem = FluidUtil.getFilledBucket(item.getFluid());
			if(displayItem == null || displayItem.isEmpty()){
				color = item.getFluid().getFluid().getColor();
			}
		}
		else{
			itemName = item.getOredict();
			if(OreDictionary.getOres(itemName).size() > 0)
				displayItem = OreDictionary.getOres(itemName).get(0);
		}

		ttl1 = new LinkedList<String>();
		ttl1.add(itemName);
		ttl1.add("$"+getPrice(1));

		ttl16 = new LinkedList<String>();
		ttl16.add(itemName);
		ttl16.add("$"+getPrice(16));

		ttl64 = new LinkedList<String>();
		ttl64.add(itemName);
		ttl64.add("$"+getPrice(64));

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

	/*
	private void constructItemStacks(String itemName, int meta){
		Item baseItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
		item = new ItemStack(baseItem, 1, meta);
	}
	*/

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks){
		if(!visible)
			return;
		RenderHelper.enableGUIStandardItemLighting();
		if(displayItem != null && !displayItem.isEmpty())
			itemRender.renderItemAndEffectIntoGUI(displayItem, x, y);
		else{
			drawRect(x, y, x+width, y+height, color);
		}
		//GlStateManager.scale(0.5, 0.5, 1);
		GL11.glScalef(0.5f, 0.5f, 1);
		drawString(mc.fontRenderer, getQuantity()+"", 2*(x+16)-mc.fontRenderer.getStringWidth(getQuantity()+""),
				2*(y)+24, 0xFFFFFF);

		/*
		if(GuiScreen.isShiftKeyDown()){
			drawString(mc.fontRenderer, "64", x+17-mc.fontRenderer.getStringWidth("64"), y+9, 0xFFFFFF);
		}else if(GuiScreen.isCtrlKeyDown()){
			drawString(mc.fontRenderer, "16", x+17-mc.fontRenderer.getStringWidth("16"), y+9, 0xFFFFFF);
		}
		*/
		if(item.isOredict()){
			drawString(mc.fontRenderer, "OD", 2*x, 2*y, 0xFFC921);
		}else if(item.isFluid()){
			drawString(mc.fontRenderer, "F", 2*x, 2*y, 0x6666FF);
		}
		GL11.glScalef(2, 2, 1);

	}

	/*
	@Override
	public void drawButtonForegroundLayer(int mouseX, int mouseY){

	}
	*/

	public List<String> getTooltipStrings(EntityPlayer player){

		int quant = getQuantity();
		List<String> toret;
		if(displayItem != null && !displayItem.isEmpty())
			toret = displayItem.getTooltip(player, ITooltipFlag.TooltipFlags.NORMAL);
		else if(item.isOredict()){
			toret = new LinkedList<String>();
			toret.add(item.getOredict());
			if(item.getNbt() != null)
				toret.add("With NBT: "+item.getNbt().toString());
		}else{
			toret = new LinkedList<String>();
			toret.add(item.getFluid().getLocalizedName());
		}
		toret.add("");
		toret.add("$"+quant*price);
		return toret;
	}

	/**
	 * Calculates the total cost/gain of a transaction
	 * @return Amount of money to be exchanged by transaction
	 */
	public float getPrice(int quant){
		return price*quant;
	}

	/**
	 * 1-16-64 for items/oredict items, 1-100-1000 for fluids
	 * @return ItemStack to be added/removed from player inventory.
	 */
	public int getQuantity(){
		int quantity = 1;

		if(GuiScreen.isShiftKeyDown()){
			if(item.isItem() || item.isOredict())
				quantity = 64;
			else
				quantity = 1000;
		}else if(GuiScreen.isCtrlKeyDown()){
			if(item.isItem() || item.isOredict())
				quantity = 16;
			else
				quantity = 100;
		}

		return quantity;

		//System.out.println(item.getItem().getUnlocalizedName()+" : "+item.getMetadata());
		//return new ItemStack(item.getItem(), quant, item.getMetadata());
	}

	public boolean isBuying(){
		return isBuying;
	}

}
