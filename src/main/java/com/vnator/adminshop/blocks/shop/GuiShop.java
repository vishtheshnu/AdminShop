package com.vnator.adminshop.blocks.shop;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.ModBlocks;
import com.vnator.adminshop.capabilities.money.IMoney;
import com.vnator.adminshop.capabilities.money.MoneyProvider;
import com.vnator.adminshop.packets.PacketHandler;
import com.vnator.adminshop.packets.PacketSendShopTransaction;
import com.vnator.adminshop.util.GuiButtonShop;
import com.vnator.adminshop.util.GuiButtonTab;
import com.vnator.adminshop.util.TabButtonBuilder;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;

public class GuiShop extends GuiContainer {

	private static final ResourceLocation BG_TEXT = new ResourceLocation(AdminShop.MODID, "textures/gui/shopback.png");
	private static final int BUY_BUTTON_ID = 0;
	private static final int SELL_BUTTON_ID = 1;
	private static final int NUM_ROWS = 6, NUM_COLS = 6;
	public static final int SHOP_BUTTONS_PER_PAGE = 36;

	private InventoryPlayer playerInv;
	private EntityPlayer shopUser;

	private GuiBSButtonHandler buyButtonHandler;
	private GuiBSButtonHandler sellButtonHandler;
	private ArrayList<GuiButtonShop[]> buyButtons;
	private ArrayList<GuiButtonShop[]> sellButtons;
	private GuiButtonTab [] catBuyButtons;
	private GuiButtonTab [] catSellButtons;

	private int buyCat, sellCat;
	private boolean buyMode;

	private int buttonCounter;

	public GuiShop(Container container, EntityPlayer player) {
		super(container);
		xSize = 195; ySize = 222;
		shopUser = player;
		this.playerInv = player.inventory;

		buyCat = 0;
		sellCat = 0;
		buyMode = true;
	}

	@Override
	public void initGui(){
		super.initGui();

		int x = (width-xSize) / 2;
		int y = (height-ySize) / 2;

		buttonCounter = 0;

		//Create Buy and Sell Buttons
		GuiButtonTab buyTab = TabButtonBuilder.createBuySellButton(x+8, y+4, "Buy", true);//new GuiButtonTab(BUY_BUTTON_ID, x+8, y+4, "Buy", true, bsgroup);
		buyTab.selectButton();
		buttonList.add(buyTab);
		GuiButtonTab sellTab = TabButtonBuilder.createBuySellButton(x+33, y+4, "Sell", false);//new GuiButtonTab(SELL_BUTTON_ID, x+33, y+4, "Sell", true, bsgroup);
		buttonList.add(sellTab);

		buttonCounter = 2;

		//Create Category Tab Buttons
		createCategoryButtons(x, y);

		//Create buy and sell buttons
		createBuyButtons(x, y);
		createSellButtons(x, y);

		//buttonList.add(new GuiButtonShop(0, x+62,  y+18, "minecraft:coal:1", 5, true, itemRender));
		//buttonList.add(new GuiButton(0, x+9, y+18, "Test Button!"));
		//(new GuiButton(0, 2, 0, 1, 1, "")).enabled = false;
		buyButtonHandler.setVisible(true);
	}

	/**
	 * Create category buttons for buy and sell tabs
	 */
	private void createCategoryButtons(int x, int y){
		catBuyButtons = new GuiButtonTab[Math.min(ShopStock.buyStock.size(), ShopStock.buyCategories.length)];
		for(int i = 0; i < catBuyButtons.length; i++){
			catBuyButtons[i] = TabButtonBuilder.createCategoryButton(x+9, y+17+18*i, ShopStock.buyCategories[i], i);//new GuiButtonTab(buttonCounter+i, 9, 17+18*i, ShopStock.buyCategories[i], false, buygroup);
			buttonList.add(catBuyButtons[i]);
		}
		if(catBuyButtons.length > 0) catBuyButtons[0].selectButton();
		buttonCounter += catBuyButtons.length;

		catSellButtons = new GuiButtonTab[Math.min(ShopStock.sellStock.size(), ShopStock.sellCategories.length)];
		TabButtonBuilder.createNewGroup();
		for(int i = 0; i < catSellButtons.length; i++){
			catSellButtons[i] = TabButtonBuilder.createCategoryButton(x+9, y+17+18*i, ShopStock.sellCategories[i], i);//new GuiButtonTab(buttonCounter+i, 9, 17+18*i, ShopStock.sellCategories[i], false, sellgroup);
			catSellButtons[i].enabled = false;
			catSellButtons[i].visible = false;
			buttonList.add(catSellButtons[i]);
		}
		if(catSellButtons.length > 0) catSellButtons[0].selectButton();
		buttonCounter += catSellButtons.length;
	}

	/**
	 * Create shop and category buttons for the buy tab.
	 */
	private void createBuyButtons(int x, int y){
		boolean errorFlag = false;
		buyButtonHandler = new GuiBSButtonHandler();
		buyButtons = new ArrayList<GuiButtonShop[]>();
		System.out.println("BuyStock Size: "+ShopStock.buyStock.size());
		for(int i = 0; i < ShopStock.buyStock.size(); i++){
			buyButtonHandler.addCategory(ShopStock.buyStock.get(i).size());
			buyButtons.add(new GuiButtonShop[ShopStock.buyStock.get(i).size()]);
			System.out.println("BuyStock index "+i+" Size: "+ShopStock.buyStock.get(i).size());
			for(int j = 0; j < ShopStock.buyStock.get(i).size(); j++){
				/*
				ItemStack buySample = ShopStock.buyItems.get(i)[j];
				if(buySample == null){
					AdminShop.logger.log(Level.ERROR, "Shop item null! Buy category "+i+", item index "+j+
							"name: "+ ShopStock.buyItems.get(i)[j].toString());
					shopUser.sendMessage(new TextComponentString("Error with item: \""+ShopStock.buyItems.get(i)[j].toString()+
							"\" in buy category "+i+", item index "+j));
					errorFlag = true;
					continue;
				}
				*/
				ShopItem sample = ShopStock.buyStock.get(i).get(j);
				System.out.println("createBuyButtons: "+i+" , "+j+"; "+sample.toString());
				GuiButtonShop but = new GuiButtonShop(buttonCounter, x+62+18*(j%NUM_COLS),
						y+18+18*((j/NUM_COLS)%NUM_ROWS),
						sample, sample.getPrice(), true, itemRender);
				buttonList.add(but);
				but.category = (short)i;
				but.index = j;
				buyButtonHandler.addButton(but);

				buyButtons.get(i)[j] = but;
				//Keep enabled if buy, category 1
				if(i > 0) {
					but.visible = false;
					but.enabled = false;
				}
				buttonCounter++;
			}
		}

		if(errorFlag) {
			String errorStr = I18n.format("error.buy.storeFormat");
			AdminShop.logger.log(Level.ERROR, errorStr);
			shopUser.sendMessage(new TextComponentString(errorStr));
		}
	}

	/**
	 * Create shop and category buttons for the sell tab
	 */
	private void createSellButtons(int x, int y){
		boolean errorFlag = false;
		sellButtonHandler = new GuiBSButtonHandler();
		sellButtons = new ArrayList<GuiButtonShop[]>();
		buttonCounter = 0;
		for(int i = 0; i < ShopStock.sellStock.size(); i++){
			sellButtonHandler.addCategory(ShopStock.sellStock.get(i).size());
			sellButtons.add(new GuiButtonShop[ShopStock.sellStock.get(i).size()]);
			for(int j = 0; j < ShopStock.sellStock.get(i).size(); j++){
				/*
				//Get the item to be sold
				ItemStack sellSample = null;
				//Check if it's an oredict entry
				if(ShopStock.sellItems.get(i)[j].isOreDict()){
					NonNullList<ItemStack> odList = OreDictionary.getOres(ShopStock.sellItems.get(i)[j].getOreName());
					if(odList.size() > 0){ //In case user inputted nonsense value
						sellSample = odList.get(0);
					}
				}
				else { //It's an item, not an oredict entry
					sellSample = ShopStock.sellItems.get(i)[j].getItem();
				}

				if(sellSample == null) {
					AdminShop.logger.log(Level.ERROR, "Shop item null! Sell category " + i + ", item index " + j +
							", name: " + ShopStock.sellItems.get(i)[j].toString());
					shopUser.sendMessage(new TextComponentString("Error with item: \""+ShopStock.sellItems.get(i)[j].toString()+
					"\" in sell category "+i+", item index "+j));
					errorFlag = true;
					continue;
				}
				*/

				ShopItem sample = ShopStock.sellStock.get(i).get(j);
				GuiButtonShop but = new GuiButtonShop(buttonCounter, x+62+18*(j%NUM_COLS),
						y+18+18*((j/NUM_COLS)%NUM_ROWS),
						sample, sample.getPrice(), false, itemRender);

				buttonList.add(but);
				but.category = (short)i;
				but.index = j;
				sellButtonHandler.addButton(but);

				sellButtons.get(i)[j] = but;
				buttonCounter++;
			}
		}

		if(errorFlag){
			String errorStr = I18n.format("error.sell.storeFormat");
			AdminShop.logger.log(Level.ERROR, errorStr);
			shopUser.sendMessage(new TextComponentString(errorStr));
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1, 1, 1, 1);
		mc.getTextureManager().bindTexture(BG_TEXT);
		int x = (width-xSize) / 2;
		int y = (height-ySize) / 2;
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		//Item.REGISTRY.getObject(new ResourceLocation())
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		//Draw Shop's Name
		String name = I18n.format(ModBlocks.shop.getUnlocalizedName()+".name");
		fontRenderer.drawString(name, xSize/2 - fontRenderer.getStringWidth(name)/2, 6, 0x404040);

		//Draw player inventory name
		fontRenderer.drawString(playerInv.getDisplayName().getUnformattedText(), 16, ySize-94, 0x404040);

		//Draw money remaining
		IMoney imon = shopUser.getCapability(MoneyProvider.MONEY_CAPABILITY, null);
		String money = imon.getFormattedMoney();
		fontRenderer.drawString(money, 178-fontRenderer.getStringWidth(money), ySize-94, 0x404040);

		//Draw tooltips for hovered button
		GuiBSButtonHandler hand = buyMode ? buyButtonHandler : sellButtonHandler;
		GuiButtonShop but = hand.isMouseOn(mouseX, mouseY);
		if(but != null)
			drawHoveringText(but.getTooltipStrings(shopUser), mouseX-(width-xSize)/2, mouseY-(height-ySize)/2);
		/*
		GuiButtonShop[] myarr = buyMode ? buyButtons.get(buyCat) : sellButtons.get(sellCat);
		for(GuiButtonShop but : myarr){
			//Check if mouse is over button
			if(but.x < mouseX && but.x+but.width > mouseX && but.y < mouseY && but.y+but.height > mouseY){
				drawHoveringText(but.getTooltipStrings(shopUser), mouseX-(width-xSize)/2, mouseY-(height-ySize)/2);
			}
		}
		*/
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws java.io.IOException{
		super.mouseClicked(mouseX, mouseY, mouseButton);

		int x = (width-xSize) / 2;
		int y = (height-ySize) / 2;
		//Scroll up and down buttons
		System.out.println("mouse location: "+(mouseX-x)+" , "+(mouseY-y));
		if(mouseX > 170+x && mouseX < 186+x){
			GuiBSButtonHandler handler = buyMode ? buyButtonHandler : sellButtonHandler;
			if(mouseY > 18+y && mouseY < 34+y){
				System.out.println("Scrolling up!");
				if(handler.canScroll(true)){
					handler.scroll(true);
				}
			}else if(mouseY > 108+y && mouseY < 124+y){
				System.out.println("Scrolling down!");
				if(handler.canScroll(false)){
					handler.scroll(false);
				}
			}
		}


	}

	@Override
	protected void actionPerformed(GuiButton button){
		if(button instanceof GuiButtonShop){
			AdminShop.logger.log(Level.DEBUG, "GuiShopButton pressed!");
			GuiButtonShop shopbutton = (GuiButtonShop) button;
			PacketHandler.INSTANCE.sendToServer(new PacketSendShopTransaction(
					shopbutton.category, shopbutton.index, shopbutton.getQuantity(), buyMode
			));
		}else if(button instanceof GuiButtonTab){
			GuiButtonTab tabbutton = (GuiButtonTab) button;
			tabbutton.selectButton();
			if(tabbutton.isBSButton())
				switchBSTab(tabbutton);
			else if(tabbutton.isCategoryButton()){
				switchCatTab(tabbutton);
			}
		}
	}

	private void switchBSTab(GuiButtonTab tabbutton){
		if(tabbutton.isBuy()){
			buyMode = true;
			sellButtonHandler.setVisible(false);
			buyButtonHandler.setVisible(true);
			/*
			//Enable currently selected buy button tab
			for(int i = 0; i < sellButtons.get(sellCat).length; i++){
				sellButtons.get(sellCat)[i].enabled = false;
				sellButtons.get(sellCat)[i].visible = false;
			}
			//Disable previously selected sell buttons
			for(int i = 0; i < buyButtons.get(buyCat).length; i++){
				buyButtons.get(buyCat)[i].enabled = true;
				buyButtons.get(buyCat)[i].visible = true;
			}
			*/
			//Disable previous category buttons
			for(GuiButtonTab but : catSellButtons){
				but.enabled = false;
				but.visible = false;
			}
			//Enable current category buttons
			for(GuiButtonTab but : catBuyButtons){
				but.enabled = true;
				but.visible = true;
			}
		}else if(tabbutton.isSell()){
			buyMode = false;

			buyButtonHandler.setVisible(false);
			sellButtonHandler.setVisible(true);
			/*
			//Disable previously selected shop buttons
			for(int i = 0; i < buyButtons.get(buyCat).length; i++){
				buyButtons.get(buyCat)[i].enabled = false;
				buyButtons.get(buyCat)[i].visible = false;
			}
			//Enable currently selected sell button tab
			for(int i = 0; i < sellButtons.get(sellCat).length; i++){
				sellButtons.get(sellCat)[i].enabled = true;
				sellButtons.get(sellCat)[i].visible = true;
			}
			*/
			//Enable currently selected category buttons
			for(GuiButtonTab but : catSellButtons){
				but.enabled = true;
				but.visible = true;
			}
			//Disable previously selected category buttons
			for(GuiButtonTab but : catBuyButtons){
				but.enabled = false;
				but.visible = false;
			}
		}
	}

	private void switchCatTab(GuiButtonTab tabbutton){
		GuiButtonShop [] newbuttons = null;
		GuiButtonShop [] oldbuttons = null;
		if(buyMode){ //Get buttons from buy button list
			buyButtonHandler.setVisible(false);
			buyButtonHandler.setCategory(tabbutton.getCategory());
			buyButtonHandler.setVisible(true);
			/*
			newbuttons = buyButtons.get(tabbutton.getCategory());
			oldbuttons = buyButtons.get(buyCat);
			buyCat = tabbutton.getCategory();
			*/
		}else{
			sellButtonHandler.setVisible(false);
			sellButtonHandler.setCategory(tabbutton.getCategory());
			sellButtonHandler.setVisible(true);
			/*
			newbuttons = sellButtons.get(tabbutton.getCategory());
			oldbuttons = sellButtons.get(sellCat);
			sellCat = tabbutton.getCategory();
			*/
		}

		/*
		for(GuiButtonShop but : oldbuttons){
			but.visible = false;
			but.enabled = false;
		}
		for(GuiButtonShop but : newbuttons){
			but.visible = true;
			but.enabled = true;
		}
		*/

	}

	/*
	private void buyItem(GuiButtonShop shopbutton){
		IItemHandler inventory = shopUser.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		ItemStack tobuy = shopbutton.getItemStack();
		ItemStack returned = ItemHandlerHelper.insertItemStacked(inventory, tobuy, true);
		float price = shopbutton.getPrice(tobuy.getCount() - returned.getCount());
		boolean success = shopUser.getCapability(MoneyProvider.MONEY_CAPABILITY, null).withdraw(price);
		if(success){
			//Send money into player inventory
			ItemHandlerHelper.giveItemToPlayer(shopUser, tobuy);
		}else{
			AdminShop.logger.log(Level.ERROR, "Not enough money to perform transaction!");
		}
	}
	*/
}
