package com.vnator.adminshop.blocks.atm;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.ModBlocks;
import com.vnator.adminshop.capabilities.BalanceAdapter;
import com.vnator.adminshop.packets.PacketHandler;
import com.vnator.adminshop.packets.PacketUpdateMoney;
import com.vnator.adminshop.packets.PacketWithdrawMoney;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class GuiATM extends GuiContainer {

	private static final ResourceLocation BG = new ResourceLocation(AdminShop.MODID, "textures/gui/atm.png");
	private EntityPlayer player;

	private GuiTextField moneyField;
	private GuiButton withdrawButton;

	public GuiATM(Container container, EntityPlayer player) {
		super(container);
		xSize = 176; ySize = 166;
		this.player = player;
	}

	@Override
	public void initGui(){
		super.initGui();
		moneyField = new GuiTextField(0, fontRenderer, 32+guiLeft, 48+guiTop, 112, 18){
			public boolean textboxKeyTyped(char typedChar, int keyCode){
				if(typedChar >= '0' && typedChar <= '9' || (typedChar == '.' && !this.getText().contains(".")) ||
						typedChar == 8 || typedChar == 127) //delete and backspace
					return super.textboxKeyTyped(typedChar, keyCode);
				else
					return false;
			}
		};

		withdrawButton = new GuiButton(0, guiLeft+148, guiTop+47, ""){
			public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks){
				if(!this.visible)
					return;

				GlStateManager.color(1, 1, 1, 1);
				mc.getTextureManager().bindTexture(BG);

				if(mousePressed(mc, mouseX, mouseY)){
					drawTexturedModalRect(x, y, 176, 52, 20, 20);
				}else if(isMouseOver()){
					drawTexturedModalRect(x, y, 176, 32, 20, 20);
				}else{
					drawTexturedModalRect(x, y, 176, 12, 20, 20);
				}
			}
		};
		withdrawButton.width = 20;
		withdrawButton.height = 20;
		addButton(withdrawButton);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if(button.id == withdrawButton.id){
			System.out.println("Pressed withdraw button!");
			//Perform withdraw
			float moneyText = 0;
			try {
				moneyText = Float.parseFloat(moneyField.getText());
			}catch (NumberFormatException e){
				AdminShop.logger.log(Level.ERROR, "Value in GuiATM's text field can't be parsed into a float!");
			}
			if(moneyText == 0)
				return;

			PacketHandler.INSTANCE.sendToServer(new PacketWithdrawMoney(moneyText));
		}
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

		//Check if refresh button was pressed
		if(mouseX > 8 && mouseX < 20 && mouseY > 24 && mouseY < 36){

		}
		//Check if withdraw button was pressed
		/*
		if(mouseX > guiLeft+148 && mouseX < guiLeft+168 && mouseY > guiTop+47 && mouseY < guiTop+67){
			System.out.println("Pressed withdraw button!");
			//Perform withdraw
			float moneyText = 0;
			try {
				moneyText = Float.parseFloat(moneyField.getText());
			}catch (NumberFormatException e){
				AdminShop.logger.log(Level.ERROR, "Value in GuiATM's text field can't be parsed into a float!");
			}
			if(moneyText == 0)
				return;

			PacketHandler.INSTANCE.sendToServer(new PacketWithdrawMoney(moneyText));
		}
		*/
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException{
		if(moneyField.textboxKeyTyped(typedChar, keyCode))
			return;

		super.keyTyped(typedChar, keyCode);
	}
}
