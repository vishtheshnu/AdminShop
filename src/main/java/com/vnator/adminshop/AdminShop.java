package com.vnator.adminshop;

import com.google.common.eventbus.Subscribe;
import com.vnator.adminshop.blocks.shop.ShopStock;
import com.vnator.adminshop.capabilities.money.MoneyProvider;
import com.vnator.adminshop.client.AdminshopTab;
import com.vnator.adminshop.packets.PacketHandler;
import com.vnator.adminshop.packets.PacketUpdateMoney;
import com.vnator.adminshop.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@Mod.EventBusSubscriber
@Mod(modid = AdminShop.MODID, name = AdminShop.NAME, version = AdminShop.VERSION)
public class AdminShop
{
	public static final String MODID = "adminshop";
	public static final String NAME = "Admin Shop";
	public static final String VERSION = "1.0";

	@SidedProxy(clientSide = "com.vnator.adminshop.proxy.ClientProxy", serverSide = "com.vnator.adminshop.proxy.ServerProxy")
	public static CommonProxy proxy;

	@Mod.Instance
	public static AdminShop instance;

	public static final AdminshopTab creativeTab = new AdminshopTab();

	public static Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		System.out.println(NAME+" is loading!");
		proxy.preInit(event);
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new ModGuiHandler());
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		// some example code
		logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		proxy.postInit(event);

		//Register items into ShopStock
		registerShopStock();
	}

	private void registerShopStock(){
		ShopStock.setShopCategories(ConfigHandler.All_Shop_Categories.buyCategories, ConfigHandler.All_Shop_Categories.sellCategories);
		ShopStock.setShopStockBuy(ConfigHandler.createStringListBuy(), ConfigHandler.createPriceListBuy());
		ShopStock.setShopStockSell(ConfigHandler.createStringListSell(), ConfigHandler.createPriceListSell());
	}

	@SubscribeEvent
	public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
		float money = event.player.getCapability(MoneyProvider.MONEY_CAPABILITY, null).getMoney();
		PacketHandler.INSTANCE.sendTo(new PacketUpdateMoney(money), (EntityPlayerMP)event.player);
	}

	@Mod.EventBusSubscriber
	public static class RegistrationHandler{

	    @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event){
			ModItems.registerItems(event);
			ModBlocks.registerItemBlocks(event);
        }

        @SubscribeEvent
		public static void registerBlocks(RegistryEvent.Register<Block> event){
			ModBlocks.register(event);
		}

		@SubscribeEvent
		public static void registerModels(ModelRegistryEvent event){
			ModItems.registerModels();
			ModBlocks.registerModels();
		}
	}

}
