package com.vnator.adminshop.blocks.autoShop;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.ModBlocks;
import com.vnator.adminshop.packets.PacketHandler;
import com.vnator.adminshop.packets.PacketUpdateAutoBuyer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class GuiAutoBuyer extends GuiContainer {
	private static final ResourceLocation BG_TEXTURE = new ResourceLocation(AdminShop.MODID, "textures/gui/pedestal.png");

	private ContainerAutoBuyer container;
	private InventoryPlayer playerInv;
	private GuiButton plus, minus, rateButton, isItemButton;
	private int quantity, rate;
	private boolean isItem;
	private BlockPos pos;

	public GuiAutoBuyer(Container container, InventoryPlayer playerInv, TileEntityAutoBuyer buyer) {
		super(container);
		this.playerInv = playerInv;
		this.container = (ContainerAutoBuyer)container;
		quantity = buyer.getQuantity();
		rate = buyer.getRate();
		isItem = buyer.getIsBuyingItem();
		pos = buyer.getPos();
	}

	@Override
	public void initGui(){
		super.initGui();
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;

		plus = new GuiButton(0, x+xSize/2 - 70, y+ySize-104, 20, 20, "+");
		minus = new GuiButton(1, x+xSize/2 + 50, y+ySize-104, 20, 20,"-");
		rateButton = new GuiButton(2, x+10, y+ySize-144, 60, 20, getRateText(rate));
		isItemButton = new GuiButton(3, x+xSize-70, y+ySize-144, 60, 20, getIFText(isItem));
		this.buttonList.add(plus);
		this.buttonList.add(minus);
		this.buttonList.add(rateButton);
		this.buttonList.add(isItemButton);
	}


	@Override
	public void actionPerformed(GuiButton button){
		int change = 1;
		if(GuiScreen.isShiftKeyDown()){
			if(isItem)
				change = 64;
			else
				change = 1000;
		}
		else if(GuiScreen.isCtrlKeyDown()) {
			if (isItem)
				change = 16;
			else
				change = 100;
		}

		int toWrite = 0;
		if(button.id == 0){
			quantity += change;
			toWrite |= 1;
		}else if(button.id == 1){
			quantity -= change;
			if(quantity < 0) quantity = 0;
			toWrite |= 1;
		}else if(button.id == 2){
			rate++;
			rate %= 3;
			rateButton.displayString = getRateText(rate);
			toWrite |= 4;
		}else if(button.id == 3){
			isItem = !isItem;
			isItemButton.displayString = getIFText(isItem);
			toWrite |= 2;
		}
		PacketHandler.INSTANCE.sendToServer(new PacketUpdateAutoBuyer(pos, quantity, isItem, false, toWrite));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float v, int i, int i1) {
		GlStateManager.color(1, 1, 1, 1);
		mc.getTextureManager().bindTexture(BG_TEXTURE);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		ItemStack item = container.getInventory().get(0);
		if(item != null && !item.isEmpty()){
			String name = I18n.format(item.getUnlocalizedName()+".name");
			fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, ySize-114, 0x404040);
		}
		fontRenderer.drawString(quantity+"", xSize/2 - fontRenderer.getStringWidth(quantity+"")/2,
				ySize-94, 0x404040);
		String name = I18n.format(ModBlocks.itemBuyer.getUnlocalizedName() + ".name");
		fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 0x404040);
		//fontRenderer.drawString(playerInv.getDisplayName().getUnformattedText(), 8, ySize - 94, 0x404040);
	}

	private String getRateText(int i){
		switch (i){
			case 0: return I18n.format("rateTick");
			case 1: return I18n.format("rateSec");
			case 2: return I18n.format("rateMin");
			default: return "ayy lmao";
		}
	}

	private String getIFText(boolean isItem){
		if(isItem) return I18n.format("isItem");
		else return I18n.format("isFluid");
	}
}
