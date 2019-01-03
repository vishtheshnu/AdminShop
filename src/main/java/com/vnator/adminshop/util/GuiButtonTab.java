package com.vnator.adminshop.util;

import com.vnator.adminshop.AdminShop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class GuiButtonTab extends GuiButton {

	private static final ResourceLocation BACKGROUND = new ResourceLocation(AdminShop.MODID, "textures/gui/shopback.png");
	private static final int TYPE_BUY = 0;
	private static final int TYPE_SELL = 1;

	protected ButtonGroup group;
	protected boolean isSelected;
	protected boolean isBSButton; //to determine which texture to draw
	protected String buttonText;
	protected boolean isBuy;
	protected boolean isSell;

	protected boolean isCategoryButton;
	protected int category;

	public GuiButtonTab(int buttonId, int x, int y, String buttonText, boolean isBSButton, ButtonGroup group) {
		super(buttonId, x, y, buttonText);
		this.buttonText = buttonText;
		isSelected = false;
		this.isBSButton = isBSButton;
		this.group = group;
		group.addMember(this);
	}

	public void selectButton(){
		group.deselectAll();
		isSelected = true;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks){
		if(!visible)
			return;
		GlStateManager.color(1, 1, 1, 1);
		mc.getTextureManager().bindTexture(BACKGROUND);
		if(isBSButton){
			if(isSelected)
				drawTexturedModalRect(x, y, 195, 44, 25, 12);
			else
				drawTexturedModalRect(x, y, 195, 32, 25, 12);

			drawCenteredString(mc.fontRenderer, buttonText, x+12, y+2, 0xFFFFFF);
		}else{
			if(isSelected)
				drawTexturedModalRect(x, y, 195, 16, 50, 16);
			else
				drawTexturedModalRect(x, y, 195, 0, 50, 16);

			drawCenteredString(mc.fontRenderer, buttonText, x+25, y+4, 0xFFFFFF);
		}
	}

	public boolean isBSButton(){
		return isBSButton;
	}

	public boolean isBuy(){
		return isBuy;
	}

	public boolean isSell(){
		return isSell;
	}

	public boolean isCategoryButton(){
		return isCategoryButton;
	}

	public int getCategory(){
		return category;
	}

	public static class ButtonGroup{
		LinkedList<GuiButtonTab> members;

		public ButtonGroup(){
			members = new LinkedList<GuiButtonTab>();
		}

		public ButtonGroup(GuiButtonTab... members){
			this();
			for(GuiButtonTab tab : members)
				this.members.add(tab);
		}

		public void addMember(GuiButtonTab tab){
			members.add(tab);
		}

		public void deselectAll(){
			for(GuiButtonTab tab : members)
				tab.isSelected = false;
		}
	}
}
