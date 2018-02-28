package com.vnator.adminshop;

import com.vnator.adminshop.items.CheckItem;
import com.vnator.adminshop.items.FirstItem;
import com.vnator.adminshop.items.IngotCopper;
import com.vnator.adminshop.items.ItemBase;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import java.util.*;

public class ModItems {

	public static final List<ItemBase> itemsList = new ArrayList<ItemBase>();

	//public static FirstItem firstItem = new FirstItem();
	public static IngotCopper ingotCopper = new IngotCopper();
	public static CheckItem check = new CheckItem();

	public static void registerItems(RegistryEvent.Register<Item> event){
		event.getRegistry().registerAll(itemsList.toArray(new Item[0]));
	}

	public static void registerModels(){
		System.out.println("Number of Items: "+itemsList.size());
		for(ItemBase i : itemsList){
			i.registerItemModel();
		}
	}
}
