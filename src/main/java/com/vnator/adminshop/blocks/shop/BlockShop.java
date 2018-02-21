package com.vnator.adminshop.blocks.shop;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.ModGuiHandler;
import com.vnator.adminshop.blocks.BlockTileEntity;
import com.vnator.adminshop.capabilities.money.IMoney;
import com.vnator.adminshop.capabilities.money.MoneyProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockShop extends BlockTileEntity<TileEntityShop> {

	public BlockShop(){
		super(Material.ROCK, "shop");
	}

	@Override
	public Class<TileEntityShop> getTileEntityClass(){ return TileEntityShop.class; }

	@Nullable
	@Override
	public TileEntityShop createTileEntity(World world, IBlockState state){ return new TileEntityShop(); }

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
									EnumFacing side, float hitX, float hitY, float hitZ){
		if(!world.isRemote){
			/*Open Gui*/
			player.openGui(AdminShop.instance, ModGuiHandler.SHOP, world, pos.getX(), pos.getY(), pos.getZ());

			IMoney money = player.getCapability(MoneyProvider.MONEY_CAPABILITY, null);
			if(money == null)
				System.err.println("\n\nMONEY IS NULL!!! WOW DUDE!\n\n");
			//player.sendMessage(new TextComponentString("Player Money: "+money.getMoney()));
		}
		return true;
	}
}
