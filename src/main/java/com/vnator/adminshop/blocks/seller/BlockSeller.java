package com.vnator.adminshop.blocks.seller;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.ModGuiHandler;
import com.vnator.adminshop.blocks.BlockTileEntity;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class BlockSeller extends BlockTileEntity<TileEntitySeller> {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    	private static boolean hasTileEntity;
	
	public BlockSeller(){super(Material.ROCK, "seller");
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		 return new BlockStateContainer(this, new IProperty[] { FACING});
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
	public Class<TileEntitySeller> getTileEntityClass() {
		return TileEntitySeller.class;
	}

	@Nullable
	@Override
	public TileEntitySeller createTileEntity(World world, IBlockState state) {
		return new TileEntitySeller();
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		//Set placing player as Seller's owner
		if(!worldIn.isRemote){
			TileEntitySeller ent = (TileEntitySeller) worldIn.getTileEntity(pos);
			if(placer instanceof EntityPlayer)
				ent.setPlayer(placer.getUniqueID().toString());
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
									EnumFacing side, float hitX, float hitY, float hitZ){

		super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
		if(!world.isRemote){
			TileEntitySeller ent = (TileEntitySeller) world.getTileEntity(pos);
			if(ent.getPlayer() == null) {
				ent.setPlayer(player.getUniqueID().toString());
				player.sendMessage(new TextComponentString("Registered Player to Item Seller!"));
				ent.markDirty();
			}

			if(ent.getPlayer().equals(player.getUniqueID().toString())){
				player.sendMessage(new TextComponentString("You are the registered owner of this block."));
			}else{
				player.sendMessage(new TextComponentString("WARNING: You will lose any items inserted. " +
						"This block is registered to "+ent.getPlayer()));
			}

			IFluidHandlerItem handler = FluidUtil.getFluidHandler(player.getHeldItem(hand));//.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
			if(handler != null){
				//Put liquid into tank
				FluidStack liquid = handler.drain(1000, false);
				if(liquid != null && ent.fill(liquid, false) == 1000){
					ent.fill(handler.drain(1000, true), true);
					player.setHeldItem(hand, handler.getContainer());
				}
			}else{
				player.openGui(AdminShop.instance, ModGuiHandler.SELLER, world, pos.getX(), pos.getY(), pos.getZ());
			}
			//player.sendMessage(new TextComponentString("liquid in tank: "+ent.tank.getFluid().getFluid().getName()+" x"+ent.tank.getFluidAmount()));
		}
		return true;
	}
	
	@Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(FACING, placer.getHorizontalFacing().getOpposite());
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
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		IItemHandler inv = world.getTileEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		for(int i = 0; i < inv.getSlots(); i++){
			ItemStack item = inv.getStackInSlot(i);
			EntityItem ent = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), item);
			world.spawnEntity(ent);
		}
		super.breakBlock(world, pos, state);
	}
}
