package com.vnator.adminshop.blocks;

import com.vnator.adminshop.ModBlocks;
import com.vnator.adminshop.capabilities.BalanceAdapter;
import com.vnator.adminshop.capabilities.ledger.LedgerProvider;
import com.vnator.adminshop.capabilities.money.MoneyProvider;
import com.vnator.adminshop.packets.PacketHandler;
import com.vnator.adminshop.packets.PacketUpdateMoney;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public abstract class BlockTileEntity<TE extends TileEntity> extends BlockBase{

	public BlockTileEntity(Material material, String name){
		super(material, name);
		setResistance(1f);
		setHardness(1f);
		setHarvestLevel("pickaxe", 0);
		setLightLevel(0);
		setLightOpacity(0);
		ModBlocks.blockTEList.add(this);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune){
		return Item.getItemFromBlock(this);
	}

	@Override
	public int quantityDropped(Random rand){
		return 1;
	}

	public abstract Class<TE> getTileEntityClass();

	public TE getTileEntity(IBlockAccess world, BlockPos pos){
		return (TE)world.getTileEntity(pos);
	}

	@Override
	public boolean hasTileEntity(IBlockState state){
		return true;
	}

	@Nullable
	@Override
	public abstract TE createTileEntity(World world, IBlockState state);

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
									EnumFacing side, float hitX, float hitY, float hitZ){

		if(!world.isRemote) {
			//Update client
			PacketHandler.INSTANCE.sendTo(new PacketUpdateMoney(BalanceAdapter.getMoneyServer((EntityPlayerMP)player)),
					(EntityPlayerMP) player);
		}
		return true;
	}
}
