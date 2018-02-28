package com.vnator.adminshop;

import com.vnator.adminshop.blocks.BlockBase;
import com.vnator.adminshop.blocks.BlockOre;
import com.vnator.adminshop.blocks.BlockTileEntity;
import com.vnator.adminshop.blocks.atm.BlockATM;
import com.vnator.adminshop.blocks.counter.BlockCounter;
import com.vnator.adminshop.blocks.seller.BlockSeller;
import com.vnator.adminshop.blocks.pedestal.BlockPedestal;
import com.vnator.adminshop.blocks.shop.BlockShop;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks {

	public static final List<BlockBase> blockList = new ArrayList<BlockBase>();
	public static final List<Item> blockItemList = new ArrayList<Item>();
	public static final List<BlockTileEntity> blockTEList = new ArrayList<BlockTileEntity>();

	public static BlockOre oreCopper = new BlockOre("ore_copper");
	public static BlockCounter counter = new BlockCounter();
	public static BlockPedestal pedestal = new BlockPedestal();
	public static BlockShop shop = new BlockShop();
	public static BlockSeller itemSeller = new BlockSeller();
	public static BlockATM atm = new BlockATM();

    public static void register(RegistryEvent.Register<Block> event){
		event.getRegistry().registerAll(blockList.toArray(new BlockBase[0]));
		for(BlockTileEntity te : blockTEList){
			GameRegistry.registerTileEntity(te.getTileEntityClass(), te.getRegistryName().toString());
		}
		//GameRegistry.registerTileEntity(counter.getTileEntityClass(), counter.getRegistryName().toString());
    }

    public static void registerItemBlocks(RegistryEvent.Register<Item> event){
		event.getRegistry().registerAll(blockItemList.toArray(new Item[0]));
		/*
		for(BlockBase b : blockList){
			event.getRegistry().register(b.createItemBlock());
		}
		*/
    }

    public static void registerModels(){
		for(BlockBase b : blockList){
			b.registerItemModel(Item.getItemFromBlock(b));
		}
    }
}
