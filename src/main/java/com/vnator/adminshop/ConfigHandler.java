package com.vnator.adminshop;

import com.vnator.adminshop.blocks.shop.ShopStock;
import com.vnator.adminshop.proxy.CommonProxy;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;

@Config(modid = AdminShop.MODID, name = AdminShop.MODID+"/"+AdminShop.MODID, category = "")
@Mod.EventBusSubscriber
public class ConfigHandler {

	@Config.Comment({"General Config"})
	public static GeneralCategory GENERAL_CONFIGS = new GeneralCategory();

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event){
		if(event.getModID().equals(AdminShop.MODID)){
			ConfigManager.sync(event.getModID(), Config.Type.INSTANCE); //resync config
		}
	}

	public static class GeneralCategory{
		@Config.Comment({"How much money each player starts with"})
		public float startingMoney = 100f;
		@Config.Comment({"Minimum mb sold by Seller at once. Smaller values = more lag. Set low if difficult to obtain and expensive liquids are sellable, like UU matter." +
				"NOTE: does not change the price of liquids, just how the seller block interacts with them."})
		public int liquidSellPacketSize = 10;
		@Config.Comment({"Minimum power sold by Seller at once. Smaller values = more lag. Set low if it's extremely difficult for the player to produce power in this modpack."})
		public int powerSellPacketSize = 100;
		@Config.Comment({"How much money to give the play in exchange for each Forge Energy unit"})
		public float forgeEnergyPrice = 0.0005f;
	}
}
