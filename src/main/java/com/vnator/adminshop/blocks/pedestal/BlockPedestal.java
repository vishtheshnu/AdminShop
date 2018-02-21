package com.vnator.adminshop.blocks.pedestal;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.ModGuiHandler;
import com.vnator.adminshop.blocks.BlockTileEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class BlockPedestal extends BlockTileEntity<TileEntityPedestal> {

	public BlockPedestal(){
		super(Material.ROCK, "pedestal");
	}

	@Override
	public Class<TileEntityPedestal> getTileEntityClass(){
		return TileEntityPedestal.class;
	}

	@Nullable
	@Override
	public TileEntityPedestal createTileEntity(World world, IBlockState state){
		return new TileEntityPedestal();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
									EnumFacing side, float hitX, float hitY, float hitZ){
		if(!world.isRemote){
			ItemStack heldItem = player.getHeldItem(hand);
			TileEntityPedestal tile = getTileEntity(world, pos);
			IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
			if(!player.isSneaking()){
				if(heldItem.isEmpty()){
					player.setHeldItem(hand, itemHandler.extractItem(0, 64, false));
				}else{
					player.setHeldItem(hand, itemHandler.insertItem(0, heldItem, false));
				}
				tile.markDirty();
			}else{
				/*
				ItemStack stack = itemHandler.getStackInSlot(0);
				if(!stack.isEmpty()){
					String localized = AdminShop.proxy.localize(stack.getUnlocalizedName()+".name");
				}
				*/
				player.openGui(AdminShop.instance, ModGuiHandler.PEDESTAL, world, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		return true;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		TileEntityPedestal tile = getTileEntity(world, pos);
		IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
		ItemStack stack = itemHandler.getStackInSlot(0);
		if(!stack.isEmpty()){
			EntityItem item = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);
			world.spawnEntity(item);
		}
		super.breakBlock(world, pos, state);
	}
}
