package com.vnator.adminshop.packets;

import com.vnator.adminshop.blocks.autoShop.TileEntityAutoBuyer;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateAutoBuyer implements IMessage {

	BlockPos pos; //position of AutoBuyer to update
	int quantity;
	boolean isBuyingItem;
	boolean incrementRate;
	int toWrite; //bitwise comparator of what values to overwrite in AutoBuyer

	@Override
	public void fromBytes(ByteBuf byteBuf) {
		pos = new BlockPos(byteBuf.readInt(), byteBuf.readInt(), byteBuf.readInt());
		quantity = byteBuf.readInt();
		isBuyingItem = byteBuf.readBoolean();
		incrementRate = byteBuf.readBoolean();
		toWrite = byteBuf.readInt();
	}

	@Override
	public void toBytes(ByteBuf byteBuf) {
		byteBuf.writeInt(pos.getX());
		byteBuf.writeInt(pos.getY());
		byteBuf.writeInt(pos.getZ());
		byteBuf.writeInt(quantity);
		byteBuf.writeBoolean(isBuyingItem);
		byteBuf.writeBoolean(incrementRate);
		byteBuf.writeInt(toWrite);
	}

	public PacketUpdateAutoBuyer(BlockPos buyerPos, int quantity, boolean isBuyingItem, boolean incrementRate, int toWrite){
		this.pos = buyerPos;
		this.quantity = quantity;
		this.isBuyingItem = isBuyingItem;
		this.incrementRate = incrementRate;
		this.toWrite = toWrite;
	}

	public PacketUpdateAutoBuyer(){}

	public static class Handler implements IMessageHandler<PacketUpdateAutoBuyer, IMessage>{

		@Override
		public IMessage onMessage(PacketUpdateAutoBuyer message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(PacketUpdateAutoBuyer message, MessageContext ctx){
			TileEntity ent = ctx.getServerHandler().player.world.getTileEntity(message.pos);
			if(ent == null || !(ent instanceof TileEntityAutoBuyer)) return;
			TileEntityAutoBuyer buyer = (TileEntityAutoBuyer) ent;
			if((message.toWrite & 1) > 0) buyer.setQuantity(message.quantity);
			if((message.toWrite & 2) > 0) buyer.setIsBuyingItem(message.isBuyingItem);
			if((message.toWrite & 4) > 0) buyer.incrementRate();
			buyer.markDirty();
		}
	}
}
