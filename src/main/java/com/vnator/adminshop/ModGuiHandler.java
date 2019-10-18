package com.vnator.adminshop;

import com.vnator.adminshop.blocks.atm.ContainerATM;
import com.vnator.adminshop.blocks.atm.GuiATM;
import com.vnator.adminshop.blocks.atm.TileEntityATM;
import com.vnator.adminshop.blocks.autoShop.ContainerAutoBuyer;
import com.vnator.adminshop.blocks.autoShop.GuiAutoBuyer;
import com.vnator.adminshop.blocks.autoShop.TileEntityAutoBuyer;
import com.vnator.adminshop.blocks.seller.ContainerSeller;
import com.vnator.adminshop.blocks.seller.GuiSeller;
import com.vnator.adminshop.blocks.seller.TileEntitySeller;
import com.vnator.adminshop.blocks.shop.ContainerShop;
import com.vnator.adminshop.blocks.shop.GuiShop;
import com.vnator.adminshop.blocks.shop.TileEntityShop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModGuiHandler implements IGuiHandler {

	public static final int PEDESTAL = 0;
	public static final int SHOP = 1;
	public static final int SELLER = 2;
	public static final int ATM = 3;
	public static final int AUTOBUYER = 4;

	@Override
	public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
		switch (ID){
			case SHOP:
				System.err.println("Opening Shop GUI!");
				return new ContainerShop(player.inventory); //(TileEntityShop)world.getTileEntity(new BlockPos(x, y, z))
			case SELLER:
				return new ContainerSeller(player.inventory, (TileEntitySeller)world.getTileEntity(new BlockPos(x, y, z)));
			case ATM:
				return new ContainerATM(player.inventory, (TileEntityATM)world.getTileEntity(new BlockPos(x, y, z)));
			case AUTOBUYER:
				return new ContainerAutoBuyer(player.inventory, (TileEntityAutoBuyer)world.getTileEntity(new BlockPos(x, y, z)));
			default:
				return null;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
		switch (ID){
			case SHOP:
				return new GuiShop(getServerGuiElement(ID, player, world, x, y, z), player);
			case SELLER:
				return new GuiSeller(getServerGuiElement(ID, player, world, x, y, z), player.inventory, (TileEntitySeller) world.getTileEntity(new BlockPos(x, y, z)));
			case ATM:
				return new GuiATM(getServerGuiElement(ID, player, world, x, y, z), player);
			case AUTOBUYER:
				return new GuiAutoBuyer(getServerGuiElement(ID, player, world, x, y, z), player.inventory, (TileEntityAutoBuyer) world.getTileEntity(new BlockPos(x, y, z)));
			default:
				return null;
		}
	}
}
