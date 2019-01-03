package com.vnator.adminshop.blocks.shop;

import com.opencsv.CSVReader;
import com.vnator.adminshop.AdminShop;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Loads the contents of the shop from a csv file. Is a singleton
 */
public class ShopLoader {

	private static final String AS_ITEM_REGEX = "[a-zA-Z0-9]+:[a-zA-Z0-9]+(:[0-9]+)? \\{.*\\}";
	private static final String CT_ITEM_REGEX = "<[a-zA-Z0-9]+:[a-zA-Z0-9]+(:[0-9]+)?>\\.withTag\\(\\{.*\\}\\)"; //<mod:item:metadata>.withTag({...})
	private static final String SHOP_FILE = "config/adminshop/shop.csv";
	private static final String DEFAULT_SHOP_FILE = "assets/adminshop/default_shop.csv";//= new ResourceLocation(AdminShop.MODID, "default_shop.csv");
	private static ShopLoader inst;

	private static final String CATEGORY_OPTION_BUY = "Buy Category Names:";
	private static final String CATEGORY_OPTION_SELL = "Sell Category Names:";

	private CSVReader reader;

	//Saved data
	private int prevCat = -1;
	private String [] buyCategoryNames, sellCategoryNames;
	private ArrayList<ArrayList<ShopItem>> buyItems, sellItems;
	private ArrayList<TextComponentString> errorLog;
	private String fileContents;
	private boolean isLogged;
	private static boolean isWorldLoaded = false;

	private ShopLoader(){
		buyItems = new ArrayList<ArrayList<ShopItem>>();
		sellItems = new ArrayList<ArrayList<ShopItem>>();
		errorLog = new ArrayList<TextComponentString>();
		isLogged = false;
	}

	/**
	 * Gets instance of the ShopLoader class. Loads the shop from file if it's the first call
	 * @return
	 */
	public static ShopLoader getInstance(){
		if(inst == null)
			inst = new ShopLoader();
		return inst;
	}

	public void loadOnWorldStart(){
		if(!isWorldLoaded)
			loadShop(null);
		isWorldLoaded = true;
	}

	public void loadShop(ICommandSender player){
		loadShop(player, null);
	}

	public void loadShop(ICommandSender player, Reader shopFile){
		AdminShop.logger.log(Level.INFO, "Loading shop!");
		if(!new File(SHOP_FILE).exists()){
			copyDefault();
		}
		try {
			resetVars();
			if(shopFile == null) {
				reader = new CSVReader(new FileReader(SHOP_FILE));
			}else{
				reader = new CSVReader(shopFile);
			}
			Iterator<String []> iter = reader.iterator();
			//logError("Loading Shop. Warnings and Errors will be printed below...", player);
			int lineNum = 0;
			while(iter.hasNext()){
				lineNum++;
				String [] line = iter.next();
				System.out.print(lineNum+"| ");
				for(String s : line)
					System.out.print("|"+s);
				System.out.println();
				parseLine(line, lineNum, player);
			}
		}catch (FileNotFoundException e){
			AdminShop.logger.log(Level.ERROR, "Could not create shop file in config directory");
			e.printStackTrace();
			System.exit(1);
		}
		isLogged = true;

		//Load contents of file as string if on the server

		try {
			fileContents = "";
			Scanner scan = new Scanner(new File(SHOP_FILE));
			while (scan.hasNext())
				fileContents += scan.nextLine() + "\n";
		} catch (IOException e) {
			AdminShop.logger.log(Level.ERROR, "Shop file not found in getFileContents().");
		}


		//Set the loaded values to the ShopStock
		ShopStock.setShopCategories(buyCategoryNames, sellCategoryNames);
		ShopStock.setShopStock(buyItems, sellItems);
	}

	/**
	 * Parse a single line of load shop
	 * @param line The line from the shop file represented as a String array
	 */
	private void parseLine(String [] line, int lineNumber, ICommandSender player){
		//Skip if line is empty or begins with #
		if(line.length == 0)
			return;
		else if(line[0].length() == 0){
			return;
		}
		else if(line[0].charAt(0) == '#')
			return;

		//Setting buy and sell categories
		else if(line[0].equals(CATEGORY_OPTION_BUY)){
			int catCount = 0;
			for(int i = 1; i < line.length; i++){
				if(line[i].length() != 0)
					catCount++;
				else
					i = line.length;
			}
			buyCategoryNames = new String[catCount];
			for(int i = 0; i < catCount; i++){
				buyCategoryNames[i] = line[i+1];
			}
		}
		else if(line[0].equals(CATEGORY_OPTION_SELL)){
			int catCount = 0;
			for(int i = 1; i < line.length; i++){
				if(line[i].length() != 0)
					catCount++;
				else
					i = line.length;
			}
			sellCategoryNames = new String[catCount];
			for(int i = 0; i < catCount; i++){
				sellCategoryNames[i] = line[i+1];
			}
		}

		//Add an item to the shop
		else if(line[0].equalsIgnoreCase("buy") ||
				line[0].equalsIgnoreCase("sell")){
			//Get shop item from text
			ShopItem shopItem = parseTextToShopItem(line[1], lineNumber, player);

			//Check if it's oredict with buy
			if(line[0].equalsIgnoreCase("buy") && shopItem.isOredict()){
				logError("Error Line "+lineNumber+": Can only use oredict for selling, not buying!", player);
				return; //Can't buy an oredict entry, so don't add this entry
			}

			//Try to extract the price of the item
			try{
				float price = Float.parseFloat(line[2]);
				shopItem.setPrice(price);
			}catch (NumberFormatException e){
				logError("Error Line "+lineNumber+" Improperly formatted price!" +
						"Must be decimal value with no other characters (no dollar signs or currency symbols)", player);
				return; //Don't add item with no price
			}

			//Add item to proper ArrayList
			ArrayList<ArrayList<ShopItem>> mylist = line[0].equalsIgnoreCase("buy") ? buyItems : sellItems;
			String [] nameArr = line[0].equalsIgnoreCase("buy") ? buyCategoryNames : sellCategoryNames;

			//Get shop category index
			int catIndex = 0;
			try{
				catIndex = Integer.parseInt(line[3]);
			}catch (NumberFormatException e){
				logError("Error Line "+lineNumber+": Category Index not an integer!" +
						"Must be a whole number", player);
				return; //Don't add item with no shop category
			}

			//Check if category index > number of category names
			/* Category names might be listed after shop items, so don't immediately report here
			if(nameArr.length <= catIndex){
				logError("Error Line "+lineNumber+": Category Index greater than the number of specified category" +
						" names! Either reduce the category index or add new category names", player);
				return; //Don't add to a w
			}
			*/

			//Fill list with a number of sub-lists to match category index
			for(int i = mylist.size(); i < catIndex+1; i++){
				mylist.add(new ArrayList<ShopItem>());
			}
			mylist.get(catIndex).add(shopItem);

		}
		//Improperly formatted line (aka not default)
		else if(line.length != 4){
			logError("Error Line "+lineNumber+": Improperly formatted line." +
					" This type of line format/syntax is not supported", player);
			return;
		}

	}

	private void copyDefault(){
		//Copy a template csv file to the config folder and retry
		try {
			InputStream defStream = AdminShop.class.getClassLoader().getResourceAsStream(DEFAULT_SHOP_FILE);
			byte [] buffer = new byte[defStream.available()];
			defStream.read(buffer);
			FileOutputStream outStream = new FileOutputStream(new File(SHOP_FILE));
			outStream.write(buffer);
		}catch (IOException e){
			AdminShop.logger.log(Level.ERROR, "Could not copy default file!");
			e.printStackTrace();
			System.exit(1); //what do you do now?
		}
	}

	/**
	 * Takes the item text from the csv and retrieves the corresponding ShopItem. Can be an ItemStack, FluidStack,
	 * or OreDictionary entry with nbt.
	 * Will return without metadata or nbt if either are improperly formatted, prints out error messages if item or
	 * fluid name don't correspond to an existing item/fluid.
	 * @param item String representation of item/fluid/oredict name + optional nbt. Can use oredict name instead of item name
	 * @param lineNum Line number that item is from, used for error reporting
	 * @return ShopItem that represents item
	 */
	private ShopItem parseTextToShopItem(String item, int lineNum, ICommandSender player){

		//Convert crafttweaker format to AdminShop format if it's in there

		//Remove < and > if exist
		if(item.charAt(0) == '<')
			item = item.substring(1);
		if(item.contains(">"))
			item = item.replace('>', ' ');

		//Change .withTag({...}) to just {...}
		if(item.contains(".withTag(")){
			item = item.replace(".withTag(", " ");
			item = item.substring(0 , item.length()-1);
		}

		//Retrieve NBT if exists
		NBTTagCompound nbt = null;
		if(item.contains("{")) {
			String nbtText = item.substring(item.indexOf('{'), item.lastIndexOf('}'));
			nbtText += '}';
			try {
				nbt = JsonToNBT.getTagFromJson(nbtText);
			} catch (NBTException e) {
				logError("Error Line "+lineNum+": Improperly formatted NBT", player);
				nbt = null;
			}
			item = item.substring(0, item.indexOf('{'));
			item = item.trim();
		}

		String [] itemParts = item.split(":");

		//Check if it's a fluid
		if(itemParts[0].equals("liquid") || itemParts[0].equals("fluid")){
			//Ignore nbt, parse and return
			Fluid fluid = FluidRegistry.getFluid(itemParts[1]);
			if(fluid == null){
				logError("Error Line "+lineNum+": Fluid named "+itemParts[1]+
						"doesn't exist! Please double check name", player);
			}
			FluidStack fstack = new FluidStack(fluid, 1, nbt);
			ShopItem toret = new ShopItem(fstack);
			//Check other parsed info to warn user of improperly formatted line
			if(itemParts.length == 3){
				logError("Warning Line "+lineNum+": Unnecessary metadata for fluid." +
						" Shop will still work, but the metadata won't do anything", player);
			}

			return toret;
		}

		//Check if it's an oredict entry
		else if(itemParts[0].equals("ore")){
			//Check if there is at least one item with specified oredict entry
			if(OreDictionary.getOres(itemParts[1]).size() == 0){
				logError("Error Line "+lineNum+": OreDictionary entry named "+itemParts[1]+
						" doesn't have any items registered to it. Double " +
						"check entry or add entries via crafttweaker or a new mod that adds those entries", player);
			}
			return new ShopItem(itemParts[1], nbt);
		}

		//Must be an item
		else{
			//Retrieve metadata
			int meta = 0;
			if(itemParts.length == 3){
				try{
					meta = Integer.parseInt(itemParts[2]);
				}catch(NumberFormatException e){
					logError("Error Line "+lineNum+": Specified metadata isn't an integer." +
							" You must use a whole number", player);
					meta = 0;
				}
			}
			Item i = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemParts[0]+":"+itemParts[1]));
			if(i == null){
				logError("Error Line "+lineNum+": Item named "
						+itemParts[1]+" doesn't exist! Please double check name", player);
			}
			ItemStack istack = new ItemStack(i, 1, meta, nbt);
			return new ShopItem(istack);
		}
	}

	/**
	 * Clears storage variables so the gc can easily clear previously loaded data
	 */
	private void resetVars(){
		buyItems = new ArrayList<>();
		sellItems = new ArrayList<>();
		/*
		for(ArrayList al : buyItems){
			al.clear();
		}
		for(ArrayList al : sellItems){
			al.clear();
		}
		buyItems.clear();
		sellItems.clear();
		*/
	}

	/**
	 * Called when an error is found in the shop csv file's formatting or syntax. Either sends the error to the
	 * player if player isn't null or saves it to a log if the player is null.
	 * @param err Error message to log
	 * @param player Player to log to. If null, will record error on log
	 */
	private void logError(String err, ICommandSender player){
		TextComponentString tcs = new TextComponentString(err);
		AdminShop.logger.log(Level.ERROR, err);
		if(player != null)
			player.sendMessage(tcs);
		else if(!isLogged){
			errorLog.add(tcs);
		}
	}

	/**
	 * Takes a non-null player and prints the currently stored error log to them if it isn't empty. Then clears the log
	 * @param player Player to print the currently stored error log to
	 */
	public void printLog(ICommandSender player){
		for(TextComponentString tcs : errorLog){
			player.sendMessage(tcs);
		}
		errorLog.clear();
		isLogged = false;
	}


	//Getters
	public String getFileContents(){

		//AdminShop.logger.log(Level.INFO, "Shop File contents:\n"+contents);
		return fileContents;
	}

}
