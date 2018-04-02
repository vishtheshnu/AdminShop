package com.vnator.adminshop.blocks.atm;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.ModGuiHandler;
import com.vnator.adminshop.blocks.BlockTileEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockATM extends BlockTileEntity<TileEntityATM> {

	public BlockATM(){super(Material.ROCK, "atm");}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}
	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
									EnumFacing side, float hitX, float hitY, float hitZ){
		super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
		if(!world.isRemote){
			player.openGui(AdminShop.instance, ModGuiHandler.ATM, world, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	@Override
	public Class<TileEntityATM> getTileEntityClass() {
		return TileEntityATM.class;
	}

	@Nullable
	@Override
	public TileEntityATM createTileEntity(World world, IBlockState state) {
		return new TileEntityATM();
	}
}
