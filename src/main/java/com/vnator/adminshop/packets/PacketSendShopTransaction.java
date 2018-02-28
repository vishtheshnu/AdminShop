package com.vnator.adminshop.packets;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.ConfigHandler;
import com.vnator.adminshop.blocks.shop.ShopItemStack;
import com.vnator.adminshop.blocks.shop.ShopStock;
import com.vnator.adminshop.capabilities.BalanceAdapter;
import com.vnator.adminshop.capabilities.ledger.LedgerProvider;
import com.vnator.adminshop.capabilities.money.MoneyProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
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
			System.out.println("Handling transaction message!");
			//Get the item
			float price = message.toBuy ? ShopStock.buyItemPrices.get(message.category)[message.index] :
					ShopStock.sellItemPrices.get(message.category)[message.index];

			if(message.toBuy){
				ItemStack transactionStack = ItemStack.EMPTY;
				ItemStack tempItem = ShopStock.buyItems.get(message.category)[message.index];
				transactionStack = ItemHandlerHelper.copyStackWithSize(tempItem, message.quantity);//new ItemStack(tempItem.getItem(), message.quantity, tempItem.getMetadata(), tempItem.getTagCompound());
				buyTransaction(ctx.getServerHandler().player, transactionStack, price);
			}else{
				ShopItemStack stk = ShopStock.sellItems.get(message.category)[message.index];
				sellTransaction(ctx.getServerHandler().player, stk, message.quantity, price);
			}

			//Update client with new balance
			EntityPlayerMP player = ctx.getServerHandler().player;
			PacketHandler.INSTANCE.sendTo(new PacketUpdateMoney(
					BalanceAdapter.getMoneyServer(player)
			), player);
		}

		private void buyTransaction(EntityPlayer player, ItemStack toBuy, float price){
			IItemHandler inventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			ItemStack returned = ItemHandlerHelper.insertItemStacked(inventory, toBuy, true);
			price = (toBuy.getCount() - returned.getCount())*price;
			boolean success = BalanceAdapter.withdraw(player, price);
			//player.world.getCapability(LedgerProvider.LEDGER_CAPABILITY, null)
			//		.withdraw(player.getName(), price);
			if(success){
				//Send money into player inventory
				//ItemHandlerHelper.giveItemToPlayer(player, toBuy);
				ItemHandlerHelper.insertItemStacked(inventory, toBuy, false);
			}else{
				AdminShop.logger.log(Level.ERROR, "Not enough money to perform transaction!");
			}
		}

		private void sellTransaction(EntityPlayer player, ItemStack toSell, float price){
			//Count if there are enough items in the player's inventory to sell
			IItemHandler inventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			int numSold = removeItemsFromInventory(inventory, toSell);
			System.out.println("Removed "+numSold+" from inventory!");
			BalanceAdapter.deposit(player, price*numSold);
			//player.getCapability(MoneyProvider.MONEY_CAPABILITY, null).deposit(price*numSold);
		}

		private void sellTransaction(EntityPlayer player, ShopItemStack item, int quantity, float price){
			IItemHandler inventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			int numSold = removeItemsFromInventory(inventory, item, quantity);
			BalanceAdapter.deposit(player, price*numSold);
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
		private int removeItemsFromInventory(IItemHandler inv, ShopItemStack item, int quantity){
			int count = quantity;
			System.out.println("Removing items from inventory! # to remove: "+count);
			for(int i = 0; i < inv.getSlots(); i++){
				ItemStack comp = inv.getStackInSlot(i);
				if(item.itemEqual(comp)){ //Found items we can remove
					if(count > comp.getCount()){ //Remove entirety of this stack
						count -= comp.getCount();
						inv.extractItem(i, comp.getCount(), false);
					}else{ //Remove what is left of stack, return number removed
						inv.extractItem(i, count, false);
						return quantity;
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
