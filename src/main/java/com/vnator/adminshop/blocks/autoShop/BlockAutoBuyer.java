package com.vnator.adminshop.blocks.autoShop;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.ModGuiHandler;
import com.vnator.adminshop.blocks.BlockTileEntity;
import com.vnator.adminshop.blocks.seller.TileEntitySeller;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class BlockAutoBuyer extends BlockTileEntity<TileEntityAutoBuyer> {

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	public BlockAutoBuyer(){
		super(Material.ROCK, "buyer");
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
									EnumFacing side, float hitX, float hitY, float hitZ){
		if(!world.isRemote){
			TileEntityAutoBuyer tile = getTileEntity(world, pos);
			IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
			//player.sendMessage(new TextComponentString(itemHandler.getStackInSlot(0).toString()));
			if(!player.getUniqueID().toString().equals(tile.getPlayer())){
				if(tile.isLocked()) {
					player.sendMessage(new TextComponentString("ACCESS DENIED: Not your Auto Buyer"));
					return true;
				}
				else
					player.sendMessage(new TextComponentString("Note: Not your auto buyer, but it's unlocked"));
			}else if(player.isSneaking()){
				tile.toggleLock();
				if(tile.isLocked())
					player.sendMessage(new TextComponentString("Now Locked: Only you can access this."));
				else
					player.sendMessage(new TextComponentString("Now Unlocked: Anyone can access this. Be careful!"));
				return true;
			}
			player.openGui(AdminShop.instance, ModGuiHandler.AUTOBUYER, world, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		//Set placing player as Seller's owner
		if(!worldIn.isRemote){
			TileEntityAutoBuyer ent = (TileEntityAutoBuyer) worldIn.getTileEntity(pos);
			if(placer instanceof EntityPlayer)
				ent.setPlayer(placer.getUniqueID().toString());
		}
	}

	@Override
	public Class<TileEntityAutoBuyer> getTileEntityClass() {
		return TileEntityAutoBuyer.class;
	}

	@Nullable
	@Override
	public TileEntityAutoBuyer createTileEntity(World world, IBlockState state) {
		return new TileEntityAutoBuyer();
	}
}
