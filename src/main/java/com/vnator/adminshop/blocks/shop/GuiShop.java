package com.vnator.adminshop.blocks.shop;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.ConfigHandler;
import com.vnator.adminshop.ModBlocks;
import com.vnator.adminshop.capabilities.money.IMoney;
import com.vnator.adminshop.capabilities.money.MoneyProvider;
import com.vnator.adminshop.packets.PacketHandler;
import com.vnator.adminshop.packets.PacketSendShopTransaction;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;

public class GuiShop extends GuiContainer {

	private static final ResourceLocation BG_TEXT = new ResourceLocation(AdminShop.MODID, "textures/gui/shopback.png");
	private static final int BUY_BUTTON_ID = 0;
	private static final int SELL_BUTTON_ID = 1;

	private InventoryPlayer playerInv;
	private EntityPlayer shopUser;

	private ArrayList<GuiButtonShop[]> buyButtons;
	private ArrayList<GuiButtonShop[]> sellButtons;

	private int buyCat, sellCat;
	private boolean buyMode;

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
		RenderHelper.enableGUIStandardItemLighting();

		int x = (width-xSize) / 2;
		int y = (height-ySize) / 2;

		int buttonCounter = 0;

		GuiButtonTab.ButtonGroup bsgroup = new GuiButtonTab.ButtonGroup();
		GuiButtonTab buyTab = new GuiButtonTab(BUY_BUTTON_ID, x+8, y+4, "Buy", true, bsgroup);
		buyTab.selectButton();
		buttonList.add(buyTab);
		GuiButtonTab sellTab = new GuiButtonTab(SELL_BUTTON_ID, x+33, y+4, "Sell", true, bsgroup);
		buttonList.add(sellTab);

		buttonCounter = 2;

		GuiButtonTab [] catBuyButtons = new GuiButtonTab[ShopStock.buyItems.size()];
		GuiButtonTab.ButtonGroup buygroup = new GuiButtonTab.ButtonGroup();
		for(int i = 0; i < catBuyButtons.length; i++){
			catBuyButtons[i] = new GuiButtonTab(buttonCounter+i, 9, 17+18*i, ShopStock.buyCategories[i],
					false, buygroup);
			buttonList.add(catBuyButtons[i]);
		}
		buttonCounter += catBuyButtons.length;

		GuiButtonTab [] catSellButtons = new GuiButtonTab[ShopStock.sellItems.size()];
		GuiButtonTab.ButtonGroup sellgroup = new GuiButtonTab.ButtonGroup();
		for(int i = 0; i < catSellButtons.length; i++){
			catSellButtons[i] = new GuiButtonTab(buttonCounter+i, 9, 17+18*i, ShopStock.sellCategories[i],
					false, sellgroup);
			buttonList.add(catSellButtons[i]);
		}
		buttonCounter += catSellButtons.length;

		buyButtons = new ArrayList<GuiButtonShop[]>();
		sellButtons = new ArrayList<GuiButtonShop[]>();
		for(int i = 0; i < ShopStock.buyItems.size(); i++){
			buyButtons.add(new GuiButtonShop[ShopStock.buyItems.get(i).length]);
			for(int j = 0; j < ShopStock.buyItems.get(i).length; j++){
				buyButtons.get(i)[j] = new GuiButtonShop(buttonCounter, x+62+18*(j%7),  y+18+18*(j/7),
						ShopStock.buyItems.get(i)[j], ShopStock.buyItemPrices.get(i)[j], true, itemRender);
				buttonList.add(buyButtons.get(i)[j]);
				buyButtons.get(i)[j].category = (short)i;
				buyButtons.get(i)[j].index = j;
				//Keep enabled if buy, category 1
				if(i > 0) {
					buyButtons.get(i)[j].visible = false;
					buyButtons.get(i)[j].enabled = false;
				}
				buttonCounter++;
			}
		}
		buttonCounter = 0;
		for(int i = 0; i < ShopStock.sellItems.size(); i++){
			sellButtons.add(new GuiButtonShop[ShopStock.sellItems.get(i).length]);
			for(int j = 0; j < ShopStock.sellItems.get(i).length; j++){
				sellButtons.get(i)[j] = new GuiButtonShop(buttonCounter, x+62+18*(j%7),  y+18+18*(j/7),
						ShopStock.sellItems.get(i)[j], ShopStock.sellItemPrices.get(i)[j], false, itemRender);
				buttonList.add(sellButtons.get(i)[j]);
				sellButtons.get(i)[j].category = (short)i;
				sellButtons.get(i)[j].index = j;
				sellButtons.get(i)[j].visible = false;
				sellButtons.get(i)[j].enabled = false;
				buttonCounter++;
			}
		}
		//buttonList.add(new GuiButtonShop(0, x+62,  y+18, "minecraft:coal:1", 5, true, itemRender));
		//buttonList.add(new GuiButton(0, x+9, y+18, "Test Button!"));
		//(new GuiButton(0, 2, 0, 1, 1, "")).enabled = false;
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
		money = "$"+imon.getMoney();
		fontRenderer.drawString(money, 178-fontRenderer.getStringWidth(money), ySize-94, 0x404040);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws java.io.IOException{
		super.mouseClicked(mouseX, mouseY, mouseButton);

		//If a category button is clicked, set it
	}

	@Override
	protected void actionPerformed(GuiButton button){
		if(button instanceof GuiButtonShop){
			AdminShop.logger.log(Level.DEBUG, "GuiShopButton pressed!");
			GuiButtonShop shopbutton = (GuiButtonShop) button;
			PacketHandler.INSTANCE.sendToServer(new PacketSendShopTransaction(
					shopbutton.category, shopbutton.index, shopbutton.getItemStack().getCount(), buyMode
			));
		}else if(button instanceof GuiButtonTab){
			GuiButtonTab tabbutton = (GuiButtonTab) button;
			tabbutton.selectButton();
			switchBSTab(tabbutton);
		}
	}

	private void switchBSTab(GuiButtonTab tabbutton){
		if(tabbutton.id == BUY_BUTTON_ID){
			buyMode = true;
			//Enable currently selected buy button tab
			for(int i = 0; i < sellButtons.get(sellCat).length; i++){
				sellButtons.get(sellCat)[i].enabled = false;
				sellButtons.get(sellCat)[i].visible = false;
			}

			for(int i = 0; i < buyButtons.get(buyCat).length; i++){
				buyButtons.get(sellCat)[i].enabled = true;
				buyButtons.get(sellCat)[i].visible = true;
			}
		}else if(tabbutton.id == SELL_BUTTON_ID){
			buyMode = false;
			//Enable currently selected sell button tab
			for(int i = 0; i < buyButtons.get(buyCat).length; i++){
				buyButtons.get(sellCat)[i].enabled = false;
				buyButtons.get(sellCat)[i].visible = false;
			}
			for(int i = 0; i < sellButtons.get(sellCat).length; i++){
				sellButtons.get(sellCat)[i].enabled = true;
				sellButtons.get(sellCat)[i].visible = true;
			}
		}
	}

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
}
