package com.vnator.adminshop.blocks.atm;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.ModBlocks;
import com.vnator.adminshop.capabilities.BalanceAdapter;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class GuiATM extends GuiContainer {

	private static final ResourceLocation BG = new ResourceLocation(AdminShop.MODID, "textures/gui/atm.png");
	private EntityPlayer player;

	private GuiTextField moneyField;

	public GuiATM(Container container, EntityPlayer player) {
		super(container);
		xSize = 176; ySize = 166;
		this.player = player;
	}

	@Override
	public void initGui(){
		super.initGui();
		moneyField = new GuiTextField(0, fontRenderer, 32+guiLeft, 50+guiTop, 112, 18){
			public boolean textboxKeyTyped(char typedChar, int keyCode){
				if(typedChar >= '0' && typedChar <= '9' || (typedChar == '.' && !this.getText().contains(".")) ||
						typedChar == 8 || typedChar == 127) //delete and backspace
					return super.textboxKeyTyped(typedChar, keyCode);
				else
					return false;
			}
		};

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1, 1, 1, 1);
		mc.getTextureManager().bindTexture(BG);
		int x = (width-xSize) / 2;
		int y = (height-ySize) / 2;
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		moneyField.drawTextBox();
	}

	@Override
	protected  void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		//Draw Shop's Name
		String name = I18n.format(ModBlocks.shop.getUnlocalizedName()+".name");
		fontRenderer.drawString(name, xSize/2 - fontRenderer.getStringWidth(name)/2, 6, 0x404040);

		//Draw player inventory name
		fontRenderer.drawString(player.inventory.getDisplayName().getUnformattedText(), 8, ySize-94, 0x404040);

		//Draw balance
		float money = BalanceAdapter.getMoneyClient(player);
		fontRenderer.drawString("Balance: $"+money, 25, 28, 0x404040);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		moneyField.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException{
		if(moneyField.textboxKeyTyped(typedChar, keyCode))
			return;

		super.keyTyped(typedChar, keyCode);
	}
}
