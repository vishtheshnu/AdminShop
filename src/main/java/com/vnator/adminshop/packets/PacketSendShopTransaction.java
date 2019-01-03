package com.vnator.adminshop.packets;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.ConfigHandler;
import com.vnator.adminshop.blocks.shop.ShopItem;
import com.vnator.adminshop.blocks.shop.ShopItemStack;
import com.vnator.adminshop.blocks.shop.ShopStock;
import com.vnator.adminshop.capabilities.BalanceAdapter;
import com.vnator.adminshop.client.AdminshopTab;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.Level;

import static net.minecraftforge.items.ItemHandlerHelper.insertItem;

/**
 * Packet sent from client to server to perform a transaction.
 */
public class PacketSendShopTransaction implements IMessage {

	private short category; //Shop Category index
	private int index; //Item index in chosen category
	private int quantity; //amount of item to perform transaction of
	private boolean toBuy; //true if item on buying list, false if on selling list

	@Override
	public void fromBytes(ByteBuf buf) {
		category = buf.readShort();
		index = buf.readInt();
		quantity = buf.readInt();
		toBuy = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeShort(category);
		buf.writeInt(index);
		buf.writeInt(quantity);
		buf.writeBoolean(toBuy);
	}

	public PacketSendShopTransaction(short category, int index, int quantity, boolean toBuy){
		this.category = category;
		this.index = index;
		this.quantity = quantity;
		this.toBuy = toBuy;
		System.out.println("Sending transaction IMessage! "+category+" , "+index+" , "+quantity+" , "+toBuy);
	}

	public PacketSendShopTransaction(){}

	public static class Handler implements IMessageHandler<PacketSendShopTransaction, IMessage>{

		@Override
		public IMessage onMessage(PacketSendShopTransaction message, MessageContext ctx) {
			System.out.println("Received transaction message from client!");
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(PacketSendShopTransaction message, MessageContext ctx){
			AdminShop.logger.log(Level.INFO, "Handling transaction packet");

			//Get ShopItem
			if(message.toBuy){
				if(message.category < 0 || message.category >= ShopStock.buyStock.size() ||
						message.index < 0 || message.index >= ShopStock.buyStock.get(message.category).size()){
					AdminShop.logger.log(Level.ERROR, "Packet received item index that doesn't exist!");
					return;
				}
			}else{
				if(message.category < 0 || message.category >= ShopStock.sellStock.size() ||
						message.index < 0 || message.index >= ShopStock.sellStock.get(message.category).size()) {
					AdminShop.logger.log(Level.ERROR, "Packet received item index that doesn't exist!");
					return;
				}
			}

			//Get the item
			ShopItem myitem = message.toBuy ? ShopStock.buyStock.get(message.category).get(message.index) :
					ShopStock.sellStock.get(message.category).get(message.index);

			if(message.toBuy){
				buyTransaction(ctx.getServerHandler().player, myitem, message.quantity);
			}else{
				sellTransaction(ctx.getServerHandler().player, myitem, message.quantity);
			}

			//Update client with new balance
			EntityPlayerMP player = ctx.getServerHandler().player;
			PacketHandler.INSTANCE.sendTo(new PacketUpdateMoney(
					BalanceAdapter.getMoneyServer(player)
			), player);
		}

		private void buyTransaction(EntityPlayer player, ShopItem item, int quantity){
			IItemHandler inventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			if(item.isItem()) {
				//Attempt to insert the items, and only perform transaction on what can fit
				AdminShop.logger.log(Level.INFO, "Buying Item");
				ItemStack toInsert = item.getItem().copy();
				toInsert.setCount(quantity);
				ItemStack returned = ItemHandlerHelper.insertItemStacked(inventory, toInsert, true);
				if(returned.getCount() == quantity){
					player.sendMessage(new TextComponentString("Not enough inventory space for item!"));
					return;
				}
				float price = (quantity - returned.getCount()) * item.getPrice();
				boolean success = BalanceAdapter.withdraw(player, price);
				if (success) {
					ItemHandlerHelper.insertItemStacked(inventory, toInsert, false);
				} else {
					player.sendMessage(new TextComponentString("Not enough money!"));
					AdminShop.logger.log(Level.ERROR, "Not enough money to perform transaction!");
				}
			}else if(item.isFluid()){
				AdminShop.logger.log(Level.INFO, "Buying Fluid");
				//Find a fluid container that can accept the fluid, perform transaction on what can fit
				FluidStack toInsert = item.getFluid().copy();
				toInsert.amount = quantity;

				//Find the first container that can accept the fluid
				boolean hasFilled = false;
				for(int i = 0; i < inventory.getSlots(); i++){
					//Check if buying exactly 1 bucket and item is an empty bucket
					if(inventory.getStackInSlot(i).getItem().equals(Items.BUCKET)){
						//inventory.insertItem()
					}
					//Look for fluid container item (not bucket)
					IFluidHandlerItem handler = FluidUtil.getFluidHandler(inventory.getStackInSlot(i));
					if(handler == null)
						continue;

					int inserted = handler.fill(toInsert, false);

					//Check if fluid container can accept purchased fluid
					if(inserted <= 0)
						continue;

					AdminShop.logger.log(Level.INFO, "Found container for fluid, accepted "+inserted+" mb");

					//Check if player can afford purchase. Finalize transaction if they can.
					hasFilled = true;
					boolean success = BalanceAdapter.withdraw(player, inserted*item.getPrice());
					if(success){
						ItemStack tankSlot = inventory.getStackInSlot(i);
						if(tankSlot.getCount() > 1){
							//Find empty slot to insert item
							ItemStack separateTank = splitSingleItem(inventory, tankSlot);
							if(separateTank != null){
								handler = FluidUtil.getFluidHandler(separateTank);
							}
							//Not enough space for new tank, refund purchase, print error message, and return
							else{
								//player.sendMessage(new TextComponentString("Not enough inventory space for item!"));
								BalanceAdapter.deposit(player, inserted*item.getPrice());
								AdminShop.logger.log(Level.INFO, "Inventory full, skipping this stacked tank");
								continue;
							}
						}
						inserted = handler.fill(toInsert, true);
						AdminShop.logger.log(Level.INFO, "Withdrew money from player, inserted "+inserted+" mb");
					}else{
						player.sendMessage(new TextComponentString("Not enough money!"));
						AdminShop.logger.log(Level.ERROR, "Not enough money to perform transaction!");
					}
					AdminShop.logger.log(Level.INFO, "Found container that can accept fluid");
					break;
					//FluidUtil.tryFillContainer(inventory.getStackInSlot(i), )
				}

				//No appropriate tank to insert fluids into
				if(!hasFilled){
					player.sendMessage(new TextComponentString("No liquid container in inventory to hold purchase!"));
				}

				//Completed buy fluid operation, return
				return;
			}else{
				AdminShop.logger.log(Level.ERROR, "Non item or fluid type used for buy! Something really wrong");
			}
		}

		private void sellTransaction(EntityPlayer player, ShopItem item, int quantity){
			IItemHandler inventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			int numSold = removeItemsFromInventory(inventory, item, quantity);
			BalanceAdapter.deposit(player, item.getPrice()*numSold);
		}


		/**
		 * Takes out one item of the parameter ItemSTack and adds it to an empty space in the parameter inventory
		 * @param inventory Inventory where the ItemStack exists and will be split within
		 * @param toSplit ItemStack to split
		 * @return The single ItemStack that is now within the inventory. Null if the inventory doesn't have room to split
		 */
		private ItemStack splitSingleItem(IItemHandler inventory, ItemStack toSplit){
			//Get single itemstack
			ItemStack singleItem = toSplit.copy();
			singleItem.setCount(1);
			//Find empty space to insert
			for(int i = 0; i < inventory.getSlots(); i++){
				if(inventory.getStackInSlot(i) == null || inventory.getStackInSlot(i).isEmpty()){
					AdminShop.logger.log(Level.INFO, "Found empty inventory space: index "+i);
					ItemStack leftover = inventory.insertItem(i, singleItem, false);
					//Make sure the free inventory space accepted the split item
					if(leftover == null || leftover.isEmpty()){
						toSplit.setCount(toSplit.getCount()-1);
						return singleItem;
					}else{
						continue;
					}
				}
			}
			return null;
		}

		/**
		 * Removes the equivalent parameter itemstack from the inventory (item and count)
		 * @param inv Inventory to remove from
		 * @param item ItemStack that represents what is to be removed. item.count() = number of items to be removed
		 * @return Number of items actually removed.
		 */
		private int removeItemsFromInventory(IItemHandler inv, ItemStack item){
			int count = item.getCount();
			System.out.println("Removing items from inventory! # to remove: "+count);
			for(int i = 0; i < inv.getSlots(); i++){
				ItemStack comp = inv.getStackInSlot(i);
				if(itemstacksEqual(comp, item)){ //Found items we can remove
					if(count > comp.getCount()){ //Remove entirety of this stack
						count -= comp.getCount();
						inv.extractItem(i, comp.getCount(), false);
					}else{ //Remove what is left of stack, return number removed
						inv.extractItem(i, count, false);
						return item.getCount();
					}
				}
			}

			//Removed as much as possible, return count
			return item.getCount() - count;
		}

		/**
		 * Same as the other removeItemsFromInventory, but checks oredict instead of item equivalence
		 * @param inv
		 * @param item
		 * @return
		 */
		private int removeItemsFromInventory(IItemHandler inv, ShopItem item, int quantity){
			int count = quantity;
			System.out.println("Removing items from inventory! # to remove: "+count);
			for(int i = 0; i < inv.getSlots(); i++){
				ItemStack comp = inv.getStackInSlot(i);
				if(item.itemEqual(comp)){ //Found items we can remove
					if(item.isItem() || item.isOredict()) {
						if (count > comp.getCount()) { //Remove entirety of this stack
							count -= comp.getCount();
							inv.extractItem(i, comp.getCount(), false);
						} else { //Remove what is left of stack, return number removed
							inv.extractItem(i, count, false);
							return quantity;
						}
					}else if(item.isFluid()){
						IFluidHandlerItem fhandle = null;
						ItemStack tank = inv.getStackInSlot(i);
						if(tank.getCount() > 1){
							ItemStack singleTank = splitSingleItem(inv, tank);
							//Could split tank, drain from that and repeat with the tank(s) in inv index of i
							if(singleTank != null){
								fhandle = FluidUtil.getFluidHandler(singleTank);
								i--;
							}
							//Couldn't split tank. Continue to next slot in inventory
							else{
								continue;
							}
						}
						if(fhandle == null)
							fhandle = FluidUtil.getFluidHandler(tank);
						FluidStack drained = fhandle.drain(count, true);
						if(drained.amount == count){
							return quantity;
						}else{
							count -= drained.amount;
						}
					}
				}
			}

			//Removed as much as possible, return count
			return quantity - count;
		}

		private boolean itemstacksEqual(ItemStack a, ItemStack b){
			if(a.getItem() == b.getItem() && a.getMetadata() == b.getMetadata()) {
				NBTTagCompound atag = a.getTagCompound();
				NBTTagCompound btag = b.getTagCompound();
				if(atag == null && btag == null)
					return true;
				if(atag != btag && atag != null)
					if (a.getTagCompound().equals(b.getTagCompound()))
						return true;
			}
			return false;
		}
	}
}
