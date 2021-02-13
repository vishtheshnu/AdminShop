package com.vnator.adminshop.packets;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.capabilities.BalanceAdapter;
import com.vnator.adminshop.items.CheckItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.logging.log4j.Level;

public class PacketWithdrawMoney implements IMessage{
	private float money;


	@Override
	public void fromBytes(ByteBuf buf) {
		money = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeFloat(money);
	}

	public PacketWithdrawMoney(float f){
		money = f;
	}

	public PacketWithdrawMoney(){}

	public static class Handler implements IMessageHandler<PacketWithdrawMoney, IMessage>{

		@Override
		public IMessage onMessage(PacketWithdrawMoney message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(PacketWithdrawMoney message, MessageContext ctx){
			EntityPlayerMP player = ctx.getServerHandler().player;

			//Create Check and see if there's enough room
			NBTTagCompound tag = new NBTTagCompound();
			NBTTagCompound displayTag = new NBTTagCompound();
			NBTTagList loreTag = new NBTTagList();

			tag.setFloat("value", message.money);
			tag.setTag("display", displayTag);
			displayTag.setTag("Name", new NBTTagString("Banker's Check"));
			displayTag.setTag("Lore", loreTag);
			loreTag.appendTag(new NBTTagString("Value: $"+message.money));

			ItemStack check = new ItemStack(Item.getByNameOrId("adminshop:check"), 1, 0);

			check.setTagCompound(tag);
			AdminShop.logger.log(Level.INFO, "Check created! NBT = "+check.getTagCompound().getFloat("value"));
			IItemHandler inv = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			ItemStack leftover = ItemHandlerHelper.insertItem(inv, check, true);
			if(!leftover.isEmpty())
				return;
			//Attempt withdrawl
			boolean withdrawSuccess = BalanceAdapter.withdraw(player, message.money);
			if(!withdrawSuccess)
				return;

			//Insert
			ItemHandlerHelper.insertItem(inv, check, false);
			PacketHandler.INSTANCE.sendTo(new PacketUpdateMoney(BalanceAdapter.getMoneyServer(player)), player);
		}
	}
}
