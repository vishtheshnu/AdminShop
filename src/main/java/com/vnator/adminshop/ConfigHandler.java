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
	@Config.Comment({"Shop Categories (Max 4 for each)"})
	public static ConfigCategory All_Shop_Categories = new ConfigCategory();
	@Config.Comment({"Buyable items and matching prices (max 42 per category)"})
	public static ConfigBuyItems Buyable_Items = new ConfigBuyItems();
	@Config.Comment({"Sellable items and matching prices (max 42 per category"})
	public static ConfigSellItems Sellable_Items = new ConfigSellItems();

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event){
		if(event.getModID().equals(AdminShop.MODID)){
			ConfigManager.sync(event.getModID(), Config.Type.INSTANCE); //resync config
		}
	}

	public static class GeneralCategory{
		@Config.Comment({"How much money each player starts with"})
		public float startingMoney = 100f;
		@Config.Comment({"Minimum mb sold by Seller at a time. Smaller values = more lag"})
		public int liquidSellPacketSize = 100;
		@Config.Comment({"Minimum power sold by Seller at a time. Smaller values = more lag"})
		public int powerSellPacketSize = 1000;
	}

	public static class ConfigCategory{
		public String [] buyCategories = new String[]{"DEFAULT"};
		public String [] sellCategories = new String[]{"DEFAULT"};
	}

	public static class ConfigBuyItems{
		@Config.Comment({"Format for each line: \"modid:itemName:metadata {nbtJSON}\" where \":metadata\" and \"{nbtJSON}\" are optional"})
		public String [] category1Items = new String[]{"minecraft:cobblestone", "minecraft:coal:1"};
		public float [] category1Prices = new float[]{1, 5};

		public String [] category2Items = new String[]{"minecraft:cobblestone"};
		public float [] category2Prices = new float[]{1};

		public String [] category3Items = new String[]{"minecraft:cobblestone"};
		public float [] category3Prices = new float[]{1};

		public String [] category4Items = new String[]{"minecraft:cobblestone"};
		public float [] category4Prices = new float[]{1};

		@Config.Comment({"List of liquid names. Eg. \"lava\""})
		public String [] liquids = new String[]{"lava"};
		@Config.Comment({"Price per millibucket (1/1000 of a bucket)"})
		public float [] liquidPrices = new float[]{0.05f};
	}

	public static class ConfigSellItems{
		public String [] category1Items = new String[]{"minecraft:stone"};
		public float [] category1Prices = new float[]{1};

		public String [] category2Items = new String[]{"minecraft:stone"};
		public float [] category2Prices = new float[]{1};

		public String [] category3Items = new String[]{"minecraft:stone"};
		public float [] category3Prices = new float[]{1};

		public String [] category4Items = new String[]{"minecraft:stone"};
		public float [] category4Prices = new float[]{1};

		@Config.Comment({"List of liquid names. Eg. \"lava\""})
		public String [] liquids = new String[]{"lava"};
		@Config.Comment({"Price per millibucket (1/1000 of a bucket)"})
		public float [] liquidPrices = new float[]{0.05f};
		@Config.Comment({"Price per forge energy unit"})
		public float forgeEnergyPrice = 0.1f;
	}

	public static ArrayList<String[]> createStringListBuy(){
		ArrayList<String[]> itemNames = new ArrayList<String[]>();
		itemNames.add(Buyable_Items.category1Items);
		itemNames.add(Buyable_Items.category2Items);
		itemNames.add(Buyable_Items.category3Items);
		itemNames.add(Buyable_Items.category4Items);
		return itemNames;
	}

	public static ArrayList<Float[]> createPriceListBuy(){
		ArrayList<Float[]> prices = new ArrayList<Float[]>();

		Float[] arr = new Float[Buyable_Items.category1Prices.length];
		for(int i = 0; i < arr.length; i++)
			arr[i] = Buyable_Items.category1Prices[i];
		prices.add(arr);

		arr = new Float[Buyable_Items.category2Prices.length];
		for(int i = 0; i < arr.length; i++)
			arr[i] = Buyable_Items.category2Prices[i];
		prices.add(arr);

		arr = new Float[Buyable_Items.category3Prices.length];
		for(int i = 0; i < arr.length; i++)
			arr[i] = Buyable_Items.category3Prices[i];
		prices.add(arr);

		arr = new Float[Buyable_Items.category4Prices.length];
		for(int i = 0; i < arr.length; i++)
			arr[i] = Buyable_Items.category4Prices[i];
		prices.add(arr);

		return prices;
	}

	public static ArrayList<String[]> createStringListSell(){
		ArrayList<String[]> itemNames = new ArrayList<String[]>();
		itemNames.add(Sellable_Items.category1Items);
		itemNames.add(Sellable_Items.category2Items);
		itemNames.add(Sellable_Items.category3Items);
		itemNames.add(Sellable_Items.category4Items);
		return itemNames;
	}

	public static ArrayList<Float[]> createPriceListSell(){
		ArrayList<Float[]> prices = new ArrayList<Float[]>();

		Float[] arr = new Float[Sellable_Items.category1Prices.length];
		for(int i = 0; i < arr.length; i++)
			arr[i] = Sellable_Items.category1Prices[i];
		prices.add(arr);

		arr = new Float[Sellable_Items.category2Prices.length];
		for(int i = 0; i < arr.length; i++)
			arr[i] = Sellable_Items.category2Prices[i];
		prices.add(arr);

		arr = new Float[Sellable_Items.category3Prices.length];
		for(int i = 0; i < arr.length; i++)
			arr[i] = Sellable_Items.category3Prices[i];
		prices.add(arr);

		arr = new Float[Sellable_Items.category4Prices.length];
		for(int i = 0; i < arr.length; i++)
			arr[i] = Sellable_Items.category4Prices[i];
		prices.add(arr);

		return prices;
	}

	/*
    private static final String CATEGORY_GENERAL = "general";
    private static final String CATEGORY_DIMENSIONS = "dimensions";

	private static final String CAT_BUYCATS = "buy categories";
	private static final String CAT_SELLCATS = "sell categories";

    private static final String CAT_PBUY = "player buyables";
    private static final String CAT_PSELL = "player sellables";

    private static final String [] CAT_BUY_LIST = new String[]{"BUY1CAT, BUY2CAT, BUY3CAT, BUY4CAT"};
    private static final String [] CAT_BUY_PRICES = new String[]{"BUY1PRC, BUY2PRC, BUY3PRC, BUY4PRC"};
	private static final String [] CAT_SELL_LIST = new String[]{"SELL1CAT, SELL2CAT, SELL3CAT, SELL4CAT"};
	private static final String [] CAT_SELL_PRICES = new String[]{"SELL1PRC, SELL2PRC, SELL3PRC, SELL4PRC"};

    private static final int MAX_CATEGORIES = 4;

    private static String [] buyCats;
    private static String [] sellCats;

    public static void readConfig(){
        Configuration cfg = CommonProxy.config;
        try{
            cfg.load();
            initGeneralConfig(cfg);
            initShopCategories(cfg);
            initPlayerBuyConfig(cfg);
			initPlayerSellConfig(cfg);
        }catch (Exception e1){
            AdminShop.logger.log(Level.ERROR, "Problem loading config file!", e1);
        }finally{
            if(cfg.hasChanged()){
                cfg.save();
            }
        }
    }

    private static void initGeneralConfig(Configuration cfg){
        cfg.addCustomCategoryComment(CATEGORY_GENERAL, "General configuration");
        //goodTut = cfg.getBoolean("goodTutorial", CATEGORY_GENERAL, goodTut, "Set to false if this tutorial sucks :(");
        //realName = cfg.getString("realName", CATEGORY_GENERAL, realName, "Set your name here");
        //System.out.println("Real Name: "+realName);
    }

    private static void initShopCategories(Configuration cfg){
    	cfg.addCustomCategoryComment(CAT_BUYCATS, "Shop categories for buying");
    	buyCats = cfg.getStringList("buy_categories", CAT_BUYCATS, new String[]{"DEFAULT"}, "List at most "+MAX_CATEGORIES+" categories");
		cfg.addCustomCategoryComment(CAT_SELLCATS, "Shop categories for buying");
		sellCats = cfg.getStringList("sell_categories", CAT_SELLCATS, new String[]{"DEFAULT"}, "List at most "+MAX_CATEGORIES+" categories");
		ShopStock.setShopCategories(buyCats, sellCats);
	}

    private static void initPlayerBuyConfig(Configuration cfg){
		cfg.addCustomCategoryComment(CAT_PBUY, "Purchaseable Items");
		ArrayList<String[]> items = new ArrayList<String[]>(MAX_CATEGORIES);
		ArrayList<String[]> prices = new ArrayList<String[]>(MAX_CATEGORIES);
		for(int i = 0; i < Math.min(buyCats.length, MAX_CATEGORIES); i++){
			items.add(cfg.getStringList(CAT_BUY_LIST[i], CAT_PBUY, new String[]{"minecraft:cobblestone:"+i}, "List of player-buyable items"));
			prices.add(cfg.getStringList(CAT_BUY_PRICES[i], CAT_PBUY, new String[]{""+i}, "Price of each player-buyable item"));
		}
		ShopStock.setShopStockBuy(items, prices);
	}

	private static void initPlayerSellConfig(Configuration cfg){
		cfg.addCustomCategoryComment(CAT_PSELL, "Purchaseable Items");
		ArrayList<String[]> items = new ArrayList<String[]>(MAX_CATEGORIES);
		ArrayList<String[]> prices = new ArrayList<String[]>(MAX_CATEGORIES);
		for(int i = 0; i < Math.min(buyCats.length, MAX_CATEGORIES); i++){
			items.add(cfg.getStringList(CAT_SELL_LIST[i], CAT_PSELL, new String[]{"minecraft:stone:"+i}, "List of player-sellable items"));
			prices.add(cfg.getStringList(CAT_SELL_PRICES[i], CAT_PSELL, new String[]{""+i}, "Price of each player-sellable item"));
		}
		ShopStock.setShopStockSell(items, prices);
	}
	*/
}
