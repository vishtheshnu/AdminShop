package com.vnator.adminshop.packets;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.ConfigHandler;
import com.vnator.adminshop.capabilities.money.MoneyProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
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
			ItemStack transactionStack = ItemStack.EMPTY;
			if(message.toBuy){ //Buying
				String itemName;
				float price = 0;
				switch (message.category){
					case 0:
						itemName = ConfigHandler.Buyable_Items.category1Items[message.index];
						transactionStack = translateItemStack(itemName, message.quantity);
						price = ConfigHandler.Buyable_Items.category1Prices[message.index];
						break;
					case 1: itemName = ConfigHandler.Buyable_Items.category2Items[message.index];
						transactionStack = translateItemStack(itemName, message.quantity);
						price = ConfigHandler.Buyable_Items.category2Prices[message.index];
						break;
					case 2: itemName = ConfigHandler.Buyable_Items.category3Items[message.index];
						transactionStack = translateItemStack(itemName, message.quantity);
						price = ConfigHandler.Buyable_Items.category3Prices[message.index];
						break;
					case 3: itemName = ConfigHandler.Buyable_Items.category4Items[message.index];
						transactionStack = translateItemStack(itemName, message.quantity);
						price = ConfigHandler.Buyable_Items.category4Prices[message.index];
						break;
				}
				buyTransaction(ctx.getServerHandler().player, transactionStack, price);
			}else{ //Selling
				String itemName;
				float price = 0;
				switch (message.category){
					case 0:
						itemName = ConfigHandler.Sellable_Items.category1Items[message.index];
						transactionStack = translateItemStack(itemName, message.quantity);
						price = ConfigHandler.Sellable_Items.category1Prices[message.index];
						break;
					case 1: itemName = ConfigHandler.Sellable_Items.category2Items[message.index];
						transactionStack = translateItemStack(itemName, message.quantity);
						price = ConfigHandler.Sellable_Items.category2Prices[message.index];
						break;
					case 2: itemName = ConfigHandler.Sellable_Items.category3Items[message.index];
						transactionStack = translateItemStack(itemName, message.quantity);
						price = ConfigHandler.Sellable_Items.category3Prices[message.index];
						break;
					case 3: itemName = ConfigHandler.Sellable_Items.category4Items[message.index];
						transactionStack = translateItemStack(itemName, message.quantity);
						price = ConfigHandler.Sellable_Items.category4Prices[message.index];
						break;
				}
				sellTransaction(ctx.getServerHandler().player, transactionStack, price);
			}
			PacketHandler.INSTANCE.sendTo(new PacketUpdateMoney(
					ctx.getServerHandler().player.getCapability(MoneyProvider.MONEY_CAPABILITY, null).getMoney()
			), ctx.getServerHandler().player);
		}

		private ItemStack translateItemStack(String s, int quant) {
			String[] parts = s.split(":");
			if (parts.length == 2){
				return new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(s)), quant, 0);
			}
			else if(parts.length == 3){
				String name = parts[0]+":"+parts[1];
				int meta = 0;
				try {
					meta = Integer.parseInt(parts[2]);
				}catch (NumberFormatException e){
					AdminShop.logger.log(Level.ERROR, "Item string improperly formatted! "+s);
					return ItemStack.EMPTY;
				}
				return new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(name)), quant, meta);
			}
			AdminShop.logger.log(Level.ERROR, "Item string improperly formatted! "+s);
			return ItemStack.EMPTY;
		}

		private void buyTransaction(EntityPlayer player, ItemStack toBuy, float price){
			IItemHandler inventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			ItemStack returned = ItemHandlerHelper.insertItemStacked(inventory, toBuy, true);
			price = (toBuy.getCount() - returned.getCount())*price;
			boolean success = player.getCapability(MoneyProvider.MONEY_CAPABILITY, null)
					.withdraw(price);
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
			player.getCapability(MoneyProvider.MONEY_CAPABILITY, null).deposit(price*numSold);
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
				if(comp.getItem().equals(item.getItem())){ //Found items we can remove
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
	}
}
