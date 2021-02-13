package com.vnator.adminshop.blocks.atm;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.ModGuiHandler;
import com.vnator.adminshop.blocks.BlockTileEntity;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
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
	
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    	private static boolean hasTileEntity;
	
	public BlockATM(){super(Material.ROCK, "atm");
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

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
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		 return new BlockStateContainer(this, new IProperty[] { FACING});
	}

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(FACING, placer.getHorizontalFacing().getOpposite());
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
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }

        return getDefaultState().withProperty(FACING, enumfacing);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumFacing)state.getValue(FACING)).getIndex();
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
