package com.vnator.adminshop.packets;

import com.vnator.adminshop.blocks.shop.ShopStock;
import io.netty.buffer.ByteBuf;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSendSell implements IMessage{

	private String name;
	private int nameSize;
	private int quantity;
	private BlockPos pos;


	@Override
	public void fromBytes(ByteBuf buf) {
		quantity = buf.readInt();
		nameSize = buf.readInt();
		pos = BlockPos.fromLong(buf.readLong());
		byte [] nbites = new byte[nameSize];
		buf.readBytes(nbites);
		name = new String(nbites);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(quantity);
		byte [] nbites = name.getBytes();
		buf.writeInt(nbites.length);
		buf.writeLong(pos.toLong());
		buf.writeBytes(nbites);
	}

	public PacketSendSell(String name, int quantity, BlockPos pos){
		this.name = name;
		this.quantity = quantity;
		this.pos = pos;
	}

	public PacketSendSell(){}

	public static class Handler implements IMessageHandler<PacketSendSell, IMessage>{

		@Override
		public IMessage onMessage(PacketSendSell message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(PacketSendSell message, MessageContext ctx){
			float money = ShopStock.sellMap.get(message.name).getPrice()*message.quantity;
			//TileEntity ent = Minecraft.getMinecraft().world.getTileEntity(message.pos);
		}
	}
}
